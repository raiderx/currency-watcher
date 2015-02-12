package org.karpukhin.currencywatcher.dao;

import org.karpukhin.currencywatcher.Rate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 12.02.15
 */
@Repository
public class RatesDaoImpl implements RatesDao {

    private List<Rate> rates = new ArrayList<>();

    @Override
    public void updateRates(List<Rate> rates) {
        this.rates = new ArrayList<>(rates);
    }

    @Override
    public List<Rate> getRates() {
        return rates;
    }
}
