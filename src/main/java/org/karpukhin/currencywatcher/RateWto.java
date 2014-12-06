package org.karpukhin.currencywatcher;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;

/**
 * @author Pavel Karpukhin
 * @since 07.12.14
 */
public class RateWto {

    private String bankTime;
    private String category;
    private String fromCurrency;
    private String toCurrency;
    private String buy;
    private String sell;
    private String created;

    public RateWto(Rate rate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");
        DecimalFormat format = new DecimalFormat("0.00");
        this.bankTime = rate.getBankTime() != null ? formatter.print(rate.getBankTime()) : null;
        this.category = rate.getCategory() != null ? rate.getCategory().name() : null;
        this.fromCurrency = rate.getFromCurrency();
        this.toCurrency = rate.getToCurrency();
        this.buy = rate.getBuy() != null ? format.format(rate.getBuy()) : null;
        this.sell = rate.getSell() != null ? format.format(rate.getSell()) : null;
        this.created = rate.getCreated() != null ? formatter.print(rate.getCreated()) : null;
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
