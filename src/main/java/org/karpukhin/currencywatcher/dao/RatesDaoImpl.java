package org.karpukhin.currencywatcher.dao;

import org.karpukhin.currencywatcher.Difference;
import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pavel Karpukhin
 * @since 12.02.15
 */
@Repository
public class RatesDaoImpl implements RatesDao {

    private List<Rate> rates = new ArrayList<>();
    private Map<MapKey, Rate> latestRates = new HashMap<>();

    @Override
    public void updateRates(List<Rate> rates) {
        for (Rate rate : rates) {
            MapKey mapKey = new MapKey(rate);
            Rate mapValue = this.latestRates.get(mapKey);
            if (mapValue == null) {
                this.latestRates.put(mapKey, rate);
                this.rates.add(rate);
            } else {
                if (!Objects.equals(mapValue.getBuy(), rate.getBuy()) ||
                        !Objects.equals(mapValue.getSell(), rate.getSell())) {
                    rate.setBuyDifference(getDifference(rate.getBuy(), mapValue.getBuy()));
                    rate.setSellDifference(getDifference(rate.getSell(), mapValue.getSell()));
                    this.latestRates.put(mapKey, rate);
                    this.rates.add(rate);
                }
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

    static Difference getDifference(BigDecimal first, BigDecimal second) {
        if (first != null && second != null) {
            int cmp = first.compareTo(second);
            if (cmp > 0) {
                return Difference.GREATER;
            }
            if (cmp < 0) {
                return Difference.LESS;
            }
        }
        return Difference.EQUAL;
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
