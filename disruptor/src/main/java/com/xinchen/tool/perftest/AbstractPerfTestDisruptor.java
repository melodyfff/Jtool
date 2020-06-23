package com.xinchen.tool.perftest;

/**
 *
 * Disruptor PerfTest
 *
 * @author xinchen
 * @version 1.0
 * @date 23/06/2020 10:29
 */
public abstract class AbstractPerfTestDisruptor {
    /** Run times */
    public static final int RUNS = 7;

    protected void testImplementations()
            throws Exception {
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (getRequiredProcessorCount() > availableProcessors) {
            System.out.print("*** Warning ***: your system has insufficient processors to execute the test efficiently. ");
            System.out.println("Processors required = " + getRequiredProcessorCount() + " available = " + availableProcessors);
        }

        long[] disruptorOps = new long[RUNS];

        System.out.println("Starting Disruptor tests");
        for (int i = 0; i < RUNS; i++) {
            System.gc();
            disruptorOps[i] = runDisruptorPass();
            // operate per second
            System.out.format("Run %d, Disruptor=%,d ops/sec%n", i, Long.valueOf(disruptorOps[i]));
        }
    }

    public static void printResults(final String className, final long[] disruptorOps, final long[] queueOps) {
        for (int i = 0; i < RUNS; i++) {
            System.out.format("%s run %d: BlockingQueue=%,d Disruptor=%,d ops/sec\n",
                    className, Integer.valueOf(i), Long.valueOf(queueOps[i]), Long.valueOf(disruptorOps[i]));
        }
    }

    /**
     * CPU Processors
     * @return Processors
     */
    protected abstract int getRequiredProcessorCount();

    protected abstract long runDisruptorPass() throws Exception;
}