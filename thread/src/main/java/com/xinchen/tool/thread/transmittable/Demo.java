package com.xinchen.tool.thread.transmittable;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * transmittable-thread-local
 *
 * https://github.com/alibaba/transmittable-thread-local
 *
 * 下面展示的是两种包裹的方式，还有一种无侵入式的开发方式Java Agent： -Xbootclasspath/a:/path/to/transmittable-thread-local-2.0.0.jar:/path/to/your/agent/jar/files
 *
 * @author xinchen
 * @version 1.0
 * @date 30/09/2020 15:59
 */
class Demo {
    private static TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    static {
        context.set("okok-in-parent");
    }

    public static void main(String[] args) throws InterruptedException {

        Runnable simpleTask = () -> System.out.println(Thread.currentThread().getName() + ": " + context.get());
        pool.execute(simpleTask);


        TtlRunnable ttlRunnable = TtlRunnable.get(simpleTask);
        pool.execute(Objects.requireNonNull(ttlRunnable));


        TtlCallable<Void> ttlCallable = TtlCallable.get(
                () -> {
                    simpleTask.run();
                    return null;
                });
        pool.submit(Objects.requireNonNull(ttlCallable));

//        pool.shutdownNow();

        // ttl修饰线程池
        // 无需再用TtlRunnable.get()和TtlCallable.get()修饰
        final ExecutorService ttlPool = TtlExecutors.getTtlExecutorService(pool);
        ttlPool.execute(simpleTask);
        ttlPool.submit(() -> {
            simpleTask.run();
            return null;
        });

        ttlPool.shutdownNow();
    }
}
