package com.xinchen.tool.pipeline.mode1.scheduler;

import com.xinchen.tool.pipeline.mode1.pipeline.Request;
import com.xinchen.tool.pipeline.mode1.pipeline.Task;

/**
 *
 * 任务管理
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/2/1 11:00
 */
public interface Scheduler<R extends Request,T extends Task> {
    void push(R request,T task);

    R poll(T task);
}
