package org.karpukhin.currencywatcher.dao;

import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;

import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 12.02.15
 */
public interface RatesDao {

    Rate getLastRate(String bankName, OperationCategories category, String fromCurrency, String toCurrency);

    void createRate(Rate rate);

    List<Rate> getLastRates();

    List<Rate> getLastRates(OperationCategories category);
}
