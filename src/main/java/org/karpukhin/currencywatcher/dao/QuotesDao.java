package org.karpukhin.currencywatcher.dao;

import org.karpukhin.currencywatcher.model.Quote;

import java.util.Collection;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */
public interface QuotesDao {

    void createQuote(Quote quote);

    Quote getLastQuote(String currencyPair);

    Collection<Quote> getLastQuotes();
}
