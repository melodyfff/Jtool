package com.xinchen.tool.example;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * @author xinchen
 * @version 1.0
 * @date 15/04/2020 16:38
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        final LongEventFactory factory = new LongEventFactory();

        int bufferSize = 1024;

        // eventFactory   : the factory to create events in the ring buffer
        // ringBufferSize : the size of the ring buffer.
        // threadFactory  : a {@link ThreadFactory} to create threads to for processors.
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE);


        // 连接事件处理
        disruptor.handleEventsWith(new LongEventHandler());

        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        LongEvetProducer producer = new LongEvetProducer(ringBuffer);


        ByteBuffer byteBuffer = ByteBuffer.allocate(8);

        for (long i=0;true;i++){
            byteBuffer.putLong(0, i);
            producer.onData(byteBuffer);
            TimeUnit.SECONDS.sleep(1);
        }

    }
}
