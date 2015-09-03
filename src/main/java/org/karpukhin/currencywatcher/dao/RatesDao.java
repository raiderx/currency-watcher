package org.karpukhin.currencywatcher.dao;

import org.joda.time.DateTime;
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

    /**
     * Returns list of rates with given bank name
     *
     * @param bankName bank name
     * @return  list of rates with given bank name
     */
    List<Rate> getLastRates(String bankName);

    /**
     * Returns list of rates with given bank name and operation category
     *
     * @param bankName bank name
     * @param category operation category
     * @return  list of rates with given bank name and operation category
     */
    List<Rate> getLastRates(String bankName, OperationCategories category);

    List<Rate> getRates(String bankName, OperationCategories category, String fromCurrency, String toCurrency, DateTime fromDate, DateTime toDate);
}
