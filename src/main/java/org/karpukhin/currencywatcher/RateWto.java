package org.karpukhin.currencywatcher;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 07.12.14
 */
public class RateWto {

    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

    private String bankTime;
    private String category;
    private String currencyPair;
    private BigDecimal buy;
    private String buyDiff;
    private BigDecimal sell;
    private String sellDiff;
    private BigDecimal average;
    private BigDecimal spread;

    public static List<RateWto> convert(List<Rate> rates) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        List<RateWto> result = new ArrayList<>(rates.size());
        for (Rate rate : rates) {
            RateWto wto = new RateWto();
            wto.bankTime = rate.getBankTime() != null ? dateTimeFormatter.print(rate.getBankTime()) : null;
            wto.category = rate.getCategory() != null ? rate.getCategory().name() : null;
            wto.currencyPair = rate.getFromCurrency() + "/" + rate.getToCurrency();
            wto.buy = rate.getBuy();
            wto.buyDiff = getDiff(rate.getLongBuyDiff());
            wto.sell = rate.getSell();
            wto.sellDiff = getDiff(rate.getLongSellDiff());
            wto.average = getAverage(rate.getBuy(), rate.getSell());
            wto.spread = getSpread(rate.getBuy(), rate.getSell());
            result.add(wto);
        }
        return result;
    }

    public String getBankTime() {
        return bankTime;
    }

    public String getCategory() {
        return category;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public String getBuyDiff() {
        return buyDiff;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public String getSellDiff() {
        return sellDiff;
    }

    public BigDecimal getAverage() {
        return average;
    }

    public BigDecimal getSpread() {
        return spread;
    }

    static BigDecimal getAverage(BigDecimal buy, BigDecimal sell) {
        if (buy != null && sell != null) {
            return sell.add(buy).divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros();
        }
        return null;
    }

    static BigDecimal getSpread(BigDecimal buy, BigDecimal sell) {
        if (buy != null && sell != null) {
            return sell.subtract(buy).abs();
        }
        return null;
    }

    static String getDiff(BigDecimal diff) {
        if (diff != null) {
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                return "greater";
            }
            if (diff.compareTo(BigDecimal.ZERO) < 0) {
                return "less";
            }
        }
        return "equals";
    }
}
