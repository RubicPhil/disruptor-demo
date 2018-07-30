package com.rubic.demo.trade;

import com.lmax.disruptor.WorkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rubic
 */
public class Consumer implements WorkHandler<TradeTransaction> {

    private List<Long> ids = new ArrayList<>();

    private static final int SIZE = 5;
    private AtomicInteger count = new AtomicInteger(0);


    @Override
    public void onEvent(TradeTransaction tradeTransaction) {
        System.out.println( Thread.currentThread().getName() + " 消费了第 " + tradeTransaction.getId() + "个消息！");

    }
}
