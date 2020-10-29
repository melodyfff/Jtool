package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.lang.Prioritized;
import com.xinchen.tool.spi.utlis.ClassUtils;
import com.xinchen.tool.spi.utlis.ConcurrentHashSet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

/**
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 10:21
 */
@SuppressWarnings("unchecked")
public class ExtensionLoader<T> {
    //------------------------------------------------------------
    // Static FIELD
    //------------------------------------------------------------

    /** 针对SPI注解中的value默认值提取的正则匹配,只允许一个默认值 */
    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");

    /** ExtensionLoader 全局缓存 */
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>(64);

    /** 加载服务策略 - 指定加载的目录以及优先级
        Load all {@link Prioritized prioritized} {@link LoadingStrategy Loading Strategies} via {@link ServiceLoader} */
    private static volatile LoadingStrategy[] strategies = loadLoadingStrategies();
    private static LoadingStrategy[] loadLoadingStrategies() {
        // JDK的SPI : 从META-INF/services/中目录中加载拓展SPI的加载目录
        return StreamSupport.stream(ServiceLoader.load(LoadingStrategy.class).spliterator(), false)
                .sorted()
                .toArray(LoadingStrategy[]::new);
    }

    //------------------------------------------------------------
    // FIELD
    //------------------------------------------------------------

    /** 缓存从Resource URL 中加载的类信息 ，加载的时候会被加上synchronized */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    /** 当loadClass的时候缓存@Activate标记的类的注解，格式为 key - Activate.class注解 */
    private final Map<String, Object> cachedActivates = new ConcurrentHashMap<>();

    /** 当loadClass的时候根据是否有@Adaptive注解来进行临时缓存 */
    private volatile Class<?> cachedAdaptiveClass = null;
    /** Adaptive实例缓存 创建Adaptive的时候使用 线程安全单例*/
    private final Holder<Object> cachedAdaptiveInstance = new Holder<>();
    /** createAdaptiveInstanceError保证在多线程情况下创建异常能被正常获取 */
    private volatile Throwable createAdaptiveInstanceError;

    /** 当loadClass的时候，根据构造函数中的参数是否包含本类中的type判断为一个Wrapper类 */
    private Set<Class<?>> cachedWrapperClasses;

    /** 加载class的时候 loadResource() || loadClass() ,产生的异常缓存信息 */
    private Map<String, IllegalStateException> exceptions = new ConcurrentHashMap<>();


    /** SPI注解中的value默认值缓存 */
    private String cachedDefaultName;
    private final Class<?> type;
    /** 本身也是一个拓展点 */
    private final ExtensionFactory objectFactory;
    /** 当loadClass的时候多个拓展的key值缓存*/
    private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<>();

    private ExtensionLoader(Class<?> type){
        this.type = type;

        // 获取自适应inject拓展类
        objectFactory = (ExtensionFactory.class == type) ?
                null
                :
                ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension();
    }

    //------------------------------------------------------------
    // Static Method
    //------------------------------------------------------------

    /** 获取加载策略 */
    public static List<LoadingStrategy> getLoadingStrategies() {
        return Arrays.asList(strategies);
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type){
        if (null == type){
            throw new IllegalArgumentException("Extension type == null");
        }
        // 必须是interface
        if (!type.isInterface()){
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }

        // 如果EXTENSION_LOADERS中没有该type,则以该type为key,新建一个ExtensionLoader(Class)为value，存入该map
        // 保证每个type对应的ExtensionLoader只被加载一次
        return (ExtensionLoader<T>) EXTENSION_LOADERS.computeIfAbsent(type, ExtensionLoader::new);
    }

    private static ClassLoader findClassLoder(){
        // 首先从当前线程中获取当前线程的类加载器 Thread.currentThread().getContextClassLoader()
        return ClassUtils.getClassLoader(ExtensionLoader.class);
    }

