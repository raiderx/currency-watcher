package org.karpukhin.currencywatcher.web;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.karpukhin.currencywatcher.model.OperationCategories;
import org.karpukhin.currencywatcher.model.Quote;
import org.karpukhin.currencywatcher.model.QuotesUpdatedEvent;
import org.karpukhin.currencywatcher.model.Rate;
import org.karpukhin.currencywatcher.model.RatesUpdatedEvent;
import org.karpukhin.currencywatcher.service.QuotesService;
import org.karpukhin.currencywatcher.service.RatesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
@RestController
public class RatesController {

    private static final Logger logger = LoggerFactory.getLogger(RatesController.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private QuotesService quotesService;

    @Autowired
    private RatesService ratesService;

    @Autowired
    private SimpMessagingTemplate template;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    @PreDestroy
    public void destroy() {
        eventBus.unregister(this);
    }

    @RequestMapping(value = "/rates", method = RequestMethod.GET)
    public List<RateWto> getRates(String category) {
        if (StringUtils.hasText(category)) {
            OperationCategories cat = OperationCategories.valueOf(category);
            List<Rate> rates = ratesService.getRates(cat);
            return RateWto.convert(rates);
        }
        return RateWto.convert(ratesService.getRates());
    }

    @MessageMapping("/queue/category")
    public void getCategoryRates(String category) {
        if (StringUtils.hasText(category)) {
            OperationCategories cat = OperationCategories.valueOf(category.toUpperCase());
            List<Rate> rates = ratesService.getRates(cat);
            List<RateWto> wtos = RateWto.convert(rates);
            template.convertAndSend("/topic/category/" + category.toLowerCase(), wtos);
        }
    }

    @MessageMapping("/queue/quotes")
    public void getQuotes() {
        Collection<Quote> quotes = quotesService.getQuotes();
        template.convertAndSend("/topic/quotes", QuoteWto.convert(quotes));
    }

    @Subscribe
    public void sendRate(RatesUpdatedEvent event) {
        List<RateWto> rates = RateWto.convert(event.getRates());
        template.convertAndSend("/topic/category/" + event.getCategory(), rates);
    }

    @Subscribe
    public void sendQuotes(QuotesUpdatedEvent event) {
        template.convertAndSend("/topic/quotes", QuoteWto.convert(event.getQuotes()));
    }

    @RequestMapping(value = "/currencypairrates", method = RequestMethod.GET)
    public List<SimpleRateWto> getCurrencyPairRates(String currencyPair, String category, String period) {
        List<Rate> rates = new ArrayList<>();
        OperationCategories cat = OperationCategories.valueOf(category.toUpperCase());
        if ("day".equals(period)) {
            rates = ratesService.getCurrencyPairDayRates(currencyPair, cat);
        } else if ("week".equals(period)) {
            rates = ratesService.getCurrencyPairWeekRates(currencyPair, cat);
        } else if ("month".equals(period)) {
            rates = ratesService.getCurrencyPairMonthRates(currencyPair, cat);
        }
        return SimpleRateWto.convert(rates);
    }
}
