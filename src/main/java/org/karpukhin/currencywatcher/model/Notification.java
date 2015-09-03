package org.karpukhin.currencywatcher.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author Pavel Karpukhin
 * @since 03.09.15
 */
public class Notification {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "CURRENCY_PAIR", nullable = false, length = 20)
    private String currencyPair;

    @Column(name = "CONDITION", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Condition condition;

    @Column(name = "VALUE", precision = 10, scale = 2)
    private BigDecimal value;
}
