package org.karpukhin.currencywatcher.rateproviders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.NullNode;
import org.joda.time.DateTime;
import org.karpukhin.currencywatcher.exceptions.ApplicationException;
import org.karpukhin.currencywatcher.model.OperationCategories;
import org.karpukhin.currencywatcher.model.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static org.karpukhin.currencywatcher.utils.AssertUtils.assertNotNull;
import static org.karpukhin.currencywatcher.utils.AssertUtils.assertTrue;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
public class TcsRatesProviderImpl implements RatesProvider {

    private static final Logger logger = LoggerFactory.getLogger(TcsRatesProviderImpl.class);

    private static final String DEFAULT_URL = "https://www.tinkoff.ru/api/v1/currency_rates/";

    static final int CONNECT_TIMEOUT = 20000;

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
    static final String GZIP = "gzip";

    private static final String CATEGORY_FIELD = "category";
    private static final String FROM_CURRENCY_FIELD = "fromCurrency";
    private static final String TO_CURRENCY_FIELD = "toCurrency";
    private static final String BUY_FIELD = "buy";
    private static final String SELL_FIELD = "sell";

    private static final String MILLISECONDS_EXPR = "/payload/lastUpdate/milliseconds";
    private static final String RATES_EXPR = "/payload/rates";
    private static final String CATEGORY_EXPR = "/category";
    private static final String FROM_CURRENCY_EXPR = "/fromCurrency/name";
    private static final String TO_CURRENCY_EXPR = "/toCurrency/name";
    private static final String BUY_EXPR = "/buy";
    private static final String SELL_EXPR = "/sell";

    private static final ObjectReader reader = new ObjectMapper()/*.disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)*/.reader();

