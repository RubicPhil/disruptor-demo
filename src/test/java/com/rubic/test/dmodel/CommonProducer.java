package com.rubic.test.dmodel;

/**
 * CommonProducer
 *
 * @author rubic
 * @since 2018-07-26
 */
public abstract class CommonProducer implements Producer {

    private String com;

    public CommonProducer() {}

    public void setCom(String com) {
        this.com = com;
    }

    public String getCom() {
        return com;
    }

    @Override
    abstract public void doJob();

    @Override
    public String getIds() {
        return "rubic";
    }
}
