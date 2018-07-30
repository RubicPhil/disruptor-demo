package com.rubic.demo.disruptor3;


import com.lmax.disruptor.EventHandler;

/**
 * @author rubic
 */
public class LongEventHandler implements EventHandler<LongEvent> {

    public void onEvent(LongEvent longEvent, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("LongEvent : " + longEvent.getValue() + "; sequence: " + sequence + "; endOfBatch: " + endOfBatch);
    }
}
