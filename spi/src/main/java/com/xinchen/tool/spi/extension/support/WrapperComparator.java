package com.xinchen.tool.spi.extension.support;

import com.xinchen.tool.spi.extension.Activate;

import java.util.Comparator;

/**
 *
 * OrderComparator
 *
 * createExtension的时候对cachedWrapperClasses中的的对象集合进行排序
 *
 * @author xinchen
 * @version 1.0
 * @date 30/10/2020 14:51
 */
public class WrapperComparator implements Comparator<Object> {
    /** Single */
    public static final Comparator<Object> COMPARATOR = new WrapperComparator();

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1.equals(o2)) {
            return 0;
        }

        Class clazz1 = (Class) o1;
        Class clazz2 = (Class) o2;

        OrderInfo a1 = parseOrder(clazz1);
        OrderInfo a2 = parseOrder(clazz2);

        // never return 0 even if n1 equals n2, otherwise, o1 and o2 will override each other in collection like HashSet
        return a1.order > a2.order ? 1:-1;
    }


    private OrderInfo parseOrder(Class<?> clazz) {
        OrderInfo info = new OrderInfo();
        if (clazz.isAnnotationPresent(Activate.class)){
            Activate activate = clazz.getAnnotation(Activate.class);
            info.order = activate.order();
        }
        return info;
    }

    private static class OrderInfo {
        private int order;
    }
}
