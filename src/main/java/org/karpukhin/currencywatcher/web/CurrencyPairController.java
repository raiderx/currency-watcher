package org.karpukhin.currencywatcher.web;

import org.karpukhin.currencywatcher.service.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Pavel Karpukhin
 * @since 03.09.15
 */
@Controller
public class CurrencyPairController {

    @Autowired
    private RatesService ratesService;

    @RequestMapping(value = "/currencies", method = RequestMethod.GET)
    public ModelAndView index(String currencyPair) {
        String pair = currencyPair.replace('-', '/').toUpperCase();
        return new ModelAndView("currency")
                .addObject("currencyPair", pair);
    }
}
