package com.xinchen.tool.thread.monitor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Monitor监控ThreadPoolExecutor
 *
 * @author xinchen
 * @version 1.0
 * @date 01/11/2019 13:06
 */
public class ThreadPoolExecutorMonitorSimple {
    public static void main(String[] args) throws InterruptedException {
        final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                2,
                4,
                10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new LogRejectedExecutionHandler()
        );

        // 创建监控任务,间隔3s
        final MonitorTask monitorTask = new MonitorTask(poolExecutor, 3);

        // 监听任务启动
        new Thread(monitorTask).start();

        for (int i = 0; i < 10; i++) {
            poolExecutor.execute(new WorkerTask("cmd-"+i));
        }

        TimeUnit.SECONDS.sleep(60);

        poolExecutor.shutdown();

        TimeUnit.SECONDS.sleep(5);

        monitorTask.shutdown();
    }

    static class LogRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 当任务队列满了,并且达到maximumPoolSize时的拒绝策略
            System.out.println(r.toString() + " is rejected");
        }
    }

    static class WorkerTask implements Runnable{
        private String command;

        public WorkerTask(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" Start. Command = "+command);
            processCommand();
            System.out.println(Thread.currentThread().getName()+" End.");
        }

        private void processCommand(){
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        @Override
        public String toString() {
            return "WorkerTask{" +
                    "command='" + command + '\'' +
                    '}';
        }
    }

    static class MonitorTask implements Runnable{
        // 被监控的executor
        private final ThreadPoolExecutor executor;
        // 监控间隔
        private final int seconds;
        // 监控开关
        private boolean run = true;

        public MonitorTask(ThreadPoolExecutor executor, int seconds) {
            this.executor = executor;
            this.seconds = seconds;
        }

        public void shutdown(){
            this.run = false;
        }
        @Override
        public void run() {
            while (run) {
                System.out.println(
                        String.format("%s - [monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, Queue: %d, isShutdown: %s, isTerminated: %s",
                                Thread.currentThread().getName(),
                                this.executor.getPoolSize(),
                                this.executor.getCorePoolSize(),
                                this.executor.getActiveCount(),
                                this.executor.getCompletedTaskCount(),
                                this.executor.getTaskCount(),
                                this.executor.getQueue().size(),
                                this.executor.isShutdown(),
                                this.executor.isTerminated()));
                try {
                    TimeUnit.SECONDS.sleep(seconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
