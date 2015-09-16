package org.karpukhin.currencywatcher.web;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.karpukhin.currencywatcher.model.Quote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */
public class QuoteWto {

    private String name;
    private BigDecimal value;

    public static Collection<QuoteWto> convert(Collection<Quote> quotes) {
        List<QuoteWto> result = new ArrayList<>(quotes.size());
        for (Quote quote : quotes) {
            QuoteWto wto = new QuoteWto();
            wto.name = quote.getName();
            wto.value = quote.getValue();
            result.add(wto);
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getValue() {
        return value;
    }
}
