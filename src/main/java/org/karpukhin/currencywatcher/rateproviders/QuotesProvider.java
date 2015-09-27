package org.karpukhin.currencywatcher.rateproviders;

import org.karpukhin.currencywatcher.model.Quote;

import java.io.InputStream;
import java.util.Collection;

/**
 * Provider for exchange quotes
 *
 * @author Pavel Karpukhin
 * @since 10.09.15
 */
public interface QuotesProvider {

    /**
     * Returns exchange quote for given currency pair
     *
     * @param currencyPair currency pair
     * @return exchange quote for given currency pair
     */
    Quote getQuote(String currencyPair);

    /**
     * Returns exchange quotes for given currency pairs
     *
     * @param currencyPairs comma spearates currency pairs
     * @return exchange quotes for given currency pairs
     */
    Collection<Quote> getQuotes(String currencyPairs);

    /**
     * Returns exchange quote parsed from given stream
     *
     * @param input stream
     * @return exchange quote parsed from given stream
     */
    Quote parseQuote(InputStream input);

    /**
     * Returns exchange quotes parsed from given stream
     *
     * @param input stream
     * @return exchange quotes parsed from given stream
     */
    Collection<Quote> parseQuotes(InputStream input);
}
