package org.karpukhin.currencywatcher.web;

import org.karpukhin.currencywatcher.Rate;
import org.karpukhin.currencywatcher.RateWto;
import org.karpukhin.currencywatcher.dao.RatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 06.12.14
 */
@RestController
public class RatesController {

    @Autowired
    private RatesDao ratesDao;

    @RequestMapping(value = "/rates", method = RequestMethod.GET)
    public List<RateWto> getRates() {
        List<Rate> rates = ratesDao.getRates();
        return RateWto.convert(rates);
    }
}
