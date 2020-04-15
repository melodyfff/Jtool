package com.xinchen.tool.example;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * @author xinchen
 * @version 1.0
 * @date 15/04/2020 16:49
 */
public class ClientForJDK8 {
    public static void main(String[] args) throws InterruptedException {

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // 这里就不需要工厂去创建LongEvent
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new,bufferSize, DaemonThreadFactory.INSTANCE);


        disruptor.handleEventsWith((event, sequence, endOfBatch) -> System.out.println("Event: " + event));

        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++)
        {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setValue(buffer.getLong(0)), bb);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
