package com.xinchen.tool.example;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 *
 * Publishing Using Translators
 *
 * <pre>
 * {@link RingBuffer} 在 3.0 版本之前被认为是 Disruptor 的主要概念。
 * 但从 Diruptor 3.0 开始，RingBuffer 只负责存储和更新 Disruptor 的数据，在一些高级的使用场景中用户也可以自定义它。
 *
 * RingBuffer 是基于数组的缓存实现，存储生产和消费的 Event，它实现了阻塞队列的语义，也是创建 sequencer 与定义 WaitStartegy 的入口。
 * 如果 RingBuffer 满了，则生产者会阻塞等待；如果 RingBuffer 空了，则消费者会阻塞等待。
 * </pre>
 *
 *
 * @author xinchen
 * @version 1.0
 * @date 15/04/2020 16:09
 */
public class LongEventProducerWithTranslator {

    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR = new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
        @Override
        public void translateTo(LongEvent event, long sequence, ByteBuffer arg0) {
            event.setValue(arg0.getLong(0));
        }
    };

    public void onData(ByteBuffer byteBuffer){
        ringBuffer.publishEvent(TRANSLATOR,byteBuffer);
    }
}
