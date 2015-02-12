package org.karpukhin.currencywatcher.dao;

import org.karpukhin.currencywatcher.Rate;

import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 12.02.15
 */
public interface RatesDao {

    void updateRates(List<Rate> rates);

    List<Rate> getRates();
}
