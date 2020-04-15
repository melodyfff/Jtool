package com.xinchen.tool.example;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 *
 * Publishing Using the Legacy API
 *
 *
 * @author xinchen
 * @version 1.0
 * @date 15/04/2020 16:29
 */
public class LongEvetProducer {
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEvetProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer byteBuffer){

        // 抓取下个序列
        long sequence = ringBuffer.next();

        try {
            // 为序列获取Disrupto的入口
            LongEvent event = ringBuffer.get(sequence);

            // 填充数据
            event.setValue(byteBuffer.getLong(0));
        } finally {
            ringBuffer.publish(sequence);
        }

    }
}
