package com.xinchen.tool.spi.extension.support;


import com.xinchen.tool.spi.extension.Activate;
import java.util.Comparator;

/**
 * OrderComparator for Activate
 */
public class ActivateComparator implements Comparator<Object> {

    public static final Comparator<Object> COMPARATOR = new ActivateComparator();

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


        ActivateInfo a1 = parseActivate(o1.getClass());
        ActivateInfo a2 = parseActivate(o2.getClass());


        // never return 0 even if n1 equals n2, otherwise, o1 and o2 will override each other in collection like HashSet
        return a1.order > a2.order ? 1 : -1;
    }


    private ActivateInfo parseActivate(Class<?> clazz) {
        ActivateInfo info = new ActivateInfo();
        if (clazz.isAnnotationPresent(Activate.class)) {
            Activate activate = clazz.getAnnotation(Activate.class);
            info.order = activate.order();
        }
        return info;
    }

    private static class ActivateInfo {
        private int order;
    }
}
