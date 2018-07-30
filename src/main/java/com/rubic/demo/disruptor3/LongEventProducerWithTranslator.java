package com.rubic.demo.disruptor3;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.sun.java.accessibility.util.Translator;

import java.nio.ByteBuffer;

/**
 * @author rubic
 */
public class LongEventProducerWithTranslator {

    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR = ((longEvent, sequence, byteBuffer) -> longEvent.setValue(byteBuffer.getLong(0)));

    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer bb) {
        ringBuffer.publishEvent(TRANSLATOR, bb);
    }

}
