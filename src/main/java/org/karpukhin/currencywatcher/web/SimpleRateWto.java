package org.karpukhin.currencywatcher.web;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.karpukhin.currencywatcher.Rate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 26.08.15
 */
public class SimpleRateWto {

    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String NUMBER_FORMAT = "0.00";

    private String dateTime;
    private String buy;
    private String sell;

    public static List<SimpleRateWto> convert(List<Rate> rates) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        NumberFormat numberFormat = new DecimalFormat(NUMBER_FORMAT);
        List<SimpleRateWto> result = new ArrayList<>(rates.size());
        for (Rate rate : rates) {
            SimpleRateWto wto = new SimpleRateWto();
            wto.dateTime = rate.getBankTime() != null ? dateTimeFormatter.print(rate.getBankTime()) : null;
            wto.buy = numberFormat.format(rate.getBuy());
            wto.sell = numberFormat.format(rate.getSell());
            result.add(wto);
        }
        return result;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getBuy() {
        return buy;
    }

    public String getSell() {
        return sell;
    }

    static String getValue(BigDecimal value, BigDecimal diff, NumberFormat format) {
        if (value == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(format.format(value));
        if (diff != null && diff.compareTo(BigDecimal.ZERO) != 0) {
            sb.append(" (").append(format.format(diff)).append(")");
        }
        return sb.toString();
    }
}
