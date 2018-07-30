package com.rubic.demo.trade;

import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author rubic
 */
public class IntEventExceptionHandler implements ExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void handleEventException(Throwable ex, long sequence, Object event) {
        logger.error("handleEventException", ex);
    }

    public void handleOnStartException(Throwable ex) {
        logger.error("handleOnStartException", ex);
    }

    public void handleOnShutdownException(Throwable ex) {
        logger.error("handleOnShutdownException", ex);
    }

}
