package com.rubic.demo;

/**
 * FalseSharing: 伪共享问题
 * 问题描述：在核心1上运行的线程想更新变量X，同时核心2上的线程想要更新变量Y。
 * 不幸的是，这两个变量在同一个缓存行中。每个线程都要去竞争缓存行的所有权来更新变量。
 * 如果核心1获得了所有权，缓存子系统将会使核心2中对应的缓存行失效。
 * 当核心2获得了所有权然后执行更新操作，核心1就要使自己对应的缓存行失效。
 * 这会来来回回的经过L3缓存，大大影响了性能。如果互相竞争的核心位于不同的插槽，就要额外横跨插槽连接，问题可能更加严重。
 *
 * 方法：内存填充，通过填充相应字节大小的变量，使得一个缓冲行只包含一个需要竞争的变量
 *
 * @author rubic
 * @since 2018/7/30
 */
public class FalseSharing implements Runnable {

    public final static  int  NUM_THREADS = 2;
    public final static long ITERATIONS = 500L * 1000L * 1000L;
    private final int arrayIndex;

    private static VolatileLong[] longs = new VolatileLong[NUM_THREADS];

    static {
        for (int i = 0; i < longs.length; i++) {
            longs[i] = new VolatileLong();
        }
    }


    public FalseSharing(final int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }


    public static void main(String[] args) throws Exception {
        final long start = System.nanoTime();
        runTest();
        System.out.println("duration = " + (System.nanoTime() - start));
    }

    private static void runTest() throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new FalseSharing(i));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    public void run() {
        long i = ITERATIONS + 1;
        while (0 != --i) {
            longs[arrayIndex].value = i;
        }
    }

    public final static class VolatileLong {
        public volatile long value = 0L;
        public long p1, p2, p3, p4, p5, p6;
    }
}
