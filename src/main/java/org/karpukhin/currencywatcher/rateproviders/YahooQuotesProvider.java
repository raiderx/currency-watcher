package org.karpukhin.currencywatcher.rateproviders;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.karpukhin.currencywatcher.exceptions.ApplicationException;
import org.karpukhin.currencywatcher.model.Quote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static org.karpukhin.currencywatcher.utils.AssertUtils.assertNotNull;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */

public class YahooQuotesProvider implements QuotesProvider {

    static final String BASE_URL = "http://download.finance.yahoo.com/d/quotes.csv";
    // s - symbol, n - name, l1 - last trade (price only), d1 - last trade date, t1 - last trade time, a - ask, b - bid
    static final String columns = "snl1d1t1ab";

    static final String UTF8 = "UTF-8";

    static final int CONNECT_TIMEOUT = 20000;
    static final String ACCEPT = "Accept";
    static final String ACCEPT_VALUE = "application/json, text/javascript, */*; q=0.01";
    static final String ACCEPT_ENCODING = "Accept-Encoding";
    static final String ACCEPT_ENCODING_VALUE = "gzip, deflate, sdch";
    static final String ACCEPT_LANGUAGE = "Accept-Language";
    static final String ACCEPT_LANGUAGE_VALUE = "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4";
    static final String USER_AGENT = "User-Agent";
    static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";

    static final String CONTENT_ENCODING = "Content-Encoding";

    static final Map<String, String> map = new HashMap<>();

    static {
        map.put("USD/RUB", "RUB=X");
        map.put("EUR/RUB", "EURRUB=X");
        map.put("EUR/USD", "EURUSD=X");
        map.put("GBP/RUB", "GBPRUB=X");
    }

    @Override
    public Quote getQuote(String currencyPair) {
        List<String> strings = getQuotesAsStrings(currencyPair);
        return strings != null && !strings.isEmpty() ? parseQuote(strings.get(0)) : null;
    }

    @Override
    public Collection<Quote> getQuotes(String currencyPairs) {
        return parseQuotes(getQuotesAsStrings(currencyPairs));
    }

    @Override
    public Quote parseQuote(InputStream input) {
        List<String> strings = getQuotesAsStrings(input);
        return strings != null && !strings.isEmpty() ? parseQuote(strings.get(0)) : null;
    }

    @Override
    public Collection<Quote> parseQuotes(InputStream input) {
        return parseQuotes(getQuotesAsStrings(input));
    }

    @Override
    public Quote parseQuote(String str) {
        String[] columns = str.replaceAll("\"", "").split(",");
        return new Quote(
                columns[0], columns[1],
                new BigDecimal(columns[2]).stripTrailingZeros(),
                parseDateTime(columns[3], columns[4]),
                new BigDecimal(columns[5]).stripTrailingZeros(),
                new BigDecimal(columns[6]).stripTrailingZeros());
    }

    public Collection<Quote> parseQuotes(List<String> strings) {
        assertNotNull(strings, "Parameter 'strings' is required");

        List<Quote> result = new ArrayList<>();
        for (String str : strings) {
            result.add(parseQuote(str));
        }
        return result;
    }

    static InputStream getInputStream(HttpURLConnection connection) throws IOException {
        assertNotNull(connection, "Parameter 'connection' is required");

        String contentEncoding = connection.getHeaderField(CONTENT_ENCODING);
        if (contentEncoding == null) {
            return connection.getInputStream();
        }
        if ("gzip".equals(contentEncoding)) {
            return new GZIPInputStream(connection.getInputStream());
        }
        throw new ApplicationException("Unexpected content encoding: " + contentEncoding);
    }

    static List<String> getQuotesAsStrings(String currencyPairs) {
        String path;
        try {
            path = BASE_URL + "?s="  + URLEncoder.encode(getSymbols(currencyPairs), UTF8) + "&f=" + URLEncoder.encode(columns, UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding:" + UTF8);
        }

        URL url;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Wrong URL: " + path);
        }

        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection)url.openConnection();
            connection.setUseCaches(false);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setRequestProperty(ACCEPT, ACCEPT_VALUE);
            connection.setRequestProperty(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE);
            connection.setRequestProperty(ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_VALUE);
            connection.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
            connection.connect();
        } catch (IOException e) {
            throw new ApplicationException("Address is wrong or host is not available now", e);
        }

        try (InputStream stream = getInputStream(connection)) {
            return getQuotesAsStrings(stream);
        } catch (IOException e) {
            throw new ApplicationException("Error while reading data from stream", e);
        } finally {
            connection.disconnect();
        }
    }

    static List<String> getQuotesAsStrings(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF8))) {
            String line = reader.readLine();
            List<String> result = new ArrayList<>();
            while(line != null) {
                result.add(line);
                line = reader.readLine();
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("I/o error", e);
        }
    }

    static String getSymbols(String currencyPairs) {
        String[] pairs = currencyPairs.split(",");
        StringBuilder result = new StringBuilder();
        for(String pair : pairs) {
            String symbol = map.get(pair);
            if (symbol != null) {
                result.append(symbol).append(',');
            }
        }
        String str = result.toString();
        return str.endsWith(",") ? str.substring(0, str.length() - 1) : str;
    }

    static DateTime parseDateTime(String date, String time) {
        DateTimeFormatter format = DateTimeFormat.forPattern("M/d/yyyy h:mma")
                .withLocale(Locale.US)
                .withZone(DateTimeZone.UTC);
        return format.parseDateTime(date + " " + time);
    }
}
