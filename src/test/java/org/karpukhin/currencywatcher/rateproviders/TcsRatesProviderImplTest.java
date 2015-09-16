package org.karpukhin.currencywatcher.rateproviders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.karpukhin.currencywatcher.exceptions.ApplicationException;
import org.karpukhin.currencywatcher.model.OperationCategories;
import org.karpukhin.currencywatcher.model.Rate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
public class TcsRatesProviderImplTest {

    private static final String UTF_8 = "UTF-8";

    private ObjectMapper mapper;
    private TcsRatesProviderImpl provider;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        provider = new TcsRatesProviderImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseStreamWithNull() throws IOException {
        provider.parseStream(null);
    }

    @Test(expected = ApplicationException.class)
    public void testParseStreamWithEmptyStream() throws IOException {
        try (InputStream stream = new ByteArrayInputStream("".getBytes(UTF_8))) {
            provider.parseStream(stream);
        }
    }

    @Test
    public void testParseStream() throws IOException {
        try (InputStream stream = TcsRatesProviderImplTest.class.getResourceAsStream("/tcs_rates.json")) {
            List<Rate> result = provider.parseStream(stream);
            assertThat(result, is(not(nullValue())));
            assertThat(result, hasSize(greaterThan(0)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseRootJsonNodeWithNull() {
        provider.parseRootJsonNode(null);
    }

    @Test(expected = ApplicationException.class)
    public void testParseRootJsonNodeWithEmptyNode() throws IOException {
        String str = mapper.writeValueAsString(new HashMap<>());
        JsonNode node = mapper.readTree(str);

        provider.parseRootJsonNode(node);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseRatesNodeWithNull() {
        provider.parseRatesNode(null);
    }

    @Test(expected = ApplicationException.class)
    public void testParseRatesNodeWithEmptyNode() throws IOException {
        String str = mapper.writeValueAsString(new HashMap<>());
        JsonNode node = mapper.readTree(str);

        provider.parseRatesNode(node);
    }

    @Test(expected = ApplicationException.class)
    public void testParseRatesNodeWithNullFieldValue() throws IOException {
        Map<String, Object> value = new HashMap<String, Object>() {{ put("rates", null); }};
        String str = mapper.writeValueAsString(value);
        JsonNode node = mapper.readTree(str);

        provider.parseRatesNode(node);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseRatesNodeElementWithNull() {
        provider.parseRatesNodeElement(null);
    }

    @Test(expected = ApplicationException.class)
    public void testParseRatesNodeElementWithEmptyNode() throws IOException {
        String str = mapper.writeValueAsString(new HashMap<>());
        JsonNode node = mapper.readTree(str);

        provider.parseRatesNodeElement(node);
    }

    @Test
    public void testParseRatesNodeElement() throws IOException {
        Map<String, Object> object = new HashMap<>();
        object.put("category", TcsRatesProviderImpl.TcsOperationCategories.DebitCardsTransfers);
        object.put("fromCurrency", new HashMap<String, Object>() {{ put("name", "USD"); }});
        object.put("toCurrency", new HashMap<String, Object>() {{ put("name", "RUB"); }});
        object.put("buy", "50.5");
        object.put("sell", "52.5");
        String value = mapper.writeValueAsString(object);
        JsonNode node = mapper.readTree(value);

        Rate result = provider.parseRatesNodeElement(node);

        assertThat(result, is(not(nullValue())));
        assertThat(result.getCategory(), is(equalTo(OperationCategories.DEBIT_CARDS_TRANSFERS)));
        assertThat(result.getFromCurrency(), is(equalTo("USD")));
        assertThat(result.getToCurrency(), is(equalTo("RUB")));
        assertThat(result.getBuy(), is(equalTo(new BigDecimal("50.5"))));
        assertThat(result.getSell(), is(equalTo(new BigDecimal("52.5"))));
        assertThat(result.getBankTime(), is(nullValue()));
    }
}
