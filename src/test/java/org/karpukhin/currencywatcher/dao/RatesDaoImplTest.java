package org.karpukhin.currencywatcher.dao;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Pavel Karpukhin
 * @since 19.03.15
 */
public class RatesDaoImplTest {

    @Test
    public void testGetLongDiffWhenBothNull() {
        BigDecimal result = RatesDaoImpl.getLongDiff(null, null);
        assertThat(result, is(BigDecimal.ZERO));
    }

    @Test
    public void testGetLongDiffWhenFirstNull() {
        BigDecimal second = BigDecimal.valueOf(123.45);
        BigDecimal result = RatesDaoImpl.getLongDiff(null, second);
        assertThat(result, is(second));
    }

    @Test
    public void testGetLongDiffWhenFirstZero() {
        BigDecimal second = BigDecimal.valueOf(123.45);
        BigDecimal result = RatesDaoImpl.getLongDiff(BigDecimal.ZERO, second);
        assertThat(result, is(second));
    }

    @Test
    public void testGetLongDiffWhenSecondNull() {
        BigDecimal first = BigDecimal.valueOf(123.45);
        BigDecimal result = RatesDaoImpl.getLongDiff(first, null);
        assertThat(result, is(first));
    }

    @Test
    public void testGetLongDiffWhenSecondZero() {
        BigDecimal first = BigDecimal.valueOf(123.45);
        BigDecimal result = RatesDaoImpl.getLongDiff(first, BigDecimal.ZERO);
        assertThat(result, is(first));
    }

    @Test
    public void testGetLongDiffWhenBothPositive() {
        BigDecimal first = BigDecimal.valueOf(12.35);
        BigDecimal second = BigDecimal.valueOf(34.56);
        BigDecimal result = RatesDaoImpl.getLongDiff(first, second);
        assertThat(result, is(first.add(second)));
    }

    @Test
    public void testGetLongDiffWhenBothNegative() {
        BigDecimal first = BigDecimal.valueOf(-12.35);
        BigDecimal second = BigDecimal.valueOf(-34.56);
        BigDecimal result = RatesDaoImpl.getLongDiff(first, second);
        assertThat(result, is(first.add(second)));
    }

    @Test
    public void testGetLongDiffWhenDiffSigns() {
        BigDecimal first = BigDecimal.valueOf(12.35);
        BigDecimal second = BigDecimal.valueOf(-34.56);
        BigDecimal result = RatesDaoImpl.getLongDiff(first, second);
        assertThat(result, is(second));
    }
}
