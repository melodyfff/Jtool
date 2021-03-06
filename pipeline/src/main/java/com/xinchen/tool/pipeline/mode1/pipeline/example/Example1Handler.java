package com.xinchen.tool.pipeline.mode1.pipeline.example;

import com.xinchen.tool.pipeline.mode1.pipeline.Handler;
import com.xinchen.tool.pipeline.mode1.pipeline.HandlerContext;
import com.xinchen.tool.pipeline.mode1.pipeline.Request;
import com.xinchen.tool.pipeline.mode1.pipeline.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/2/1 22:44
 */
@Slf4j
public class Example1Handler implements Handler {
    private final String name;

    public Example1Handler(String name) {
        this.name = name;
    }

    @Override
    public void receiveTask(HandlerContext ctx, Request request) {
        log.info("进入[{}] -> 触发 pipeline -> 接收请求 [{}]", name, request);
        ctx.fireTaskReceived(request);
    }

    @Override
    public void filterTask(HandlerContext ctx, Task task) {
        log.info("进入[{}] -> 触发 pipeline -> 过滤任务 [{}]", name, task.id());
        ctx.fireTaskFiltered(task);
    }

    @Override
    public void executeTask(HandlerContext ctx, Task task) {
        if (task instanceof ExampleTask1) {
            log.info("进入[{}] -> 触发 pipeline -> 执行任务 [{}]", name, task.id());
            ctx.fireTaskExecuted(task);
        }
    }

    @Override
    public void afterCompletion(HandlerContext ctx) {
        log.info("进入[{}] -> 触发 pipeline -> 任务结束 ", name);
        ctx.fireAfterCompletion(ctx);
    }

    @Override
    public boolean isSupport() {
        return true;
    }
}
