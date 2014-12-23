package org.karpukhin.currencywatcher.web;

import org.karpukhin.currencywatcher.Rate;
import org.karpukhin.currencywatcher.RateWto;
import org.karpukhin.currencywatcher.exceptions.ApplicationException;
import org.karpukhin.currencywatcher.rateproviders.RatesProvider;
import org.karpukhin.currencywatcher.rateproviders.TcsRatesProviderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.karpukhin.currencywatcher.utils.AssertUtils.assertNotNull;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
@RestController
public class RatesController {

    static final String ACCEPT = "Accept";
    static final String ACCEPT_VALUE = "application/json, text/javascript, */*; q=0.01";
    static final String ACCEPT_ENCODING = "Accept-Encoding";
    static final String ACCEPT_ENCODING_VALUE = "gzip, deflate, sdch";
    static final String ACCEPT_LANGUAGE = "Accept-Language";
    static final String ACCEPT_LANGUAGE_VALUE = "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4";
    static final String REFERER = "Referer";
    static final String REFERER_VALUE = "https://www.tcsbank.ru/about/documents/exchange/";
    static final String USER_AGENT = "User-Agent";
    static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";
    static final String X_REQUESTED_WITH = "X-Requested-With";
    static final String X_REQUESTED_WITH_VALUE = "XMLHttpRequest";

    static final String CONTENT_ENCODING = "Content-Encoding";

    private static final String DEFAULT_URL = "https://www.tcsbank.ru/api-m1/v1/currency_rates";

    private RatesProvider ratesProvider;

    public RatesController() {
        ratesProvider = new TcsRatesProviderImpl();
    }

    @RequestMapping(value = "/rates", method = RequestMethod.GET)
    public List<RateWto> getRates() {
        URL url;
        try {
            url = new URL(DEFAULT_URL);
        } catch (MalformedURLException e) {
            throw new ApplicationException("Wrong URL: " + DEFAULT_URL, e);
        }
        //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("198.71.51.227", 80));
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection)url.openConnection(/*proxy*/);
            connection.setUseCaches(false);
            connection.setConnectTimeout(20000);
            connection.setRequestProperty(ACCEPT, ACCEPT_VALUE);
            connection.setRequestProperty(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE);
            connection.setRequestProperty(ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_VALUE);
            connection.setRequestProperty(REFERER, REFERER_VALUE);
            connection.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
            connection.setRequestProperty(X_REQUESTED_WITH, X_REQUESTED_WITH_VALUE);
            connection.connect();
        } catch (IOException e) {
            throw new ApplicationException("Address is wrong or host is not available now", e);
        }
        try (InputStream stream = getInputStream(connection)) {
            List<Rate> rates = ratesProvider.parseStream(stream);
            return RateWto.convert(rates);
        } catch (IOException e) {
            throw new ApplicationException("Error while reading data from stream", e);
        } finally {
            connection.disconnect();
        }
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
}
