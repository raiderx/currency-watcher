package org.karpukhin.currencywatcher.rateproviders;

import org.junit.Before;
import org.junit.Test;
import org.karpukhin.currencywatcher.model.Quote;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */
public class YahooQuotesProviderTest {

    private QuotesProvider provider;

    @Before
    public void setUp() {
        provider = new YahooQuotesProvider();
    }

    @Test
    public void testParseQuote() {
        String str = "\"RUB=X\",\"USD/RUB\",65.8440,\"9/16/2015\",\"10:35am\",65.8500,65.8380";
        Quote result = YahooQuotesProvider.parseQuote(str);
        System.out.println(result);

        assertThat(result, is(not(nullValue())));
        assertThat(result.getSymbol(), is("RUB=X"));
        assertThat(result.getName(), is("USD/RUB"));
        assertThat(result.getValue(), is(new BigDecimal("65.8440").stripTrailingZeros()));
        assertThat(result.getDateTime(), is(YahooQuotesProvider.parseDateTime("9/16/2015", "10:35am")));
        assertThat(result.getAsk(), is(new BigDecimal("65.8500").stripTrailingZeros()));
        assertThat(result.getBid(), is(new BigDecimal("65.8380").stripTrailingZeros()));
    }

    @Test
    public void testParseQuote2() {
        String str = "\"EURRUB=X\",\"EUR/RUB\",73.8520,\"9/16/2015\",\"1:01pm\",73.8950,73.8090";
        Quote result = YahooQuotesProvider.parseQuote(str);
        System.out.println(result);

        assertThat(result, is(not(nullValue())));
        assertThat(result.getSymbol(), is("EURRUB=X"));
        assertThat(result.getName(), is("EUR/RUB"));
        assertThat(result.getValue(), is(new BigDecimal("73.8520").stripTrailingZeros()));
        assertThat(result.getDateTime(), is(YahooQuotesProvider.parseDateTime("9/16/2015", "1:01pm")));
        assertThat(result.getAsk(), is(new BigDecimal("73.8950").stripTrailingZeros()));
        assertThat(result.getBid(), is(new BigDecimal("73.8090").stripTrailingZeros()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSymbolsWhenNull() {
        YahooQuotesProvider.getSymbols(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSymbolsWhenEmptyString() {
        YahooQuotesProvider.getSymbols("");
    }

    @Test
    public void testGetSymbols() {
        String result = YahooQuotesProvider.getSymbols("EUR/RUB,USD/RUB");
        assertThat(result, is("EURRUB=X,RUB=X"));
    }

    @Test
    public void testGetSymbolsWhenUnknownCurrencyPair() {
        String result = YahooQuotesProvider.getSymbols("EUR/RUB,USD/JPY");
        assertThat(result, is("EURRUB=X"));
    }
}
