package com.rubic.demo.trade;

import com.lmax.disruptor.RingBuffer;
import com.rubic.demo.disruptor3.LongEvent;

import java.nio.ByteBuffer;

/**
 * @author rubic
 */
public class Producer {
    private RingBuffer<TradeTransaction> ringBuffer;

    public Producer(RingBuffer<TradeTransaction> ringBuffer) {
        this.ringBuffer = ringBuffer;
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
}
