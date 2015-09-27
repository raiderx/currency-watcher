package org.karpukhin.currencywatcher.rateproviders;

import org.karpukhin.currencywatcher.model.Rate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author Pavel Karpukhin
 * @since 07.12.14
 */
public interface RatesProvider {

    Collection<Rate> getRates();

    List<Rate> parseStream(InputStream stream) throws IOException;
}
