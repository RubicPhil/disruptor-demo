package com.rubic.demo.work;


import com.lmax.disruptor.WorkHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LongEventHandler：元素处理类
 *
 * @author rubic
 * @since 2018-07-17
 */
public class LongEventHandler implements WorkHandler<LongEvent> {
    private List<LongEvent> list;
    private AtomicInteger count;

    public LongEventHandler(AtomicInteger count) {
        this.count = count;
    }

    @Override
    public void onEvent(LongEvent longEvent) throws Exception {

        count.incrementAndGet();
        list.add(longEvent);
        if (count.get() % 50 == 0) {
            list.clear();
        }
        System.out.println(Thread.currentThread().getName() + "  消费了 " + longEvent.getId());
    }

    public void handle(List<LongEvent> list) {
        if (list.isEmpty()) {
            System.out.println("");
        }
    }
}