    @Override
    public Collection<Rate> getRates() {
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
            connection.setConnectTimeout(CONNECT_TIMEOUT);
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
            return parseStream(stream);
        } catch (IOException e) {
            throw new ApplicationException("Error while reading data from stream", e);
        } finally {
            connection.disconnect();
        }
    }

    public List<Rate> parseStream(InputStream stream) throws IOException {
        assertNotNull(stream, "Parameter 'stream' can not be null");

        JsonNode rootNode;
        try {
            rootNode = reader.readTree(stream);
        } catch (JsonProcessingException e) {
            throw new ApplicationException("Can not read JSON from stream", e);
        }
        if (rootNode == null) {
            return Collections.emptyList();
        }
        return parseRootJsonNode(rootNode);
    }

    List<Rate> parseRootJsonNode(JsonNode rootNode) {
        assertNotNull(rootNode, "Parameter 'rootNode' can not be null");
        assertTrue(rootNode != NullNode.instance, "JSON can not be empty");

        JsonNode millisecondsNode = rootNode.at(MILLISECONDS_EXPR);
        if (millisecondsNode.isMissingNode() || !millisecondsNode.isLong()) {
            throw new ApplicationException("Field 'milliseconds' is not a long or was not found");
        }
        DateTime bankTime = new DateTime(millisecondsNode.asLong());
        List<Rate> rates = parseRatesNode(rootNode.at(RATES_EXPR));
        for (Rate rate : rates) {
            rate.setBankTime(bankTime);
        }
        return rates;
    }

    List<Rate> parseRatesNode(JsonNode ratesNode) {
        assertNotNull(ratesNode, "Parameter 'ratesNode' can not be null");
        assertTrue(ratesNode != NullNode.instance, "Field 'rates' can not be null");

        if (!ratesNode.isMissingNode() && ratesNode.isArray()) {
            List<Rate> result = new ArrayList<>();
            for (int i = 0; i < ratesNode.size(); ++i) {
                JsonNode rateNode = ratesNode.get(i);
                Rate rate = parseRatesNodeElement(rateNode);
                if (rate != null) {
                    result.add(rate);
                }
            }
            return result;
        }
        throw new ApplicationException("Field 'rates' does not exist or is not an array");
    }

    Rate parseRatesNodeElement(JsonNode rateNode) {
        assertNotNull(rateNode, "Parameter 'rateNode' can not be null");
        assertTrue(rateNode != NullNode.instance, "Element of field 'rates' can not be null");

        logger.debug("Parsing rate node: {}", rateNode);

        if (!rateNode.hasNonNull(CATEGORY_FIELD) || !rateNode.hasNonNull(FROM_CURRENCY_FIELD) || !rateNode.hasNonNull(TO_CURRENCY_FIELD)) {
            throw new ApplicationException("Format of field 'rates' was changed: " +
                    "can not find one of fields 'category', 'fromCurrency' or 'toCurrency'");
        }
        Rate rate = new Rate();
        rate.setBankName("TCS");
        rate.setBuyDiff(BigDecimal.ZERO);
        rate.setSellDiff(BigDecimal.ZERO);
        rate.setCreated(new DateTime());

        String category = rateNode.at(CATEGORY_EXPR).asText();
        try {
            TcsOperationCategories tcsCategory = TcsOperationCategories.valueOf(category);
            rate.setCategory(TcsOperationCategories.getOperationCategory(tcsCategory));
        } catch (IllegalArgumentException e) {
            throw new ApplicationException("Unexpected value for field '" + CATEGORY_FIELD + "': " + category);
        }

        rate.setFromCurrency(rateNode.at(FROM_CURRENCY_EXPR).asText());
        rate.setToCurrency(rateNode.at(TO_CURRENCY_EXPR).asText());

        if (rateNode.hasNonNull(BUY_FIELD)) {
            rate.setBuy(stringToBigDecimal(rateNode.at(BUY_EXPR).asText()));
        }

        if (rateNode.hasNonNull(SELL_FIELD)) {
            rate.setSell(stringToBigDecimal(rateNode.at(SELL_EXPR).asText()));
        }

        return rate;
    }

    static InputStream getInputStream(URLConnection connection) throws IOException {
        assertNotNull(connection, "Parameter 'connection' is required");

        String contentEncoding = connection.getHeaderField(CONTENT_ENCODING);
        if (contentEncoding == null) {
            return connection.getInputStream();
        }
        if (GZIP.equals(contentEncoding)) {
            return new GZIPInputStream(connection.getInputStream());
        }
        throw new ApplicationException("Unexpected content encoding: " + contentEncoding);
    }

    /**
     * Returns instance of BigDecimal with number converted from given string value
     *
     * @param str string representation of number
     * @return instance of BigDecimal with number converted from given string value
     */
    static BigDecimal stringToBigDecimal(String str) {
        return new BigDecimal(str).setScale(2, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros();
    }

    enum TcsOperationCategories {

        // Для вкладов

        DepositClosingBenefit,  // Перевод с вклада при закрытии Вклада в связи
                                // с окончанием срока и/или после пролонгации

        DepositClosing,         // Перевод с Вклада при частичном изъятии Вклада
                                // и/или досрочном закрытии Вклада до пролонгации

        DepositPayments,        // Зачисление на Вклад

        // Для дебетовых карт

        DebitCardsTransfers,    // Операции по Картсчету, в том числе через Центр
                                // обслуживания клиентов, Интернет-банк и Мобильное
                                // приложение, кроме операций с использованием карты (ее реквизитов)

        DebitCardsOperations,   // Операции с использованием карты (ее реквизитов)

        // Для кредитных карт

        CreditCardsTransfers,   // Операции по договору кредитной карты, в т.ч. через
                                // Центр обслуживания клиентов, Интернет-Банк и Мобильный приложение,
                                // кроме операций с использованием карты (ее реквизитов)

        CreditCardsOperations,  // Операции с использованием карты (ее реквизитов)

        // Для электронных денежных средств

        PrepaidCardsTransfers,  // Операции по зачислению денежных средств
                                // на договор электронных денежных средств

        PrepaidCardsOperations, // Операции с использованием карты (ее реквизитов)

        // Для накопительных счетов

        SavingAccountTransfers; // Операции по Накопительному счету, в т.ч. через
                                // Центр обслуживания клиентов, Интернет-Банк и
                                // мобильное приложение

        private static final Map<TcsOperationCategories, OperationCategories> map = new HashMap<>();

        static {
            map.put(DepositClosingBenefit, OperationCategories.DEPOSIT_CLOSING_BENEFIT);
            map.put(DepositClosing, OperationCategories.DEPOSIT_CLOSING);
            map.put(DepositPayments, OperationCategories.DEPOSIT_PAYMENTS);
            map.put(DebitCardsTransfers, OperationCategories.DEBIT_CARDS_TRANSFERS);
            map.put(DebitCardsOperations, OperationCategories.DEBIT_CARDS_OPERATIONS);
            map.put(CreditCardsTransfers, OperationCategories.CREDIT_CARDS_TRANSFERS);
            map.put(CreditCardsOperations, OperationCategories.CREDIT_CARDS_OPERATIONS);
            map.put(PrepaidCardsTransfers, OperationCategories.PREPAID_CARDS_TRANSFERS);
            map.put(PrepaidCardsOperations, OperationCategories.PREPAID_CARDS_OPERATIONS);
            map.put(SavingAccountTransfers, OperationCategories.SAVING_ACCOUNT_TRANSFERS);
        }

        static OperationCategories getOperationCategory(TcsOperationCategories category) {
            assertNotNull(category, "Parameter 'category' is required");

            return map.get(category);
        }
    }
}
