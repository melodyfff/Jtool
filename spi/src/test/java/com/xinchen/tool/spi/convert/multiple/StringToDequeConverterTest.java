package com.xinchen.tool.spi.convert.multiple;

import com.xinchen.tool.spi.utils.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TransferQueue;

import static com.xinchen.tool.spi.extension.ExtensionLoader.getExtensionLoader;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link StringToDequeConverter} Test
 *
 */
public class StringToDequeConverterTest {

    private MultiValueConverter converter;

    @BeforeEach
    public void init() {
        converter = getExtensionLoader(MultiValueConverter.class).getExtension("string-to-deque");
    }

    @Test
    public void testAccept() {

        assertFalse(converter.accept(String.class, Collection.class));

        assertFalse(converter.accept(String.class, List.class));
        assertFalse(converter.accept(String.class, AbstractList.class));
        assertTrue(converter.accept(String.class, LinkedList.class));
        assertFalse(converter.accept(String.class, ArrayList.class));

        assertFalse(converter.accept(String.class, Queue.class));
        assertFalse(converter.accept(String.class, BlockingQueue.class));
        assertFalse(converter.accept(String.class, TransferQueue.class));
        assertTrue(converter.accept(String.class, Deque.class));
        assertTrue(converter.accept(String.class, BlockingDeque.class));

        assertFalse(converter.accept(String.class, Set.class));
        assertFalse(converter.accept(String.class, SortedSet.class));
        assertFalse(converter.accept(String.class, NavigableSet.class));
        assertFalse(converter.accept(String.class, TreeSet.class));
        assertFalse(converter.accept(String.class, ConcurrentSkipListSet.class));

        assertFalse(converter.accept(null, char[].class));
        assertFalse(converter.accept(null, String.class));
        assertFalse(converter.accept(null, String.class));
        assertFalse(converter.accept(null, null));
    }

    @Test
    public void testConvert() {

        Deque values = new ArrayDeque(asList(1, 2, 3));

        Deque result = (Deque) converter.convert("1,2,3", Deque.class, Integer.class);

        assertTrue(CollectionUtils.equals(values, result));

        values = new ArrayDeque(asList("123"));

        result = (Deque) converter.convert("123", Deque.class, String.class);

        assertTrue(CollectionUtils.equals(values, result));

        assertNull(converter.convert(null, Collection.class, Integer.class));
        assertNull(converter.convert("", Collection.class, null));
    }

    @Test
    public void testGetSourceType() {
        assertEquals(String.class, converter.getSourceType());
    }

    @Test
    public void testGetPriority() {
        assertEquals(Integer.MAX_VALUE - 3, converter.getPriority());
    }
}
