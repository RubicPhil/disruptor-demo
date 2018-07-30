package com.rubic.demo.trade;


import com.lmax.disruptor.EventHandler;

/**
 * @author rubic
 */
public class TradeTransactionJMSNotifyHandler implements EventHandler<TradeTransaction> {

    @Override
    public void onEvent(TradeTransaction tradeTransaction, long l, boolean b) throws Exception {
        System.out.println("第三个消费者 消费了 id： " + tradeTransaction.getId());
    }
}
