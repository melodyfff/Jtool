package com.xinchen.tool.pipeline.pipeline;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/2/1 21:24
 */
@Slf4j
public class HandlerContext {
    /** 前一节点 */
    HandlerContext pre;
    /** 后一节点 */
    HandlerContext next;
    /** 当前节点处理具体事务 */
    Handler handler;

    private Task task;


    public void fireTaskReceived(Request request) {
        log.debug("进入[{}] -> 触发 pipeline -> 接收到任务 fireTaskReceived",this.getClass().getName());
        invokeTaskReceived(next(), request);
    }
    public void fireTaskFiltered(Task task) {
        log.debug("进入[{}] -> 触发 pipeline -> 过滤任务 [{}] fireTaskFiltered",this.getClass().getName(),task);
        invokeTaskFiltered(next(), task);
    }

    public void fireTaskExecuted(Task task) {
        log.debug("进入[{}] -> 触发 pipeline -> 执行任务 [{}] fireTaskExecuted",this.getClass().getName(),task);
        invokeTaskExecuted(next(), task);
    }

    public void fireAfterCompletion(HandlerContext ctx) {
        log.debug("进入[{}] -> 结束 pipeline -> 结束任务 fireAfterCompletion",this.getClass().getName());
        invokeAfterCompletion(next());
    }

    /**
     * 处理接收到任务的事件
     * @param ctx HandlerContext
     * @param request Request 业务请求封装
     */
    static void invokeTaskReceived(HandlerContext ctx, Request request){
        if (null!=ctx){
            try {
                ctx.handler().receiveTask(ctx,request);
            } catch (Throwable e){
                // 捕获处理异常
                ctx.handler().exceptionCaught(ctx, e);
            }
        }
    }

    /**
     * 处理执行任务事件
     * @param ctx HandlerContext
     * @param task Task
     */
    static void invokeTaskExecuted(HandlerContext ctx, Task task) {
        if (null != ctx) {
            try {
                ctx.handler().executeTask(ctx, task);
            } catch (Exception e) {
                ctx.handler().exceptionCaught(ctx, e);
            }
        }
    }

    /**
     * 处理任务过滤事件
     * @param ctx HandlerContext
     * @param task Task
     */
    static void invokeTaskFiltered(HandlerContext ctx, Task task) {
        if (null != ctx) {
            try {
                ctx.handler().filterTask(ctx, task);
            } catch (Throwable e) {
                ctx.handler().exceptionCaught(ctx, e);
            }
        }
    }

    static void invokeAfterCompletion(HandlerContext ctx) {
        if (null != ctx) {
            ctx.handler().afterCompletion(ctx);
        }
    }

    private HandlerContext next(){
        return next;
    }

    private Handler handler(){
        return handler;
    }
}
