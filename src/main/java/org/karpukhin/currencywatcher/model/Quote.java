package org.karpukhin.currencywatcher.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */
public class Quote {

    private String symbol;
    private String name;
    private BigDecimal value;
    private DateTime dateTime;
    private BigDecimal ask;
    private BigDecimal bid;

    public Quote(String symbol, String name, BigDecimal value, DateTime dateTime, BigDecimal ask, BigDecimal bid) {
        this.ask = ask;
        this.bid = bid;
        this.dateTime = dateTime;
        this.name = name;
        this.symbol = symbol;
        this.value = value;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", dateTime=" + dateTime +
                ", ask=" + ask +
                ", bid=" + bid +
                '}';
    }
}
