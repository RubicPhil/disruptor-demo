package com.rubic.demo.work;

import com.lmax.disruptor.RingBuffer;

/**
 * Producer：元素生产者
 *
 * @author rubic
 * @since 2018-07-17
 */
public class Producer {

    private RingBuffer<LongEvent> ringBuffer;

    public Producer(RingBuffer ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onEvent(Long id) {
        long sequence = ringBuffer.next();
        try {
            LongEvent event = ringBuffer.get(sequence);
            event.setId(id);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
