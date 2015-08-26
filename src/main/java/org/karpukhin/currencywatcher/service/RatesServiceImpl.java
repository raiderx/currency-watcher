package org.karpukhin.currencywatcher.service;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;
import org.karpukhin.currencywatcher.RatesUpdatedEvent;
import org.karpukhin.currencywatcher.dao.RatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class RatesServiceImpl implements RatesService {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private RatesDao ratesDao;

    @Override
    public void updateRates(List<Rate> rates) {
        List<Rate> changed = new ArrayList<>();
        for (Rate rate : rates) {
            Rate mapValue =
                    ratesDao.getLastRate(rate.getBankName(), rate.getCategory(), rate.getFromCurrency(), rate.getToCurrency());
            if (mapValue == null) {
                ratesDao.createRate(rate);
            } else {
                if (!equals(mapValue.getBuy(), rate.getBuy()) ||
                        !equals(mapValue.getSell(), rate.getSell())) {
                    updateDiff(rate, mapValue);
                    ratesDao.createRate(rate);
                    changed.add(rate);
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
        return ratesDao.getLastRates();
    }

    @Override
    public List<Rate> getRates(OperationCategories category) {
        return ratesDao.getLastRates(category);
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
