package org.karpukhin.currencywatcher.dao;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Pavel Karpukhin
 * @since 13.05.15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:hibernate.xml"})
@Transactional
public class RatesDaoHibernateImplTest {

    @Autowired
    private RatesDao ratesDao;

    @Before
    public void setUp() {
    }

    @Test
    public void testCreateRate() {
        Rate rate = new Rate();
        rate.setBankName("bank");
        rate.setBankTime(new DateTime());
        rate.setCategory(OperationCategories.DEBIT_CARDS_TRANSFERS);
        rate.setFromCurrency("from");
        rate.setToCurrency("to");
        rate.setCreated(new DateTime());
        ratesDao.createRate(rate);

        assertThat(rate.getId(), is(not(nullValue())));
    }

    @Test
    public void testGetLastRate() {
        Rate rate = ratesDao.getLastRate("DUMMY_BANK", OperationCategories.DEBIT_CARDS_TRANSFERS, "USD", "RUB");
        assertThat(rate, is(not(nullValue())));
        assertThat(rate.getBankName(), is("DUMMY_BANK"));
        assertThat(rate.getCategory(), is(OperationCategories.DEBIT_CARDS_TRANSFERS));
        assertThat(rate.getFromCurrency(), is("USD"));
        assertThat(rate.getToCurrency(), is("RUB"));
    }

    @Test
    public void testGetLastRates() {
        List<Rate> result = ratesDao.getLastRates("DUMMY_BANK");
        assertThat(result, is(not(nullValue())));
    }

    @Test
    public void testGetLastRatesByCategory() {
        List<Rate> result = ratesDao.getLastRates("DUMMY_BANK", OperationCategories.DEBIT_CARDS_TRANSFERS);
        assertThat(result, is(not(nullValue())));
    }

    @Test
    public void testGetRatesByDate() {
        DateTime fromDate = new DateTime(2015, 2, 6, 0, 0);
        DateTime toDate = new DateTime(2015, 2, 7, 0, 0);
        List<Rate> result = ratesDao.getRates("DUMMY_BANK", OperationCategories.DEBIT_CARDS_TRANSFERS, "USD", "RUB", fromDate, toDate);
        assertThat(result, is(not(nullValue())));
    }
}
