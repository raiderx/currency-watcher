package org.karpukhin.currencywatcher.service;

import com.google.common.eventbus.EventBus;
import org.karpukhin.currencywatcher.dao.QuotesDao;
import org.karpukhin.currencywatcher.model.QuotesUpdatedEvent;
import org.karpukhin.currencywatcher.model.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Pavel Karpukhin
 * @since 16.09.15
 */
@Service
public class QuotesServiceImpl implements QuotesService {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private QuotesDao quotesDao;

    @Override
    public void updateQuotes(Collection<Quote> quotes) {
        for (Quote quote : quotes) {
            quotesDao.createQuote(quote);
        }
        eventBus.post(new QuotesUpdatedEvent(quotes));
    }

    @Override
    public Quote getQuote(String currencyPair) {
        return null;
    }

    @Override
    public Collection<Quote> getQuotes() {
        return quotesDao.getLastQuotes();
    }
}
