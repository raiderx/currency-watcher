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
    private Difference buyDifference;
    private BigDecimal sell;
    private Difference sellDifference;
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

    public Difference getBuyDifference() {
        return buyDifference;
    }

    public void setBuyDifference(Difference buyDifference) {
        this.buyDifference = buyDifference;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    public Difference getSellDifference() {
        return sellDifference;
    }

    public void setSellDifference(Difference sellDifference) {
        this.sellDifference = sellDifference;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }
}
