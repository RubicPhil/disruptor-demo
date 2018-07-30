package com.rubic.demo.work;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demo
 *
 * @author rubic
 * @since 2018-07-17
 */
public class Demo {

    public static void main(String[] args) {
        int bufferSize = 16;

        LongEventFactory longEventFactory = new LongEventFactory();

        ExecutorService executor = Executors.newCachedThreadPool();

        RingBuffer ringBuffer = RingBuffer.create(ProducerType.SINGLE, longEventFactory, bufferSize,
                new SleepingWaitStrategy());

        Producer producer = new Producer(ringBuffer);
        for (int i = 0; i < bufferSize; i++) {
            producer.onEvent(new Long(i));
        }

        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        Sequence consumerSequence = new Sequence(-1);

        LongEventHandler eventHandler = new LongEventHandler(new AtomicInteger());
        IntEventExceptionHandler intEventExceptionHandler = new IntEventExceptionHandler();
        MyWorkProcessor<LongEvent> wp1 = new MyWorkProcessor<LongEvent>(ringBuffer, sequenceBarrier, eventHandler, intEventExceptionHandler, consumerSequence);
        //执行处理线程
        executor.execute(wp1);
        WorkProcessor<LongEvent> wp2 = new WorkProcessor<LongEvent>(ringBuffer, sequenceBarrier, eventHandler, intEventExceptionHandler, consumerSequence);
        executor.execute(wp2);
        WorkProcessor<LongEvent> wp3 = new WorkProcessor<LongEvent>(ringBuffer, sequenceBarrier, eventHandler, intEventExceptionHandler, consumerSequence);
        executor.execute(wp3);
        WorkProcessor<LongEvent> wp4 = new WorkProcessor<LongEvent>(ringBuffer, sequenceBarrier, eventHandler, intEventExceptionHandler, consumerSequence);
        executor.execute(wp4);

        wp1.halt();
        wp2.halt();
        wp3.halt();
        wp4.halt();
        System.out.println("wp1. " + wp1.isRunning());
        executor.shutdown();
    }
}
