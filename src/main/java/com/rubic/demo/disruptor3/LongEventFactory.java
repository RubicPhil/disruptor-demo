package com.rubic.demo.disruptor3;

import com.lmax.disruptor.EventFactory;

/**
 * @author rubic
 */
public class LongEventFactory implements EventFactory<LongEvent> {

    public LongEvent newInstance() {
        return new LongEvent();
    }
}
