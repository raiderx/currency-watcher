package org.karpukhin.currencywatcher.web;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.karpukhin.currencywatcher.Rate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 26.08.15
 */
public class SimpleRateWto {

    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

    private String dateTime;
    private BigDecimal buy;
    private BigDecimal sell;

    public static List<SimpleRateWto> convert(List<Rate> rates) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        List<SimpleRateWto> result = new ArrayList<>(rates.size());
        for (Rate rate : rates) {
            SimpleRateWto wto = new SimpleRateWto();
            wto.dateTime = rate.getBankTime() != null ? dateTimeFormatter.print(rate.getBankTime()) : null;
            wto.buy = rate.getBuy();
            wto.sell = rate.getSell();
            result.add(wto);
        }
        return result;
    }

    public String getDateTime() {
        return dateTime;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public BigDecimal getSell() {
        return sell;
    }
}
