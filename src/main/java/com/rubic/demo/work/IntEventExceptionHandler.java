package com.rubic.demo.work;

import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * IntEventExceptionHandler：异常处理类
 *
 * @author rubic
 * @since 2018/7/30
 */
public class IntEventExceptionHandler implements ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(IntEventExceptionHandler.class);

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
