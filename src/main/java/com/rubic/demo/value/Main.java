package com.rubic.demo.value;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.*;

/**
 * 三个生产者一个消费者，测试并发量
 * @author rubic
 */
public class Main{
        private static final int NUM_PUBLISHERS = Runtime.getRuntime().availableProcessors();
        private static final int BUFFER_SIZE = 1024 * 64;
        private static final long ITERATIONS = 1000L *1000L *20L;
        private final ExecutorService executor = Executors.newFixedThreadPool(NUM_PUBLISHERS + 1, DaemonThreadFactory.INSTANCE);
        private final CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM_PUBLISHERS +1);
        private final RingBuffer ringBuffer = RingBuffer.createMultiProducer(ValueEvent.EVENT_FACTORY, BUFFER_SIZE, new BusySpinWaitStrategy());
        private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        private final ValueAdditionEventHandler handler = new ValueAdditionEventHandler();
        private final BatchEventProcessor batchEventProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, handler);
        private final ValueBatchPublisher[] valuePublishers = new ValueBatchPublisher[NUM_PUBLISHERS];

        {

            for(int i =0; i < NUM_PUBLISHERS; i++) {
                valuePublishers[i] = new ValueBatchPublisher(cyclicBarrier, ringBuffer, ITERATIONS / NUM_PUBLISHERS,16);
            }

            ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
        }


        public long runDisruptorPass() throws Exception {
            final CountDownLatch latch = new CountDownLatch(1);

            handler.reset(latch, batchEventProcessor.getSequence().get() + ((ITERATIONS / NUM_PUBLISHERS) * NUM_PUBLISHERS));
            Future<?>[] futures = new Future[NUM_PUBLISHERS];
            for(int i =0; i < NUM_PUBLISHERS; i++)
            {
                futures[i] = executor.submit(valuePublishers[i]);
            }
            executor.submit(batchEventProcessor);

            long start = System.currentTimeMillis();
            cyclicBarrier.await(); //start test
            for(int i = 0; i < NUM_PUBLISHERS; i++) {
                futures[i].get();
            } //all published


            latch.await(); //wait for all handled
            long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);
            batchEventProcessor.halt();
            return opsPerSecond;
        }

        public static void main(String[] args) throws Exception
        {
            Main m = new Main();
            System.out.println("opsPerSecond:"+ m.runDisruptorPass());

        }
}