package com.rubic.demo.value;

import com.lmax.disruptor.EventFactory;

/**
 * @author rubic
 */
public class ValueEvent {
    private long value;
    public long getValue()
    {
        return value;
    }
    public void setValue(final long value)
    {
        this.value = value;
    }
    public static final EventFactory EVENT_FACTORY = () -> new ValueEvent();
}
