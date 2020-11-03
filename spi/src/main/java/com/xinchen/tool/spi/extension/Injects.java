package com.xinchen.tool.spi.extension;

import com.xinchen.tool.spi.logger.Logger;
import com.xinchen.tool.spi.logger.LoggerFactory;
import com.xinchen.tool.spi.utils.ReflectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * IOC依赖注入
 *
 * @author xinchen
 * @version 1.0
 * @date 30/10/2020 13:34
 */
public class Injects {

    private static final Logger log = LoggerFactory.getLogger(Injects.class);

    public static <T> T injectExtension(ExtensionFactory objectFactory,T instance,Class<?> type){
        //
        if (null == objectFactory){
            return instance;
        }

        try {
            // 寻找setter
            for (Method method : instance.getClass().getMethods()) {
                if (!isSetter(method)){
                    continue;
                }

                // 如果有标记不进行依赖注入则跳过
                if (null!=method.getAnnotation(DisableInject.class)){
                    continue;
                }

                // setter通常只为一个参数
                Class<?> pt = method.getParameterTypes()[0];
                // 判断参数是否是java原生类型
                if (ReflectUtils.isPrimitive(pt)){
                    continue;
                }

                try {
                    String property = getSetterProperty(method);
                    // 从ExtensionFactory中获取依赖对象 - 第三方依赖IOC Spring Guice等
                    final Object object = objectFactory.getExtension(pt, property);
                    if (null != object){
                        // 调用setter注入
                        method.invoke(instance, object);
                    }

                } catch (Exception e){
                    log.error("Failed to inject via method " + method.getName() + " of interface " + type.getName() + ": " + e.getMessage(), e);
                }


            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return instance;
    }

    /**
     * return true if and only if:
     * <p>
     * 1, public
     * <p>
     * 2, name starts with "set"
     * <p>
     * 3, only has one parameter
     */
    private static boolean isSetter(Method method){
        // 判断是否是setter
        return method.getName().startsWith("set") && method.getParameters().length == 1 && Modifier.isPublic(method.getModifiers());
    }

    /**
     * get properties name for setter, for instance: setVersion, return "version"
     * <p>
     * return "", if setter name with length less than 3
     */
    private static String getSetterProperty(Method method) {
        return method.getName().length() > 3 ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) : "";
    }
}
