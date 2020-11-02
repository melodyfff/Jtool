package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.context.Lifecycle;
import com.xinchen.tool.spi.extension.support.WrapperComparator;
import com.xinchen.tool.spi.lang.Prioritized;
import com.xinchen.tool.spi.logger.Logger;
import com.xinchen.tool.spi.logger.LoggerFactory;
import com.xinchen.tool.spi.utlis.ArrayUtils;
import com.xinchen.tool.spi.utlis.ClassUtils;
import com.xinchen.tool.spi.utlis.CollectionUtils;
import com.xinchen.tool.spi.utlis.ConcurrentHashSet;
import com.xinchen.tool.spi.utlis.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
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



    private static final Logger log = LoggerFactory.getLogger(ExtensionLoader.class);

    /** 针对SPI注解中的value默认值提取的正则匹配,只允许一个默认值 */
    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");

    /** ExtensionLoader 全局缓存 */
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>(64);
    /** 初始化完成的INSTANCES 全局缓存 */
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>(64);


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

    /** 缓存从Resource URL 中加载的类信息（含有@Adaptive注解的类和被Wrapper的类不会存在这里，会在其对应的缓存中） ，加载的时候会被加上synchronized */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    /** 当loadClass的时候缓存@Activate标记的类的注解，格式为 key - Activate.class注解 */
    private final Map<String, Object> cachedActivates = new ConcurrentHashMap<>();
    /** 当loadClass的时候根据是否有@Adaptive注解来进行临时缓存 */
    private volatile Class<?> cachedAdaptiveClass = null;

    /**  缓存已经加载的实例 */
    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

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

        if (!(type.isAnnotationPresent(SPI.class))) {
            throw new IllegalArgumentException("Extension type (" + type +
                    ") is not an extension, because it is NOT annotated with @" + SPI.class.getSimpleName() + "!");
        }

        // 如果EXTENSION_LOADERS中没有该type,则以该type为key,新建一个ExtensionLoader(Class)为value，存入该map
        // 保证每个type对应的ExtensionLoader只被加载一次
        return (ExtensionLoader<T>) EXTENSION_LOADERS.computeIfAbsent(type, ExtensionLoader::new);
    }

    private static ClassLoader findClassLoader(){
        // 首先从当前线程中获取当前线程的类加载器 Thread.currentThread().getContextClassLoader()
        return ClassUtils.getClassLoader(ExtensionLoader.class);
    }

    public T getExtension(String name){
        return getExtension(name, true);
    }

    public T getExtension(String name,boolean wrap){
        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Extension name == null");
        }

        if ("true".equals(name)){
            return getDefaultExtension();
        }

        // 从cachedInstances Map缓存中获取实例，此时可能并未初始化，只是一个new Holder<>
        final Holder<Object> holder = getOrCreateHolder(name);

        Object instance = holder.get();

        // 实例是否已经初始化，未初始化则进行初始化
        if (null == instance){
            synchronized (holder){
                instance = holder.get();
                if(null == instance){
                    // 创建加载的拓展类的实例
                    instance = createExtension(name, wrap);
                    holder.set(instance);
                }
            }
        }

        return (T) instance;
    }

    public Set<String> getSupportedExtensions(){
        // LoadClass()后的缓存cachedClasses
        Map<String, Class<?>> extensionClasses = getExtensionClasses();
        // 返回name的Set集合
        return Collections.unmodifiableSet(new TreeSet<>(extensionClasses.keySet()));
    }

    public Set<T> getSupportedExtensionInstances(){
        List<T> instances = new LinkedList<>();
        final Set<String> supportedExtensions = getSupportedExtensions();
        if (CollectionUtils.isNotEmpty(supportedExtensions)){
            for (String name : supportedExtensions) {
                instances.add(getExtension(name));
            }
        }
        // sort the Prioritized instances
        instances.sort(Prioritized.COMPARATOR);
        return new LinkedHashSet<>(instances);
    }

    /**
     * Get the extension by specified name if found, or {@link #getDefaultExtension() returns the default one}
     *
     * @param name the name of extension
     * @return non-null
     */
    public T getOrDefaultExtension(String name){
        return containsExtension(name) ? getExtension(name) : getDefaultExtension();
    }

    /**
     * Return default extension, return <code>null</code> if it's not configured.
     */
    public T getDefaultExtension(){
        // 确保已经加载，没有则初始化 - 这里的加载会把name缓存到cachedDefaultName中
        getExtensionClasses();

        // 这里的true?
        if (StringUtils.isBlank(cachedDefaultName) || "true".equals(cachedDefaultName)){
            return null;
        }

        return getExtension(cachedDefaultName);
    }

    /**
     * Return default extension name, return <code>null</code> if not configured.
     */
    public String getDefaultExtensionName() {
        getExtensionClasses();
        return cachedDefaultName;
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
                        // 1.尝试创建拓展 - 扫描 /META-INF/service  、/META-INF/app  、/META-INF/app/internal ...找寻该type.getName()的信息
                        // 2.在loadClass()之后，cachedAdaptiveClass会有@Adaptive标记的类信息
                        // 3.如果cachedAdaptiveClass==null则在createAdaptiveExtension()中根据 cachedDefaultName 动态生成class类信息，注意： 这里生成的类并不会加入到cachedAdaptiveClass缓存中去
                        // 4.在createAdaptiveExtension()中完成拓展类IOC依赖setter填充。
                        // 5.存入cachedAdaptiveInstance缓存中
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

    private Class<?> getExtensionClass(String name){
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Extension name == null");
        }
        // 从cachedClasses中获取
        return getExtensionClasses().get(name);
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



    //------------------------------------------------------------
    // 创建扩展类相关 -
    //------------------------------------------------------------

    /** synchronized in getExtension */
    private T createExtension(String name, boolean wrap){
        // 从cachedClasses中获取加载的类信息
        Class<?> clazz = getExtensionClasses().get(name);

        // getExtensionClasses 会去加载需要拓展的Class信息，如果缓存cachedClasses中没有，则可能在加载该Class的时候发生了错误
        // Map<String, IllegalStateException> exceptions 中保存了加载Class过程中发生的错误，或者是该name没有对应的拓展类
        if (null == clazz){
            // 查询是否是创建异常抛出IllegalStateException异常
            throw findException(name);
        }

        try {
            T instance = (T) EXTENSION_INSTANCES.get(clazz);

            // 利用反射初始化该类，并存入全局实例缓存EXTENSION_INSTANCES
            if (instance == null){
                // cache instanceed instance
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }

            // IOC 注入依赖
            Injects.injectExtension(objectFactory,instance,type);

            // wrap对象处理 ， 如果cachedWrapperClasses存在，则返回的instance为Wrapped对象
            if (wrap){

                // cachedWrapperClasses在类加载的时候更新
                if (null != cachedWrapperClasses){
                    List<Class<?>> wrapperClassesList = new ArrayList<>(cachedWrapperClasses);
                    // 对Class信息进行排序,这里是从大到小
                    wrapperClassesList.sort(WrapperComparator.COMPARATOR);
                    // 进行倒叙排序，越小的优先级越高
                    Collections.reverse(wrapperClassesList);


                    for (Class<?> wrapperClass : wrapperClassesList) {
                        // 检查是否有@Wrapper注解
                        Wrapper wrapper = wrapperClass.getAnnotation(Wrapper.class);
                        if (null == wrapper || (ArrayUtils.contains(wrapper.matches(),name) &&  !ArrayUtils.contains(wrapper.mismatches(), name) )){
                            // 根据wrapperClass反射初始化instance,再通过ioc setter依赖
                            // 这里可能有多个，如果需要选择请使用@Wrapper排除选择
                            instance = (T) Injects.injectExtension(objectFactory, wrapperClass.getConstructor(type).newInstance(instance) ,type);
                        }

                    }
                }

            }


            // 管理组件的生命周期 lifecycle.initialize
            initExtension(instance);

            return instance;
        } catch (Throwable t){
            throw new IllegalStateException("Extension instance (name: " + name + ", class: " +
                    type + ") couldn't be instantiated: " + t.getMessage(), t);
        }
    }

    private T createAdaptiveExtension(){
        try {
            // 尝试通过注入IOC的方式构造拓展
            return Injects.injectExtension(objectFactory,(T)getAdaptiveExtensionClass().newInstance() ,type);
        } catch (Exception e){
            throw new IllegalStateException("Can't create adaptive extension " + type + ", cause: " + e.getMessage(), e);
        }
    }

    private Class<?> getAdaptiveExtensionClass(){
        // 确保类加载到缓存cachedAdaptiveClass, 在loadClass()中处理
        getExtensionClasses();

        // 缓存中存在则说明该type下存在Class上有@Adaptive标记的类
        if(cachedAdaptiveClass != null){
            return cachedAdaptiveClass;
        }

        // 没存在则可能没在/META-INF/app/internal 里面配置相关的拓展信息，尝试生成Class代码
        return cachedAdaptiveClass = createAdaptiveExtensionClass();
    }

    /**
     * 创建拓展适配器类
     *
     * <p>
     * 前提：
     * 1. 必须有SPI的注解
     * 2. 被SPI声明的接口中至少一个方法有Adaptive注解。
     * </p>
     * @return
     */
    private Class<?> createAdaptiveExtensionClass() {
        // 生成 Code
        String code = new AdaptiveClassCodeGenerator(type, cachedDefaultName).generate();

        // 优先选择当前线程的ClassLoader
        ClassLoader classLoader = findClassLoader();

        // 获取内置编译器生成Class
        com.xinchen.tool.spi.compiler.Compiler compiler = ExtensionLoader.getExtensionLoader(com.xinchen.tool.spi.compiler.Compiler.class).getAdaptiveExtension();
        return compiler.compile(code, classLoader);
    }

    /** 从cachedInstances中获取实例，此时可能并未初始化，只是一个new Holder<> */
    private Holder<Object> getOrCreateHolder(String name){
        // if cachedInstances.get(name) == null ? putIfAbsent(name, new Holder<>())
        return cachedInstances.computeIfAbsent(name, (key) -> new Holder<>());
    }

    private IllegalStateException findException(String name){
        final Set<Map.Entry<String, IllegalStateException>> entries = exceptions.entrySet();
        // exceptions ,存储加载拓展类时候发生的错误信息
        for (Map.Entry<String, IllegalStateException> entry : entries) {
            if (entry.getKey().equalsIgnoreCase(name)){
                return entry.getValue();
            }
        }
        // 如果不存在加载Class错误则该name对应的拓展类不一样
        StringBuilder buf = new StringBuilder("No such extension " + type.getName() + " by name " + name);

        // 拼接可能产生错误的原因
        int i = 1;
        for (Map.Entry<String, IllegalStateException> entry : entries) {
            if (1 == i){
                buf.append(", possible causes: ");
            }

            buf.append("\r\n(");
            buf.append(i++);
            buf.append(") ");
            buf.append(entry.getKey());
            buf.append(":\r\n");
            buf.append(StringUtils.toString(entry.getValue()));
        }

        return new IllegalStateException(buf.toString());
    }

    /** App组件生命周期初始化 */
    private void initExtension(T instance){
        if (instance instanceof Lifecycle){
            Lifecycle lifecycle = (Lifecycle) instance;
            lifecycle.initialize();
        }
    }

    private boolean containsExtension(String name){
        // 从缓存中获取是否存在该拓展类Class
        return getExtensionClasses().containsKey(name);
    }

    //------------------------------------------------------------
    // 加载扩展类相关 - 资源定位、策略选择、加载类并缓存 整个过程线程安全
    //------------------------------------------------------------

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

    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir, String type, boolean extensionLoaderClassLoaderFirst, boolean overridden, String... excludedPackages) {
        // META-INF/services/${typeName}
        // META-INF/app/${typeName}
        // META-INF/app/internal/${typeName}
        String fileName = dir + type;

        try {
            Enumeration<java.net.URL> urls = null;
            // 这里的类加载器首先从当前线程的中获取 Thread.currentThread().getContextClassLoader()
            ClassLoader classLoader = findClassLoader();

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
            log.error("Exception occurred when loading extension class (interface: " +
                    type + ", description file: " + fileName + ").", t);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, java.net.URL resourceURL, boolean overridden, String... excludedPackages) {
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
                            // 缓存错误信息，确保其他资源加载正常进行
                            // 当getExtension() -> createExtension() 时会发现class为空，此时从findException（）获取错误信息
                            exceptions.put(line, e);
                        }
                    }

                }
            }
        } catch (Throwable t){
            log.error("Exception occurred when loading extension class (interface: " +
                    type + ", class file: " + resourceURL + ") in " + resourceURL, t);
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

    private void loadClass(Map<String, Class<?>> extensionClasses, java.net.URL resourceURL, Class<?> clazz, String name, boolean overridden) throws NoSuchMethodException {
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
            log.error(duplicateMsg);
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
    // Static Class - Hold Value
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
