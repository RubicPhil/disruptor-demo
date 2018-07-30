package com.rubic.demo.trade;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author rubic
 */
public class Demo3 {

    private final static int RING_BUFFER_SIZE = 1024;

    public static void main(String[] args) {


    }

    @Test
    public void test() throws Exception {
        //ThreadFactory threadFactory = (r) -> (new Thread(r, "consume thread"));
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        EventFactory<TradeTransaction> eventFactory = () -> (new TradeTransaction());

        Disruptor<TradeTransaction> disruptor = new Disruptor<TradeTransaction>(eventFactory, RING_BUFFER_SIZE,  threadFactory, ProducerType.SINGLE, new BlockingWaitStrategy());

        // 创建10个消费者来处理同一个生产者发的消息(这10个消费者不重复消费消息)
        Consumer[] consumers = new Consumer[10];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer();
        }
        disruptor.handleEventsWithWorkerPool(consumers);

        disruptor.start();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new ProducerThread(disruptor.getRingBuffer(), countDownLatch).start();
        countDownLatch.await();
        //disruptor.shutdown();
    }


    private class ProducerThread extends Thread {
        private RingBuffer<TradeTransaction> ringBuffer;
        private CountDownLatch countDownLatch;

        public ProducerThread(RingBuffer<TradeTransaction> ringBuffer, CountDownLatch countDownLatch) {
            this.ringBuffer = ringBuffer;
            this.countDownLatch = countDownLatch;
        }

        public void onData(ByteBuffer byteBuffer) {
            // 可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
            long sequence = ringBuffer.next();
            try {
                TradeTransaction tradeTransaction = ringBuffer.get(sequence);

                tradeTransaction.setId(byteBuffer.getLong(0));
            } finally {
                System.out.println("生产者发送了一条消息 id : " + byteBuffer.getLong(0));
                ringBuffer.publish(sequence);
            }
        }


        @Override
        public void run() {
            ByteBuffer bb = ByteBuffer.allocate(8);
            for (long i = 0; i < 1024L; i++) {
                bb.putLong(0, i);
                onData(bb);
            }
            countDownLatch.countDown();
        }
    }
}
