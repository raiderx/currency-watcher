package org.karpukhin.currencywatcher;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "rates",
                query = "from Rate r where r.bankName = :bankName and category = :category and fromCurrency = :fromCurrency and toCurrency = :toCurrency " +
                        "order by bankTime desc"),
        @NamedQuery(
                name = "ratesByDate",
                query = "from Rate r " +
                        "where r.bankName = :bankName and category = :category and fromCurrency = :fromCurrency and toCurrency = :toCurrency and bankTime >= :fromDate and bankTime < :toDate " +
                        "order by bankTime asc")
})
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "lastRates",
                query = "SELECT R1.* " +
                        "FROM RATES R1 " +
                        "JOIN (SELECT MAX(BANK_TIME) BANK_TIME, BANK_NAME, CATEGORY, FROM_CURRENCY, TO_CURRENCY " +
                        "      FROM RATES " +
                        "      GROUP BY BANK_NAME, CATEGORY, FROM_CURRENCY, TO_CURRENCY) R2 " +
                        "  ON R1.BANK_TIME = R2.BANK_TIME AND R1.BANK_NAME = R2.BANK_NAME AND R1.CATEGORY = R2.CATEGORY AND R1.FROM_CURRENCY = R2.FROM_CURRENCY AND R1.TO_CURRENCY = R2.TO_CURRENCY " +
                        "WHERE R1.BANK_NAME = :bankName",
                resultClass = Rate.class),
        @NamedNativeQuery(
                name = "lastRatesByCategory",
                query = "SELECT R1.* " +
                        "FROM RATES R1 " +
                        "JOIN (SELECT MAX(BANK_TIME) BANK_TIME, BANK_NAME, CATEGORY, FROM_CURRENCY, TO_CURRENCY " +
                        "      FROM RATES " +
                        "      GROUP BY BANK_NAME, CATEGORY, FROM_CURRENCY, TO_CURRENCY) R2 " +
                        "  ON R1.BANK_TIME = R2.BANK_TIME AND R1.BANK_NAME = R2.BANK_NAME AND R1.CATEGORY = R2.CATEGORY AND R1.FROM_CURRENCY = R2.FROM_CURRENCY AND R1.TO_CURRENCY = R2.TO_CURRENCY " +
                        "WHERE R1.BANK_NAME = :bankName AND R1.CATEGORY = :category",
                resultClass = Rate.class)

})
@Table(name = "RATES")
public class Rate {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "BANK_NAME", nullable = false, length = 50)
    private String bankName;
    @Column(name = "BANK_TIME", nullable = false)
    @Type(type = "org.karpukhin.currencywatcher.jodatime.hibernate.PersistentDateTime")
    private DateTime bankTime;
    @Column(name = "CATEGORY", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private OperationCategories category;
    @Column(name = "FROM_CURRENCY", nullable = false, length = 10)
    private String fromCurrency;
    @Column(name = "TO_CURRENCY", nullable = false, length = 10)
    private String toCurrency;

    /**
     * Current buy value
     */
    @Column(name = "BUY", precision = 10, scale = 2)
    private BigDecimal buy;

    /**
     * Difference between current buy value and past
     */
    @Column(name = "BUY_DIFF", precision = 10, scale = 2)
    private BigDecimal buyDiff;

    /**
     * Difference between current buy value and past pivot point
     */
    @Column(name = "LONG_BUY_DIFF", precision = 10, scale = 2)
    private BigDecimal longBuyDiff;

    /**
     * Current sell value
     */
    @Column(name = "SELL", precision = 10, scale = 2)
    private BigDecimal sell;

    /**
     * Difference between current sell value and past
     */
    @Column(name = "SELL_DIFF", precision = 10, scale = 2)
    private BigDecimal sellDiff;

    /**
     * Difference between current sell value and past pivot point
     */
    @Column(name = "LONG_SELL_DIFF", precision = 10, scale = 2)
    private BigDecimal longSellDiff;

    @Column(name = "CREATED", nullable = false)
    @Type(type = "org.karpukhin.currencywatcher.jodatime.hibernate.PersistentDateTime")
    private DateTime created;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
