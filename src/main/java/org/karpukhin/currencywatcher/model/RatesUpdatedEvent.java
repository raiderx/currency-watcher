package org.karpukhin.currencywatcher.model;

import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 26.03.15
 */
public class RatesUpdatedEvent {

    private String category;
    private List<Rate> rates;

    public RatesUpdatedEvent(String category, List<Rate> rates) {
        this.category = category;
        this.rates = rates;
    }

    public String getCategory() {
        return category;
    }

    public List<Rate> getRates() {
        return rates;
    }
}
