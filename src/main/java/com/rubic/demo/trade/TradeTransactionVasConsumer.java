package com.rubic.demo.trade;

import com.lmax.disruptor.EventHandler;

/**
 * @author rubic
 */
public class TradeTransactionVasConsumer implements EventHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction tradeTransaction, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("消费者1 消费了 " + tradeTransaction.getId() + "; ringbuffer's sequence is : " + sequence);
    }
}
