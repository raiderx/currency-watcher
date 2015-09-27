package org.karpukhin.currencywatcher.task;

import org.joda.time.DateTime;
import org.karpukhin.currencywatcher.model.Rate;
import org.karpukhin.currencywatcher.rateproviders.RatesProvider;
import org.karpukhin.currencywatcher.rateproviders.TcsRatesProviderImpl;
import org.karpukhin.currencywatcher.service.RatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Future;

/**
 * @author Pavel Karpukhin
 * @since 12.02.15
 */
@Component
@ManagedResource(objectName = "org.karpukhin.currencywatcher:name=UpdateRatesTask")
public class UpdateTask {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTask.class);

    private static final Random random = new Random();

    private static final long ONE_MINUTE = 60;
    private static final long FIVE_MINUTES = 5 * 60;

    @Autowired
    private TaskScheduler taskExecutor;

    @Autowired
    private RatesService ratesService;

    private RatesProvider ratesProvider;

    private Future future;

    public UpdateTask() {
        this.ratesProvider = new TcsRatesProviderImpl();
    }

    @PostConstruct
    public void init() {
        updateRatesAndSchedule();
    }
    
    @ManagedOperation
    public void updateRatesAndSchedule() {
        update();
        reschedule();
    }

    void update() {
        logger.info("Update task started");

        try {
            Collection<Rate> rates = ratesProvider.getRates();
            ratesService.updateRates(rates);
        } catch (Exception e) {
            logger.error("Error while fetching exchange rates", e);
        }
    }
    
    void reschedule() {
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

    static long getDelay() {
        return ONE_MINUTE + Math.abs(random.nextLong()) % FIVE_MINUTES;
    }

    static Date getNextExecutionTime() {
        DateTime dateTime = DateTime.now();
        if (dateTime.getHourOfDay() >= 9 && dateTime.getHourOfDay() < 20) {
            long delay = getDelay();
            Date date = new Date(System.currentTimeMillis() + delay * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            logger.debug(String.format("Update will be executed after %d:%02d at %s",
                    delay / ONE_MINUTE, delay % ONE_MINUTE, sdf.format(date)));
            return date;
        }
        Date date = getNextDay(9, 0).toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        logger.debug("Update will be executed at {}", sdf.format(date));
        return date;
    }
    
    static DateTime getNextDay(int hourOfDay, int minuteOfHour) {
        return DateTime.now()
                .withTime(hourOfDay, minuteOfHour, 0, 0)
                .plusDays(1);
    }
}
