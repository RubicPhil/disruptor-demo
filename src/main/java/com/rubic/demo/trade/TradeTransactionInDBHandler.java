package com.rubic.demo.trade;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * @author rubic
 */
public class TradeTransactionInDBHandler implements EventHandler<TradeTransaction>, WorkHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction tradeTransaction, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(tradeTransaction);
    }

    @Override
    public void onEvent(TradeTransaction tradeTransaction) throws Exception {
        System.out.println("第二个消费者 消费了id: " + tradeTransaction.getId());
    }
}
