package org.karpukhin.currencywatcher.service;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Pavel Karpukhin
 * @since 26.03.15.
 */
@Component
public class CustomErrorHandler implements SubscriberExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        logger.error("Error occurred while dispatching event", exception);
    }

}
