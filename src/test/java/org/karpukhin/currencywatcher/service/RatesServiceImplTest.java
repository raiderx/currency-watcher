package org.karpukhin.currencywatcher.service;

import org.junit.Test;
import org.karpukhin.currencywatcher.OperationCategories;
import org.karpukhin.currencywatcher.Rate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Pavel Karpukhin
 * @since 19.03.15
 */
public class RatesServiceImplTest {
    @Test
    public void testGetLongDiffWhenBothNull() {
        BigDecimal result = RatesServiceImpl.getLongDiff(null, null);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(BigDecimal.ZERO));
    }

    @Test
    public void testGetLongDiffWhenFirstNull() {
        BigDecimal second = BigDecimal.valueOf(123.45);
        BigDecimal result = RatesServiceImpl.getLongDiff(null, second);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(second));
    }

    @Test
    public void testGetLongDiffWhenFirstZero() {
        BigDecimal second = BigDecimal.valueOf(123.45);
        BigDecimal result = RatesServiceImpl.getLongDiff(BigDecimal.ZERO, second);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(second));
    }

    @Test
    public void testGetLongDiffWhenSecondNull() {
        BigDecimal first = BigDecimal.valueOf(123.45);
        BigDecimal result = RatesServiceImpl.getLongDiff(first, null);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(first));
    }

    @Test
    public void testGetLongDiffWhenSecondZero() {
        BigDecimal first = BigDecimal.valueOf(123.45);
        BigDecimal result = RatesServiceImpl.getLongDiff(first, BigDecimal.ZERO);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(first));
    }

    @Test
    public void testGetLongDiffWhenBothPositive() {
        BigDecimal first = BigDecimal.valueOf(12.35);
        BigDecimal second = BigDecimal.valueOf(34.56);
        BigDecimal result = RatesServiceImpl.getLongDiff(first, second);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(first.add(second)));
    }

    @Test
    public void testGetLongDiffWhenBothNegative() {
        BigDecimal first = BigDecimal.valueOf(-12.35);
        BigDecimal second = BigDecimal.valueOf(-34.56);
        BigDecimal result = RatesServiceImpl.getLongDiff(first, second);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(first.add(second)));
    }

    @Test
    public void testGetLongDiffWhenDiffSigns() {
        BigDecimal first = BigDecimal.valueOf(12.35);
        BigDecimal second = BigDecimal.valueOf(-34.56);
        BigDecimal result = RatesServiceImpl.getLongDiff(first, second);
        assertThat(result, is(not(nullValue())));
        assertThat(result, is(second));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupByCategoryWhenNull() {
        RatesServiceImpl.groupByCategory(null);
    }

    @Test
    public void testGroupByCategoryWhenEmpty() {
        Map<OperationCategories, List<Rate>> result = RatesServiceImpl.groupByCategory(new ArrayList<Rate>());
        assertThat(result, is(not(nullValue())));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void testGroupByCategory() {
        Rate first = new Rate();
        first.setCategory(OperationCategories.DEBIT_CARDS_OPERATIONS);
        Rate second = new Rate();
        second.setCategory(OperationCategories.DEBIT_CARDS_TRANSFERS);

        Map<OperationCategories, List<Rate>> result = RatesServiceImpl.groupByCategory(Arrays.asList(first, second));

        assertThat(result, is(not(nullValue())));
        assertThat(result.size(), is(2));
        assertThat(result.get(OperationCategories.DEBIT_CARDS_OPERATIONS), contains(first));
        assertThat(result.get(OperationCategories.DEBIT_CARDS_TRANSFERS), contains(second));
    }
}
