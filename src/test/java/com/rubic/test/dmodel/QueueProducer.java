package com.rubic.test.dmodel;

/**
 * QueueProducer
 *
 * @author rubic
 * @since 2018-07-26
 */
public class QueueProducer extends CommonProducer {


    public QueueProducer(String com) {
        super.setCom(com);
    }

    @Override
    public void doJob() {
        System.out.println("Queue: hello " + getIds() + " done " +getCom());
    }
}
