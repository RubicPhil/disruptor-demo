package com.rubic.demo.disruptor3;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author rubic
 */
public class LongEventMainJava8 {

    /**
     * 用lambda表达式来注册EventHandler和EventProductor
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // Executor that will be used to construct new threads for consumers
        ThreadFactory threadFactory = (r) -> {return new Thread(r, "SimpleThread");};

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 8;// Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, bufferSize, threadFactory);
        // 可以使用lambda来注册一个EventHandler
        disruptor.handleEventsWith(new LongEventHandler());
        // Start the Disruptor, starts all threads running
        disruptor.start();
        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        LongEventProducerWithTranslator producerWithTranslator = new LongEventProducerWithTranslator(ringBuffer);
        LongEventProducerWithTranslator producerWithTranslator1 = new LongEventProducerWithTranslator(ringBuffer);
        ByteBuffer bb = ByteBuffer.allocate(8);

        for (long l = 0; true; l++) {
            bb.putLong(0, l);
            producerWithTranslator.onData(bb);
            //producerWithTranslator1.onData(bb);
            //ringBuffer.publishEvent((event, sequence, buffer) -> event.setValue(buffer.getLong(0)), bb);
            Thread.sleep(1000);
        }
    }
}
