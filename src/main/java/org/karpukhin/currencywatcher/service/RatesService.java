package org.karpukhin.currencywatcher.service;

import org.karpukhin.currencywatcher.model.OperationCategories;
import org.karpukhin.currencywatcher.model.Rate;

import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 12.02.15
 */
public interface RatesService {

    void updateRates(List<Rate> rates);

    List<Rate> getRates();

    List<Rate> getRates(OperationCategories category);

    List<Rate> getCurrencyPairDayRates(String currencyPair, OperationCategories category);

    List<Rate> getCurrencyPairWeekRates(String currencyPair, OperationCategories category);

    List<Rate> getCurrencyPairMonthRates(String currencyPair, OperationCategories category);
}
