package org.karpukhin.currencywatcher.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 12.05.15
 */
@Repository
@Transactional
public class RatesDaoHibernateImpl implements RatesDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Rate getLastRate(String bankName, OperationCategories category, String fromCurrency, String toCurrency) {
        List<Rate> rates = getSession()
                .getNamedQuery("rates")
                .setParameter("bankName", bankName)
                .setParameter("category", category)
                .setParameter("fromCurrency", fromCurrency)
                .setParameter("toCurrency", toCurrency)
                .list();
        return rates.isEmpty() ? null : rates.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createRate(Rate rate) {
        getSession().save(rate);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<Rate> getLastRates(String bankName) {
        return getSession()
                .getNamedQuery("lastRates")
                .setString("bankName", bankName)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<Rate> getLastRates(String bankName, OperationCategories category) {
        return getSession()
                .getNamedQuery("lastRatesByCategory")
                .setString("bankName", bankName)
                .setString("category", category.name())
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<Rate> getRates(String bankName, OperationCategories category, String fromCurrency, String toCurrency, DateTime fromDate, DateTime toDate) {
        return getSession()
                .getNamedQuery("ratesByDate")
                .setParameter("bankName", bankName)
                .setParameter("category", category)
                .setParameter("fromCurrency", fromCurrency)
                .setParameter("toCurrency", toCurrency)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .list();
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
