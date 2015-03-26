package org.karpukhin.currencywatcher.dao;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;
import org.karpukhin.currencywatcher.RatesUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pavel Karpukhin
 * @since 12.02.15
 */
@Repository
public class RatesDaoImpl implements RatesDao {

    @Autowired
    private EventBus eventBus;

    private Map<MapKey, Rate> latestRates = new ConcurrentHashMap<>();

    @Override
    public void updateRates(List<Rate> rates) {
        List<Rate> changed = new ArrayList<>();
        for (Rate rate : rates) {
            MapKey mapKey = new MapKey(rate);
            Rate mapValue = latestRates.get(mapKey);
            if (mapValue == null) {
                latestRates.put(mapKey, rate);
            } else {
                if (!Objects.equals(mapValue.getBuy(), rate.getBuy()) ||
                        !Objects.equals(mapValue.getSell(), rate.getSell())) {
                    updateDiff(rate, mapValue);
                    latestRates.put(mapKey, rate);
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
        return new ArrayList<>(latestRates.values());
    }

    @Override
    public List<Rate> getRates(OperationCategories category) {
        List<Rate> result = new ArrayList<>();
        for (Rate rate : latestRates.values()) {
            if (Objects.equals(rate.getCategory(), category)) {
                result.add(rate);
            }
        }
        return result;
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

    static class MapKey {

        private String bankName;
        private OperationCategories category;
        private String fromCurrency;
        private String toCurrency;

        MapKey(Rate rate) {
            this.bankName = rate.getBankName();
            this.category = rate.getCategory();
            this.fromCurrency = rate.getFromCurrency();
            this.toCurrency = rate.getToCurrency();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MapKey mapKey = (MapKey) o;

            if (bankName != null ? !bankName.equals(mapKey.bankName) : mapKey.bankName != null) return false;
            if (category != mapKey.category) return false;
            if (fromCurrency != null ? !fromCurrency.equals(mapKey.fromCurrency) : mapKey.fromCurrency != null)
                return false;
            if (toCurrency != null ? !toCurrency.equals(mapKey.toCurrency) : mapKey.toCurrency != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = bankName != null ? bankName.hashCode() : 0;
            result = 31 * result + (category != null ? category.hashCode() : 0);
            result = 31 * result + (fromCurrency != null ? fromCurrency.hashCode() : 0);
            result = 31 * result + (toCurrency != null ? toCurrency.hashCode() : 0);
            return result;
        }
    }
}
