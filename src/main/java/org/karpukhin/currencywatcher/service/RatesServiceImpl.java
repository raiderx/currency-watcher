package org.karpukhin.currencywatcher.service;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;
import org.karpukhin.currencywatcher.RatesUpdatedEvent;
import org.karpukhin.currencywatcher.dao.RatesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Karpukhin
 * @since 12.02.15
 */
@Service
@Transactional
public class RatesServiceImpl implements RatesService {

    private static final Logger logger = LoggerFactory.getLogger(RatesService.class);

    private static final String BANK_NAME = "TCS";

    @Autowired
    private EventBus eventBus;

    @Autowired
    private RatesDao ratesDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRates(List<Rate> rates) {
        List<Rate> changed = new ArrayList<>();
        for (Rate newRate : rates) {
            Rate oldRate = ratesDao.getLastRate(newRate.getBankName(), newRate.getCategory(),
                                                newRate.getFromCurrency(), newRate.getToCurrency());
            if (oldRate == null) {
                ratesDao.createRate(newRate);
            } else {
                if (!equals(oldRate.getBuy(), newRate.getBuy()) || !equals(oldRate.getSell(), newRate.getSell())) {
                    logger.info("Rate was updated: {}/{}, old: {}, {}, new: {}, {}",
                            newRate.getFromCurrency(), newRate.getToCurrency(), oldRate.getBuy(), oldRate.getSell(), newRate.getBuy(), newRate.getSell());
                    updateDiff(newRate, oldRate);
                    ratesDao.createRate(newRate);
                    changed.add(newRate);
                }
            }
        }
        if (!changed.isEmpty()) {
            Map<OperationCategories, List<Rate>> grouped = groupByCategory(changed);
            for (Map.Entry<OperationCategories, List<Rate>> entry : grouped.entrySet()) {
                RatesUpdatedEvent event = new RatesUpdatedEvent(entry.getKey().name().toLowerCase(), entry.getValue());
                eventBus.post(event);
            }
        }
    }

    @Override
    public List<Rate> getRates() {
        return ratesDao.getLastRates(BANK_NAME);
    }

    @Override
    public List<Rate> getRates(OperationCategories category) {
        return ratesDao.getLastRates(BANK_NAME, category);
    }

    @Override
    public List<Rate> getCurrencyPairDayRates(String currencyPair) {
        DateTime to = LocalDate.now().plusDays(1).toDateTimeAtStartOfDay();
        DateTime from = to.minusDays(1);
        String[] parts = currencyPair.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected currency pair but got: " + currencyPair);
        }
        return ratesDao.getRates(BANK_NAME, OperationCategories.DEBIT_CARDS_TRANSFERS, parts[0], parts[1], from, to);
    }

    @Override
    public List<Rate> getCurrencyPairWeekRates(String currencyPair) {
        DateTime to = LocalDate.now().plusDays(1).toDateTimeAtStartOfDay();
        DateTime from = to.minusWeeks(1);
        String[] parts = currencyPair.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected currency pair but got: " + currencyPair);
        }
        return ratesDao.getRates(BANK_NAME, OperationCategories.DEBIT_CARDS_TRANSFERS, parts[0], parts[1], from, to);
    }

    @Override
    public List<Rate> getCurrencyPairMonthRates(String currencyPair) {
        DateTime to = LocalDate.now().plusDays(1).toDateTimeAtStartOfDay();
        DateTime from = to.minusMonths(1);
        String[] parts = currencyPair.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected currency pair but got: " + currencyPair);
        }
        return ratesDao.getRates(BANK_NAME, OperationCategories.DEBIT_CARDS_TRANSFERS, parts[0], parts[1], from, to);
    }

    static <T extends Comparable> boolean equals(T first, T second) {
        return first == second || (first != null && second != null && first.compareTo(second) == 0);
    }

    static void updateDiff(Rate newRate, Rate oldRate) {
        BigDecimal buyDiff = getDiff(newRate.getBuy(), oldRate.getBuy());
        BigDecimal sellDiff = getDiff(newRate.getSell(), oldRate.getSell());
        newRate.setBuyDiff(buyDiff);
        newRate.setLongBuyDiff(getLongDiff(oldRate.getLongBuyDiff(), buyDiff));
        newRate.setSellDiff(sellDiff);
        newRate.setLongSellDiff(getLongDiff(oldRate.getLongSellDiff(), sellDiff));
    }

    static BigDecimal getDiff(BigDecimal newValue, BigDecimal oldValue) {
        if (oldValue != null && newValue != null) {
            return newValue.subtract(oldValue);
        }
        return BigDecimal.ZERO;
    }

    static BigDecimal getLongDiff(BigDecimal longDiff, BigDecimal newDiff) {
        if (longDiff == null && newDiff == null) {
            return BigDecimal.ZERO;
        }
        if (longDiff == null || BigDecimal.ZERO.equals(longDiff)) {
            return newDiff;
        }
        if (newDiff == null || BigDecimal.ZERO.equals(newDiff)) {
            return longDiff;
        }
        if (longDiff.multiply(newDiff).compareTo(BigDecimal.ZERO) < 0) {
            return newDiff;
        }
        return longDiff.add(newDiff);
    }

    static Map<OperationCategories, List<Rate>> groupByCategory(List<Rate> rates) {
        Preconditions.checkArgument(rates != null, "Parameter 'rates' can not be null");

        Map<OperationCategories, List<Rate>> result = new HashMap<>();
        for (Rate rate : rates) {
            List<Rate> value = result.get(rate.getCategory());
            if (value == null) {
                value = new ArrayList<>();
                result.put(rate.getCategory(), value);
            }
            value.add(rate);
        }
        return result;
    }
}
