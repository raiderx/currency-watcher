package org.karpukhin.currencywatcher;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
public class Rate {

    private DateTime bankTime;
    private OperationCategories category;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal buy;
    private BigDecimal sell;
    private DateTime created;

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

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }
}
