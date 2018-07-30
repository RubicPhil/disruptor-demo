package com.rubic.demo.trade;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.dsl.Disruptor;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author rubic
 */
public class TradeTransactionProducer implements Runnable {
    Disruptor<TradeTransaction> disruptor;
    private CountDownLatch latch;
    private static int LOOP = 10;//生产者发送10条消息

    public TradeTransactionProducer(CountDownLatch latch, Disruptor<TradeTransaction> disruptor) {
        this.disruptor=disruptor;
        this.latch=latch;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        EventTranslatorOneArg<TradeTransaction, ByteBuffer> tradeTransloator = ((tradeTransaction, sequence, byteBuffer) -> tradeTransaction.setId(byteBuffer.getInt(0)));


        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for(int i = 0; i < LOOP; i++){
            byteBuffer.putInt(0, i + 60);
            System.out.println("生产者成功向Ringbuffer中发送了一条消息");
            disruptor.publishEvent(tradeTransloator, byteBuffer);
        }
        latch.countDown();
    }

}
