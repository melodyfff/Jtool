package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.lang.Prioritized;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 10:21
 */
@SuppressWarnings("unchecked")
public class ExtensionLoader<T> {
    //------------------------------------------------------------
    // Filed
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
    public static List<LoadingStrategy> getLoadingStrategies() {
        return Arrays.asList(strategies);
    }


    /** Adaptive实例缓存 创建Adaptive的时候使用 线程安全单例*/
    private final Holder<Object> cachedAdaptiveInstance = new Holder<>();
    /** createAdaptiveInstanceError保证在多线程情况下创建异常能被正常获取 */
    private volatile Throwable createAdaptiveInstanceError;

    /***/
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();


    /** SPI注解中的value默认值缓存 */
    private String cachedDefaultName;
    private final Class<?> type;
    /** 本身也是一个拓展点 */
    private final ExtensionFactory objectFactory;

    private ExtensionLoader(Class<?> type){
        this.type = type;

        // 获取自适应inject拓展类
        objectFactory = (ExtensionFactory.class == type) ?
                null
                :
                ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension();
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type){
        if (null == type){
            throw new IllegalArgumentException("Extension type == null");
        }
        // 判断是否是接口类型
        if (!type.isInterface()){
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }

        // 如果EXTENSION_LOADERS中没有该type,则以该type为key,新建一个ExtensionLoader(Class)为value，存入该map
        // 保证每个type对应的ExtensionLoader只被加载一次
        return (ExtensionLoader<T>) EXTENSION_LOADERS.computeIfAbsent(type, ExtensionLoader::new);
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
        // 缓存SPI注解的默认值
        cacheDefaultExtensionName();
        Map<String, Class<?>> extensionClasses = new HashMap<>();

        // TODO 不同加载策略加载

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
