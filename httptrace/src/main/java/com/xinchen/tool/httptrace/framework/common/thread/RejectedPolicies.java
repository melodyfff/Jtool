package com.xinchen.tool.httptrace.framework.common.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * @date 2021-07-13 16:52
 */
public final class RejectedPolicies {

  /**
   * when rejected happened ,add the new task and run the oldest task
   *
   * @return rejected execution handler
   */
  public static RejectedExecutionHandler runsOldestTaskPolicy() {
    return (r, executor) -> {
      if (executor.isShutdown()) {
        return;
      }
      BlockingQueue<Runnable> workQueue = executor.getQueue();
      Runnable firstWork = workQueue.poll();
      boolean newTaskAdd = workQueue.offer(r);
      if (firstWork != null) {
        firstWork.run();
      }
      if (!newTaskAdd) {
        executor.execute(r);
      }
    };
  }
}

