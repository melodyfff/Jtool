package com.xinchen.tool.pipeline.mode1.scheduler;

import com.xinchen.tool.pipeline.mode1.pipeline.Request;
import com.xinchen.tool.pipeline.mode1.pipeline.Task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * 队列任务管理
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/2/1 11:04
 */
public class QueueScheduler implements Scheduler<Request, Task> {

    private BlockingQueue<Request> queue = new LinkedBlockingQueue<>();

    @Override
    public void push(Request request, Task task) {
        queue.add(request);
    }

    @Override
    public Request poll(Task task) {
        return queue.poll();
    }

    public int size(){
        return queue.size();
    }
}
