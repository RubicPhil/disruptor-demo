package com.rubic.test.dmodel;

/**
 * Main
 *
 * @author rubic
 * @since 2018-07-26
 */
public class Main {

    private static Producer producer;

    public static void main(String[] args) {
        producer = new QueueProducer("163.com");
        producer.doJob();

        producer = new DisruptorProducer();
        producer.doJob();
    }
}
