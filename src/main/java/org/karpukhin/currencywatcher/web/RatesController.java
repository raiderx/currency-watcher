package org.karpukhin.currencywatcher.web;

import org.karpukhin.currencywatcher.Rate;
import org.karpukhin.currencywatcher.RateWto;
import org.karpukhin.currencywatcher.exceptions.ApplicationException;
import org.karpukhin.currencywatcher.rateproviders.RatesProvider;
import org.karpukhin.currencywatcher.rateproviders.TcsRatesProviderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
@RestController
public class RatesController {

    private RatesProvider ratesProvider;

    public RatesController() {
        ratesProvider = new TcsRatesProviderImpl();
    }

    @RequestMapping(value = "/rates", method = RequestMethod.GET)
    public List<RateWto> getRates() {
        URL url;
        try {
            url = new URL("https://www.tcsbank.ru/api-m1/v1/currency_rates/");
        } catch (MalformedURLException e) {
            throw new ApplicationException("", e);
        }
        try (InputStream stream = url.openStream()) {
            List<Rate> rates = ratesProvider.parseStream(stream);
            return RateWto.convert(rates);
        } catch (IOException e) {
            throw new ApplicationException("Error while reading data from stream", e);
        }
    }
}
