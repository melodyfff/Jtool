package com.xinchen.tool.perftest.support;

import com.lmax.disruptor.EventReleaseAware;
import com.lmax.disruptor.EventReleaser;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkProcessor;
import com.xinchen.tool.perftest.support.util.PaddedLong;

/**
 *
 * 相关说明 ： https://github.com/LMAX-Exchange/disruptor/issues/181
 *
 *
 * {@link EventReleaseAware}修饰的类，在{@link WorkProcessor}初始化时,设置内部的{@link EventReleaser}
 * <pre>
 *     if (this.workHandler instanceof EventReleaseAware) {
 *         ((EventReleaseAware)this.workHandler).setEventReleaser(this.eventReleaser);
 *     }
 * </pre>
 *
 * {@link WorkProcessor}中的{@link EventReleaser}，默认实现是设置当前{@link WorkProcessor}的{@link Sequence}值为long的最大值
 *
 *
 * @author xinchen
 * @version 1.0
 * @date 28/06/2020 15:44
 */
public class EventCountingAndReleasingWorkHandler implements WorkHandler<ValueEvent>, EventReleaseAware {
    private final PaddedLong[] counters;
    private final int index;
    private EventReleaser eventReleaser;

    public EventCountingAndReleasingWorkHandler(final PaddedLong[] counters, final int index) {
        this.counters = counters;
        this.index = index;
    }

    @Override
    public void onEvent(final ValueEvent event) throws Exception {
        // 这里的EventReleaser实际为WorkProcessor的一个内部实现
        // 实际操作为 WorkProcessor.this.sequence.set(9223372036854775807L) 设置当前WorkProcessor中的sequence值为Long.MAX_VALUE
        // 关于这个的用法可参考： https://github.com/LMAX-Exchange/disruptor/issues/181

        // 这是一种向Disruptor发出信号的机制
        // 表明workHandler已经获取了事件的副本，并且这个已经传入workHandler的事件已经不需要了，而且能被publisher覆盖
        // 适用于some of the WorkHandlers are potentially quite slow
        eventReleaser.release();
        // 相当于自增1
        counters[index].set(counters[index].get() + 1L);
    }

    @Override
    public void setEventReleaser(final EventReleaser eventReleaser) {
        // 此处在WorkProcessor初始化时设置进来
        this.eventReleaser = eventReleaser;
    }
}
