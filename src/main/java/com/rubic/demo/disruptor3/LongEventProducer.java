package com.rubic.demo.disruptor3;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * @author rubic
 */
public class LongEventProducer {

    private RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }


    public void onData(ByteBuffer byteBuffer) {
        long sequence = ringBuffer.next();


            LongEvent event = ringBuffer.get(sequence);

            event.setValue(byteBuffer.getLong(0));

            ringBuffer.publish(sequence);

    }

}
