package org.karpukhin.currencywatcher.rateproviders;

import org.karpukhin.currencywatcher.model.Quote;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 10.09.15
 */
public interface QuotesProvider {

    Quote getQuote(String currencyPair);

    Collection<Quote> getQuotes(String currencyPairs);

    Quote parseQuote(InputStream input);

    Collection<Quote> parseQuotes(InputStream input);

    Quote parseQuote(String str);

    Collection<Quote> parseQuotes(List<String> strings);
}
