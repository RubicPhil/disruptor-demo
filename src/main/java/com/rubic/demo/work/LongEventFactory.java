package com.rubic.demo.work;

import com.lmax.disruptor.EventFactory;


/**
 * LongEventFactory：元素工厂
 *
 * @author rubic
 * @since 2018/7/30
 */
public class LongEventFactory implements EventFactory<LongEvent> {

    public LongEvent newInstance() {
        return new LongEvent();
    }
}
