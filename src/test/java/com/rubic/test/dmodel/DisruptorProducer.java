package com.rubic.test.dmodel;

/**
 * DisruptorProducer
 *
 * @author rubic
 * @since 2018-07-26
 */
public class DisruptorProducer extends CommonProducer {

    @Override
    public void doJob() {
        System.out.println("Disruptor: 你好 " + getIds());
    }
}
