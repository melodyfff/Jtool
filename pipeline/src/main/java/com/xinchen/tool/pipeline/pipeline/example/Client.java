package com.xinchen.tool.pipeline.pipeline.example;

import com.xinchen.tool.pipeline.pipeline.DefaultPipeline;
import com.xinchen.tool.pipeline.pipeline.Pipeline;
import com.xinchen.tool.pipeline.pipeline.Request;
import com.xinchen.tool.pipeline.pipeline.Task;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/2/1 22:33
 */
public class Client {
    public static void main(String[] args) {
        Request request = new Request();
        ExampleTask1 task = ()-> "ok";

        Pipeline pipeline = new DefaultPipeline(request,task);

        pipeline
                .addLast(new Example1Handler("测试1"))
                .addLast(new Example2Handler("测试2"))
                .fireTaskReceived()
                .fireTaskFiltered()
                .fireTaskExecuted()
                .fireAfterCompletion();
    }
}
