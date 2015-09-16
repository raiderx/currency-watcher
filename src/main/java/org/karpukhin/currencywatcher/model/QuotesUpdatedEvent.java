package org.karpukhin.currencywatcher.model;

import java.util.Collection;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */
public class QuotesUpdatedEvent {

    private Collection<Quote> quotes;

    public QuotesUpdatedEvent(Collection<Quote> quotes) {
        this.quotes = quotes;
    }

    public Collection<Quote> getQuotes() {
        return quotes;
    }
}
