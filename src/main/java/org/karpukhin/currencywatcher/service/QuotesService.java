package org.karpukhin.currencywatcher.service;

import org.karpukhin.currencywatcher.model.Quote;

import java.util.Collection;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */
public interface QuotesService {

    void updateQuotes(Collection<Quote> quotes);

    Quote getQuote(String currencyPair);

    Collection<Quote> getQuotes();
}
