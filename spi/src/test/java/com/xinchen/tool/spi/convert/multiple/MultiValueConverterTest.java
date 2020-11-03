package com.xinchen.tool.spi.convert.multiple;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TransferQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link MultiValueConverter} Test
 *
 */
public class MultiValueConverterTest {

    @Test
    public void testFind() {
        MultiValueConverter converter = MultiValueConverter.find(String.class, String[].class);
        assertEquals(StringToArrayConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, BlockingDeque.class);
        assertEquals(StringToBlockingDequeConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, BlockingQueue.class);
        assertEquals(StringToBlockingQueueConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, Collection.class);
        assertEquals(StringToCollectionConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, Deque.class);
        assertEquals(StringToDequeConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, List.class);
        assertEquals(StringToListConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, NavigableSet.class);
        assertEquals(StringToNavigableSetConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, Queue.class);
        assertEquals(StringToQueueConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, Set.class);
        assertEquals(StringToSetConverter.class, converter.getClass());

        converter = MultiValueConverter.find(String.class, TransferQueue.class);
        assertEquals(StringToTransferQueueConverter.class, converter.getClass());
    }
}
