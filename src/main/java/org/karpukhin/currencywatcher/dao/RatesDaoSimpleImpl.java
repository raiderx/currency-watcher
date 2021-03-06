package org.karpukhin.currencywatcher.dao;

import org.joda.time.DateTime;
import org.karpukhin.currencywatcher.model.OperationCategories;
import org.karpukhin.currencywatcher.model.Rate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pavel Karpukhin
 * @since 13.05.15
 */
public class RatesDaoSimpleImpl implements RatesDao {

    private Map<MapKey, Rate> latestRates = new ConcurrentHashMap<>();
    private List<Rate> rates = new ArrayList<>();

    @Override
    public Rate getLastRate(String bankName, OperationCategories category, String fromCurrency, String toCurrency) {
        MapKey mapKey = new MapKey(bankName, category, fromCurrency, toCurrency);
        return latestRates.get(mapKey);
    }

    @Override
    public void createRate(Rate rate) {
        latestRates.put(new MapKey(rate), rate);
        rates.add(rate);
    }

    @Override
    public List<Rate> getLastRates(String bankName) {
        List<Rate> result = new ArrayList<>();
        for (Rate rate : latestRates.values()) {
            if (Objects.equals(rate.getBankName(), bankName)) {
                result.add(rate);
            }
        }
        return result;
    }

    @Override
    public List<Rate> getLastRates(String bankName, OperationCategories category) {
        List<Rate> result = new ArrayList<>();
        for (Rate rate : latestRates.values()) {
            if (Objects.equals(rate.getBankName(), bankName) && Objects.equals(rate.getCategory(), category)) {
                result.add(rate);
            }
        }
        return result;
    }

    @Override
    public List<Rate> getRates(String bankName, OperationCategories category, String fromCurrency, String toCurrency, DateTime fromDate, DateTime toDate) {
        List<Rate> result = new ArrayList<>();
        for(Rate rate : rates) {
            if (rate.getBankName().equals(bankName) && rate.getCategory() == category &&
                    rate.getFromCurrency().equals(fromCurrency) && rate.getToCurrency().equals(toCurrency) &&
                    rate.getBankTime().isAfter(fromDate) && rate.getBankTime().isBefore(toDate)) {
                result.add(rate);
            }
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

        MapKey(String bankName, OperationCategories category, String fromCurrency, String toCurrency) {
            this.bankName = bankName;
            this.category = category;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
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
