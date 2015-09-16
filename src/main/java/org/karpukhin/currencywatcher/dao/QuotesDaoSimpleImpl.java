package org.karpukhin.currencywatcher.dao;

import org.karpukhin.currencywatcher.model.Quote;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */
@Repository
public class QuotesDaoSimpleImpl implements QuotesDao {

    private Map<String, Quote> quotes = new ConcurrentHashMap<>();

    @Override
    public void createQuote(Quote quote) {
        quotes.put(quote.getName(), quote);
    }

    @Override
    public Quote getLastQuote(String currencyPair) {
        return quotes.get(currencyPair);
    }

    @Override
    public Collection<Quote> getLastQuotes() {
        return quotes.values();
    }
}
