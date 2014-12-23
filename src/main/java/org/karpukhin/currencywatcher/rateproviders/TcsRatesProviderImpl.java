package org.karpukhin.currencywatcher.rateproviders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.joda.time.DateTime;
import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;
import org.karpukhin.currencywatcher.exceptions.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.karpukhin.currencywatcher.utils.AssertUtils.assertNotNull;
import static org.karpukhin.currencywatcher.utils.AssertUtils.assertTrue;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
public class TcsRatesProviderImpl implements RatesProvider {

    private static final Logger logger = LoggerFactory.getLogger(TcsRatesProviderImpl.class);

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

    static final String[] EXPECTED_CURRENCIES = {"EUR", "RUB", "USD"};

    public List<Rate> parseStream(InputStream stream) throws IOException {
        assertNotNull(stream, "Parameter 'stream' can not be null");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(stream);
        } catch (JsonProcessingException e) {
            throw new ApplicationException("Can not read JSON from stream", e);
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

        if (!rateNode.hasNonNull(CATEGORY_FIELD) || !rateNode.hasNonNull(FROM_CURRENCY_FIELD) || !rateNode.hasNonNull(TO_CURRENCY_FIELD)) {
            throw new ApplicationException("Format of field 'rates' was changed: " +
                    "can not find one of fields 'category', 'fromCurrency' or 'toCurrency'");
        }
        Rate rate = new Rate();
        rate.setCreated(new DateTime());

        String category = rateNode.at(CATEGORY_EXPR).asText();
        try {
            TcsOperationCategories tcsCategory = TcsOperationCategories.valueOf(category);
            rate.setCategory(TcsOperationCategories.getOperationCategory(tcsCategory));
        } catch (IllegalArgumentException e) {
            throw new ApplicationException("Unexpected value for field '" + CATEGORY_FIELD + "': " + category);
        }

        String fromCurrency = rateNode.at(FROM_CURRENCY_EXPR).asText();
        if (Arrays.binarySearch(EXPECTED_CURRENCIES, fromCurrency) >= 0) {
            rate.setFromCurrency(fromCurrency);
        } else {
            logger.warn("Unexpected value of field '" + FROM_CURRENCY_FIELD + "': {}", fromCurrency);
        }

        String toCurrency = rateNode.at(TO_CURRENCY_EXPR).asText();
        if (Arrays.binarySearch(EXPECTED_CURRENCIES, fromCurrency) >= 0) {
            rate.setToCurrency(toCurrency);
        } else {
            logger.warn("Unexpected value of field '" + TO_CURRENCY_FIELD + "': {}", toCurrency);
        }

        if (rateNode.hasNonNull(BUY_FIELD)) {
            rate.setBuy(new BigDecimal(rateNode.at(BUY_EXPR).asText()));
        }

        if (rateNode.hasNonNull(SELL_FIELD)) {
            rate.setSell(new BigDecimal(rateNode.at(SELL_EXPR).asText()));
        }

        return rate;
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
