package com.xinchen.tool.spi.lang;


import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.junit.Assert.assertEquals;

/**
 * {@link Prioritized} Test
 *
 * @author xinchen
 * @version 1.0
 * @date 29/10/2020 15:26
 */
public class PrioritizedTest {
    @Test
    public void testConstants() {
        assertEquals(Integer.MAX_VALUE, Prioritized.MIN_PRIORITY);
        assertEquals(Integer.MIN_VALUE, Prioritized.MAX_PRIORITY);
    }

    @Test
    public void testGetPriority() {
        // 默认返回0
        assertEquals(Prioritized.NORMAL_PRIORITY, new Prioritized() {}.getPriority());
    }

    @Test
    public void testComparator() {
        // Comparator测试
        List<Object> list = new LinkedList<>();

        // All Prioritized
        list.add(of(1));
        list.add(of(2));
        list.add(of(3));

        List<Object> copy = new LinkedList<>(list);

        sort(list, Prioritized.COMPARATOR);
        // 1,2,3
        assertEquals(copy, list);


        // MIX non-Prioritized and Prioritized
        list.clear();

        list.add(1);
        list.add(of(2));
        list.add(of(1));

        sort(list, Prioritized.COMPARATOR);

        copy = asList(of(1), of(2), 1);

        // PrioritizedValue(1),PrioritizedValue(2),1
        assertEquals(copy, list);

        // All non-Prioritized
        list.clear();
        list.add(1);
        list.add(2);
        list.add(3);

        sort(list, Prioritized.COMPARATOR);

        copy = asList(1, 2, 3);

        assertEquals(copy, list);
    }


    static PrioritizedValue of(int value) {
        return new PrioritizedValue(value);
    }



    static class PrioritizedValue implements Prioritized {

        private final int value;

        private PrioritizedValue(int value) {
            this.value = value;
        }

        public int getPriority() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PrioritizedValue)) return false;
            PrioritizedValue that = (PrioritizedValue) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}