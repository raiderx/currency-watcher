package org.karpukhin.currencywatcher;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 07.12.14
 */
public class RateWto {

    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String NUMBER_FORMAT = "0.00";

    private String bankName;
    private String bankTime;
    private String category;
    private String fromCurrency;
    private String toCurrency;
    private String buy;
    private String sell;
    private String created;

    public static List<RateWto> convert(List<Rate> rates) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        NumberFormat numberFormat = new DecimalFormat(NUMBER_FORMAT);
        List<RateWto> result = new ArrayList<>(rates.size());
        for (Rate rate : rates) {
            RateWto wto = new RateWto();
            wto.bankName = rate.getBankName();
            wto.bankTime = rate.getBankTime() != null ? dateTimeFormatter.print(rate.getBankTime()) : null;
            wto.category = rate.getCategory() != null ? rate.getCategory().name() : null;
            wto.fromCurrency = rate.getFromCurrency();
            wto.toCurrency = rate.getToCurrency();
            wto.buy = rate.getBuy() != null ? numberFormat.format(rate.getBuy()) : null;
            wto.sell = rate.getSell() != null ? numberFormat.format(rate.getSell()) : null;
            wto.created = rate.getCreated() != null ? dateTimeFormatter.print(rate.getCreated()) : null;
            result.add(wto);
        }
        return result;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankTime() {
        return bankTime;
    }

    public String getCategory() {
        return category;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public String getBuy() {
        return buy;
    }

    public String getSell() {
        return sell;
    }

    public String getCreated() {
        return created;
    }
}
