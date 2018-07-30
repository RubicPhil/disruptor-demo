package com.rubic.demo.trade;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 多个消费者之间没有关联
 * @author rubic
 */
public class Demo {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws InterruptedException {
        long beginTime = System.currentTimeMillis();
        //第一个参数用来在ring buffer中创建event，第二个参数是ring buffer的大小，第三个参数是消费者处理消息而使用的线程池。第四个参数是单或者多生产者模式，第五个参数是可选的等待策略。
		/*当消费者等待在SequenceBarrier上时，有许多可选的等待策略，不同的等待策略在延迟和CPU资源的占用上有所不同，可以视应用场景选择：
		BusySpinWaitStrategy ： 自旋等待，类似Linux Kernel使用的自旋锁。低延迟但同时对CPU资源的占用也多。
		BlockingWaitStrategy ： 使用锁和条件变量。CPU资源的占用少，延迟大。
		SleepingWaitStrategy ： 在多次循环尝试不成功后，选择让出CPU，等待下次调度，多次调度后仍不成功，尝试前睡眠一个纳秒级别的时间再尝试。这种策略平衡了延迟和CPU资源占用，但延迟不均匀。
		YieldingWaitStrategy ： 在多次循环尝试不成功后，选择让出CPU，等待下次调。平衡了延迟和CPU资源占用，但延迟也比较均匀。
		PhasedBackoffWaitStrategy ： 上面多种策略的综合，CPU资源的占用少，延迟大。*/
        //以上代码主要用来设置RingBuffer.
        int bufferSize = 1024;
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();


        //我们只需要使用 Person::new 来获取Person类构造函数的引用，Java编译器会自动根据PersonFactory.create方法的签名来选择合适的构造函数。
        Disruptor<TradeTransaction> disruptor = new Disruptor<>(TradeTransaction::new, bufferSize, threadFactory, ProducerType.SINGLE, new LiteBlockingWaitStrategy());
        // 使用disruptor创建消费者组C1,C2
        TradeTransactionVasConsumer[] consumers = new TradeTransactionVasConsumer[2];
        for(int i =0;i<consumers.length;i++){
            consumers[i] = new TradeTransactionVasConsumer();
        }
        EventHandlerGroup<TradeTransaction> handlerGroup = disruptor.handleEventsWith(new TradeTransactionVasConsumer(),new TradeTransactionInDBHandler() );

        TradeTransactionJMSNotifyHandler jmsConsumer = new TradeTransactionJMSNotifyHandler();
        // 声明在C1,C2完事之后执行JMS消息发送操作 也就是流程走到C3
        handlerGroup.then(jmsConsumer);

        disruptor.start();// 启动
        CountDownLatch latch = new CountDownLatch(1);
        // 生产者准备
        executor.submit(new TradeTransactionProducer(latch, disruptor));

        latch.await();// 等待生产者完事.

        disruptor.shutdown();

        executor.shutdown();

        System.out.println("总耗时:" + (System.currentTimeMillis() - beginTime));
    }

}
