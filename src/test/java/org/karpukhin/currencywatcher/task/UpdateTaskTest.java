package org.karpukhin.currencywatcher.task;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Pavel Karpukhin
 * @since 03.03.15
 */
public class UpdateTaskTest {

    @Test
    public void testGetNextDay() {
        DateTime result = UpdateTask.getNextDay(8, 8);
        assertThat(result, is(DateTime.now().plusDays(1).withTime(8, 8, 0, 0)));
    }
}