    public T getAdaptiveExtension(){
        Object instance = cachedAdaptiveInstance.get();
        if (null == instance){
            if (null != createAdaptiveInstanceError){
                throw new IllegalArgumentException("Failed to create adaptive instance: " + createAdaptiveInstanceError.toString(), createAdaptiveInstanceError);
            }

            // 确保cachedAdaptiveInstance是一个线程安全单例
            synchronized (cachedAdaptiveInstance){
                instance = cachedAdaptiveInstance.get();
                if (null == instance){
                    try {
                        // 尝试创建拓展
                        instance = createAdaptiveExtension();
                        cachedAdaptiveInstance.set(instance);
                    } catch (Throwable t){
                        // cache error
                        createAdaptiveInstanceError = t;
                        throw new IllegalStateException("Failed to create adaptive instance: " + t.toString(), t);
                    }
                }
            }
        }
        return (T) instance;
    }


    private T createAdaptiveExtension(){
        try {
            // 尝试通过注入IOC的方式构造拓展
            return injectExtension((T)getAdaptiveExtensionClass().newInstance());
        } catch (Exception e){
            throw new IllegalStateException("Can't create adaptive extension " + type + ", cause: " + e.getMessage(), e);
        }
    }

    private Class<?> getAdaptiveExtensionClass(){
        return null;
    }

    private T injectExtension(T instance){
        return null;
    }

