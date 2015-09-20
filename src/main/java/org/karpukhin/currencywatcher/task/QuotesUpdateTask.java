package org.karpukhin.currencywatcher.task;

import ch.qos.logback.core.util.Duration;
import org.karpukhin.currencywatcher.model.Quote;
import org.karpukhin.currencywatcher.rateproviders.QuotesProvider;
import org.karpukhin.currencywatcher.rateproviders.YahooQuotesProvider;
import org.karpukhin.currencywatcher.service.QuotesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Pavel Karpukhin
 * @since 20.09.15
 */
@Component
public class QuotesUpdateTask {

    private static final Logger logger = LoggerFactory.getLogger(QuotesUpdateTask.class);

    private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1L);
    private static final String TIME_FORMAT = "HH:mm:ss";

    @Autowired
    private QuotesService quotesService;

    @Autowired
    private TaskScheduler taskExecutor;

    @Value("${quotes.update.task.currency.pairs}")
    private String currencyPairs;

    @Value("${quotes.update.task.interval}")
    private String interval;

    private Future future;

    private QuotesProvider quotesProvider;

    public QuotesUpdateTask() {
        quotesProvider = new YahooQuotesProvider();
    }

    @PostConstruct
    public void updateAndSchedule() {
        update();
        schedule();
    }

    void update() {
        logger.info("QuotesUpdate task was started");
        logger.debug("Currency pairs: {}", currencyPairs);
        try {
            Collection<Quote> quotes = quotesProvider.getQuotes(currencyPairs);
            quotesService.updateQuotes(quotes);
        } catch (Exception e) {
            logger.error("Error updating quotes", e);
        }
    }

    void schedule() {
        if (future != null) {
            future.cancel(true);
        }

        future = taskExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                return getNextExecutionTime();
            }
        });
    }

    Date getNextExecutionTime() {
        long now = System.currentTimeMillis();

        logger.debug("Interval as string: {}", interval);
        long millis = Duration.valueOf(interval).getMilliseconds();

        Date date = new Date(now + millis);
        if (logger.isInfoEnabled()) {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
            logger.info(String.format("Next time quotes will be updated after %d:%02d at %s",
                    millis / ONE_MINUTE, millis % ONE_MINUTE, sdf.format(date)));
        }
        return date;
    }
}
