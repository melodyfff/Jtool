package com.xinchen.tool.pipeline.scheduler;

import com.xinchen.tool.pipeline.pipeline.Request;
import com.xinchen.tool.pipeline.pipeline.Task;

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