    private Map<String,Class<?>> getExtensionClasses(){
        Map<String, Class<?>> classes = cachedClasses.get();
        if (null == classes){
            synchronized (cachedClasses){
                classes = cachedClasses.get();
                if (null == classes){
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 加载拓展类
     * synchronized in getExtensionClasses
     */
    private Map<String,Class<?>> loadExtensionClasses(){
        // 缓存SPI注解的默认值,如果存在的话
        cacheDefaultExtensionName();
        Map<String, Class<?>> extensionClasses = new HashMap<>();

        // 这里的加载很重要：
        // 根据加载策略分别加载 内部 - 常规 - JDK的拓展 ，策略的不同影响加载的缓存的类 ， 具体可查看loadClass()
        // 整个加载过程由于在上一步中加了 synchronized ，所以是线程安全的
        // extensionClasses 最终为 ->  Holder<Map<String, Class<?>>> cachedClasses 缓存
        for (LoadingStrategy strategy : strategies) {
            loadDirectory(
                    // 拓展类的缓存Map
                    extensionClasses,
                    // 策略的资源目录，如： META-INF/services , META-INF/app
                    strategy.directory(),
                    // 如：META-INF/services 下 com.xinchen.tool.spi.extension.LoadingStrategy
                    type.getName(),
                    // 是否更偏向于ExtensionLoader.class当前的类加载器，默认是获取当前线程的类加载器
                    strategy.preferExtensionClassLoader(),
                    // 当key相同时，是否可以覆盖缓存中的。这里主要是保证Single
                    strategy.overridden(),
                    // 排除加载的包
                    strategy.excludedPackages()
            );
            // loadDirectory(extensionClasses, strategy.directory(), type.getName().replace("org.apache", "com.alibaba"), strategy.preferExtensionClassLoader(), strategy.overridden(), strategy.excludedPackages());
        }

        return extensionClasses;
    }

    /**
     *
     * 缓存{@link SPI}标记的类type 中的value
     *
     * extract and cache default extension name if exists
     */
    private void cacheDefaultExtensionName(){
        // 获取SPI注解
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        if (null != defaultAnnotation){
            // value default is ""
            String value = defaultAnnotation.value();
            if ((value= value.trim()).length()>0){
                String[] names = NAME_SEPARATOR.split(value);
                // 只允许一个默认value值
                if (names.length > 1){
                    throw new IllegalStateException("More than 1 default extension name on extension " + type.getName()
                            + ": " + Arrays.toString(names));
                }
                if (names.length == 1){
                    // 缓存SPI注解上的默认值
                    cachedDefaultName = names[0];
                }
            }
        }
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir, String type,
                               boolean extensionLoaderClassLoaderFirst, boolean overridden, String... excludedPackages) {
        // META-INF/services/${typeName}
        // META-INF/app/${typeName}
        // META-INF/app/internal/${typeName}
        String fileName = dir + type;

        try {
            Enumeration<java.net.URL> urls = null;
            // 这里的类加载器首先从当前线程的中获取 Thread.currentThread().getContextClassLoader()
            ClassLoader classLoader = findClassLoder();

            // try to load from ExtensionLoader's ClassLoader first
            if (extensionLoaderClassLoaderFirst){
                ClassLoader extensionLoaderClassLoader = ExtensionLoader.class.getClassLoader();
                if (ClassLoader.getSystemClassLoader() != extensionLoaderClassLoader){
                    urls = extensionLoaderClassLoader.getResources(fileName);
                }
            }

            if (null == urls || !urls.hasMoreElements()){
                // 尝试从当前线程中加载，如果没获取到当前线程的类加载器，则从系统类加载器中加载资源
                if (null != classLoader){
                    urls = classLoader.getResources(fileName);
                } else {
                    urls = ClassLoader.getSystemClassLoader().getResources(fileName);
                }
            }

            if (null != urls){
                while (urls.hasMoreElements()){
                    final java.net.URL resourceURL = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceURL, overridden, excludedPackages);
                }
            }


        } catch (Throwable t){
            // TODO log
//            logger.error("Exception occurred when loading extension class (interface: " +
//                    type + ", description file: " + fileName + ").", t);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader,
                              java.net.URL resourceURL, boolean overridden, String... excludedPackages) {
        try {
            // 读取resource中的内容
            // 一般为 key=com.xinchen.tool.spi.extension.ExtensionLoader # 可选注释信息
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))){
                String line;
                while ((line = reader.readLine()) != null){
                    final int ci = line.indexOf('#');
                    if (ci >= 0){
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (line.length() > 0){
                        try {
                            String name = null;
                            final int i = line.indexOf('=');
                            if (i > 0){
                                // 提取 key
                                name = line.substring(0, i).trim();
                                line = line.substring(i + 1).trim();
                            }
                            // 检查是否存在value以及根据当前的LoadStrategy判断是否需要排除加载
                            if (line.length() > 0 && !isExcluded(line,excludedPackages)){
                                loadClass(
                                        // 当前类中缓存的 Holder<Map<String, Class<?>>> cachedClasses
                                        extensionClasses,
                                        // resource url
                                        resourceURL,
                                        // 从给定类加载器中加载类，并初始化
                                        Class.forName(line, true, classLoader),
                                        // 提取的key值，用于cachedClasses的key
                                        name,
                                        // 由加载策略决定是否可以覆盖cachedAdaptiveClass
                                        overridden
                                );
                            }

                        } catch (Throwable t){
                            IllegalStateException e = new IllegalStateException("Failed to load extension class (interface: " + type + ", class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);
                            exceptions.put(line, e);
                        }
                    }

                }
            }
        } catch (Throwable t){
            // TODO logger
//            logger.error("Exception occurred when loading extension class (interface: " +
//                    type + ", class file: " + resourceURL + ") in " + resourceURL, t);
        }
    }

    private boolean isExcluded(String className, String... excludedPackages){
        if (null != excludedPackages){
            for (String excludedPackage : excludedPackages) {
                if (className.startsWith(excludedPackage+".")){
                    return true;
                }
            }
        }
        return false;
    }

    private void loadClass(Map<String, Class<?>> extensionClasses, java.net.URL resourceURL, Class<?> clazz, String name,
                           boolean overridden) throws NoSuchMethodException {
        // class必须是type接口的子类
        if (!type.isAssignableFrom(clazz)){
            throw new IllegalStateException("Error occurred when loading extension class (interface: " +
                    type + ", class line: " + clazz.getName() + "), class "
                    + clazz.getName() + " is not subtype of interface.");
        }

        // 当@Adaptive存在于类上时
        if (clazz.isAnnotationPresent(Adaptive.class)){
            // 缓存到 cachedAdaptiveClass中，由策略决定是否可以覆盖
            cacheAdaptiveClass(clazz, overridden);
        } else if (isWrapperClass(clazz)){
            // 当是一个wrapper的class时
            // 判断标准为构造函数Constructor中的参数为当前type
            cacheWrapperClass(clazz);
        } else {
            // 获取默认的无参构造函数？ 这步应该是确保至少存在一个无参构造函数，以便后续反射创建步骤能正常进行，如果没有则直接抛出异常 NoSuchMethodException
            clazz.getConstructor();
            // 如果没有指定key ， 例如直接是：com.xinchen.tool.spi.extension.LoadingStrategyAppInternal
            if (null == name || name.isEmpty()){
                // 被加载的类名
                name = clazz.getSimpleName();
                // type: Service , Class: AService ，拼接后为: A
                if (name.endsWith(type.getSimpleName())) {
                    name = name.substring(0, name.length() - type.getSimpleName().length());
                }
                if (name.length() == 0) {
                    throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config " + resourceURL);
                }
                name = name.toLowerCase();
            }

            String[] names = NAME_SEPARATOR.split(name);
            if (null!=names && names.length > 0){
                // 这里只会对第一个进行存储  -> Map<String, Object> cachedActivates
                cacheActivateClass(clazz, names[0]);

                for (String n:names){
                    // 缓存class和key名对应 -> ConcurrentMap<Class<?>, String> cachedNames
                    cacheName(clazz, n);
                    // 更新extensionClasses ->  Holder<Map<String, Class<?>>> cachedClasses
                    saveInExtensionClass(extensionClasses,clazz,n,overridden);
                }
            }

        }

    }

    /**
     * cache name
     */
    private void cacheName(Class<?> clazz, String name) {
        if (!cachedNames.containsKey(clazz)) {
            cachedNames.put(clazz, name);
        }
    }

    /**
     * put clazz in extensionClasses
     */
    private void saveInExtensionClass(Map<String, Class<?>> extensionClasses, Class<?> clazz, String name, boolean overridden) {
        Class<?> c = extensionClasses.get(name);
        if (null == c || overridden){
            extensionClasses.put(name, clazz);
        } else if (clazz != c){
            // 当发现 Holder<Map<String, Class<?>>> cachedClasses中的缓存类和类加载中的不一致时，表示重复加载，不是一个Single
            String duplicateMsg = "Duplicate extension " + type.getName() + " name " + name + " on " + c.getName() + " and " + clazz.getName();
            // TODO logger
//            logger.error(duplicateMsg);
            throw new IllegalStateException(duplicateMsg);
        }

    }

    /**
     * cache Activate class which is annotated with <code>Activate</code>
     */
    private void cacheActivateClass(Class<?> clazz, String name) {
        Activate activate = clazz.getAnnotation(Activate.class);
        if (activate != null){
            // 存储到cachedActivates中
            cachedActivates.put(name, activate);
        }
    }

    /**
     * cache Adaptive class which is annotated with <code>Adaptive</code>
     * @param clazz Class
     * @param overridden 由加载策略决定是否可以覆盖 cachedAdaptiveClass
     */
    private void cacheAdaptiveClass(Class<?> clazz, boolean overridden) {
        if (null == cachedAdaptiveClass || overridden){
            cachedAdaptiveClass = clazz;
        } else if (!cachedAdaptiveClass.equals(clazz)){
            throw new IllegalStateException("More than 1 adaptive class found: "
                    + cachedAdaptiveClass.getName()
                    + ", " + clazz.getName());
        }

    }

    /**
     * cache wrapper class
     * <p>
     * like: ProtocolFilterWrapper, ProtocolListenerWrapper
     */
    private void cacheWrapperClass(Class<?> clazz) {
        if (cachedWrapperClasses == null){
            // 这里自定义的ConcurrentHashSet本质为一个ConcurrentMap，只是提供记录插入顺序
            cachedWrapperClasses = new ConcurrentHashSet<>();
        }
        cachedWrapperClasses.add(clazz);
    }

    /**
     * test if clazz is a wrapper class
     * <p>
     * which has Constructor with given class type as its only argument
     *
     * @param clazz Class
     * @return boolean
     */
    private boolean isWrapperClass(Class<?> clazz) {
        try {
            clazz.getConstructor(type);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    //------------------------------------------------------------
    // Static Class
    //------------------------------------------------------------

    /**
     * Helper Class for hold a value.
     */
    static class Holder<T>{
        private volatile T value;

        T get() {
            return value;
        }

        void set(T value) {
            this.value = value;
        }
    }
}
