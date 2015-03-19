package org.karpukhin.currencywatcher;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
public class Rate {

    private String bankName;
    private DateTime bankTime;
    private OperationCategories category;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal buy;
    private BigDecimal buyDiff;
    private BigDecimal longBuyDiff;
    private BigDecimal sell;
    private BigDecimal sellDiff;
    private BigDecimal longSellDiff;
    private DateTime created;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public DateTime getBankTime() {
        return bankTime;
    }

    public void setBankTime(DateTime bankTime) {
        this.bankTime = bankTime;
    }

    public OperationCategories getCategory() {
        return category;
    }

    public void setCategory(OperationCategories category) {
        this.category = category;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public BigDecimal getBuyDiff() {
        return buyDiff;
    }

    public void setBuyDiff(BigDecimal buyDiff) {
        this.buyDiff = buyDiff;
    }

    public BigDecimal getLongBuyDiff() {
        return longBuyDiff;
    }

    public void setLongBuyDiff(BigDecimal longBuyDiff) {
        this.longBuyDiff = longBuyDiff;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    public BigDecimal getSellDiff() {
        return sellDiff;
    }

    public void setSellDiff(BigDecimal sellDiff) {
        this.sellDiff = sellDiff;
    }

    public BigDecimal getLongSellDiff() {
        return longSellDiff;
    }

    public void setLongSellDiff(BigDecimal longSellDiff) {
        this.longSellDiff = longSellDiff;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "bankName='" + bankName + '\'' +
                ", bankTime=" + bankTime +
                ", category=" + category +
                ", fromCurrency='" + fromCurrency + '\'' +
                ", toCurrency='" + toCurrency + '\'' +
                ", buy=" + buy +
                ", buyDiff=" + buyDiff +
                ", longBuyDiff=" + longBuyDiff +
                ", sell=" + sell +
                ", sellDiff=" + sellDiff +
                ", longSellDiff=" + longSellDiff +
                ", created=" + created +
                '}';
    }
}
