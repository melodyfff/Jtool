package com.xinchen.tool.httptrace.framework.common.thread;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * @date 2021-07-13 16:59
 */
class RejectedPoliciesTest {
  private static final int DEFAULT_CORE_POOL_SIZE = 1;
  private static final int DEFAULT_KEEP_ALIVE_TIME = 10;
  private static final int MAX_QUEUE_SIZE = 1;
  /**
   * Test runs oldest task policy.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRunsOldestTaskPolicy() throws Exception {
    AtomicInteger atomicInteger = new AtomicInteger();
    ThreadPoolExecutor poolExecutor =
        new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE, DEFAULT_CORE_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(MAX_QUEUE_SIZE),
            new NamedThreadFactory("OldestRunsPolicy", DEFAULT_CORE_POOL_SIZE),
            RejectedPolicies.runsOldestTaskPolicy());
    CountDownLatch downLatch1 = new CountDownLatch(1);
    CountDownLatch downLatch2 = new CountDownLatch(1);
    CountDownLatch downLatch3 = new CountDownLatch(1);

    //task1
    poolExecutor.execute(() -> {
      try {
        //wait the oldest task of queue count down
        downLatch1.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      atomicInteger.getAndAdd(1);
    });
    assertThat(atomicInteger.get()).isEqualTo(0);

    //task2
    poolExecutor.execute(() -> {
      // run second
      atomicInteger.getAndAdd(2);
    });

    //task3
    poolExecutor.execute(() -> {
      downLatch2.countDown();
      //task3 run
      atomicInteger.getAndAdd(3);
      downLatch3.countDown();
    });

    //only the task2 run which is the oldest task of queue
    assertThat(atomicInteger.get()).isEqualTo(2);


    downLatch1.countDown();
    downLatch2.await();
    //wait task3 run +3
    downLatch3.await();
    //run task3
    assertThat(atomicInteger.get()).isEqualTo(6);

  }
}
