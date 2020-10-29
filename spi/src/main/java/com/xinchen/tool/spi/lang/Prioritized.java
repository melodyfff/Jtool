package com.xinchen.tool.spi.lang;

import java.util.Comparator;

/**
 *
 * 标注优先级，可被需要排序的对象继承实现
 *
 * 数字越小优先级越高
 *
 * {@code Prioritized} interface can be implemented by objects that
 * should be sorted, for example the tasks in executable queue.
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 15:11
 */
public interface Prioritized extends Comparable<Prioritized>{

    /**
     * The {@link Comparator} of {@link Prioritized}
     */
    Comparator<Object> COMPARATOR = (one, two) -> {
        boolean b1 = one instanceof Prioritized;
        boolean b2 = two instanceof Prioritized;
        if (b1 && !b2) {        // one is Prioritized, two is not
            return -1;
        } else if (b2 && !b1) { // two is Prioritized, one is not
            return 1;
        } else if (b1 && b2) {  //  one and two both are Prioritized
            return ((Prioritized) one).compareTo((Prioritized) two);
        } else {                // no different
            return 0;
        }
    };

    /**
     * The maximum priority
     */
    int MAX_PRIORITY = Integer.MIN_VALUE;

    /**
     * The minimum priority
     */
    int MIN_PRIORITY = Integer.MAX_VALUE;

    /**
     * Normal Priority
     */
    int NORMAL_PRIORITY = 0;

    /**
     * Get the priority
     *
     * 子类通过覆盖此方法设置优先级
     *
     * @return the default is {@link #NORMAL_PRIORITY one}
     */
    default int getPriority() {
        return NORMAL_PRIORITY;
    }

    @Override
    default int compareTo(Prioritized that){
        return Integer.compare(this.getPriority(), that.getPriority());
    }
}
