package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * User: vsl
 * Date: 06.10.14
 * Time: 10:06
 */
@RunWith(GwtMockitoTestRunner.class)
public class RepeatDateTest {

    @Test(expected=IllegalArgumentException.class)
    public void testCreateBadDay1() {
        new RepeatDate(-1, RepeatChooser.MODE.WEEK);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateBadWeekDay() {
        new RepeatDate(7, RepeatChooser.MODE.WEEK);
    }

    public void testCreateFineMonthDay() {
        new RepeatDate(7, RepeatChooser.MODE.MONTH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateBadMonthday() {
        new RepeatDate(RepeatDate.MAX_DAYS + 1, RepeatChooser.MODE.MONTH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateBadMonth() {
        new RepeatDate(0, DateUtil.MONTHS, RepeatChooser.MODE.YEAR);
    }

    @Test
    public void testEqualsWeekly() {
        RepeatDate monday = new RepeatDate(0, RepeatChooser.MODE.WEEK);
        RepeatDate monday2 = new RepeatDate(0, 33, RepeatChooser.MODE.WEEK);
        RepeatDate tuesday = new RepeatDate(1, RepeatChooser.MODE.WEEK);

        assertEquals(monday, monday2);
        assertEquals(monday.hashCode(), monday2.hashCode());

        assertNotEquals(monday, tuesday);
    }

    @Test
    public void testEqualsMonthly() {
        RepeatDate d10 = new RepeatDate(10, RepeatChooser.MODE.MONTH);
        RepeatDate d10Another = new RepeatDate(10, 0, RepeatChooser.MODE.MONTH);
        RepeatDate d11 = new RepeatDate(11, RepeatChooser.MODE.MONTH);

        assertEquals(d10, d10Another);
        assertEquals(d10.hashCode(), d10Another.hashCode());

        assertNotEquals(d10, d11);
    }

    @Test
    public void testEqualsYearly() {
        RepeatDate jan10 = new RepeatDate(10, 0, RepeatChooser.MODE.YEAR);
        RepeatDate jan10Another = new RepeatDate(10, 0, RepeatChooser.MODE.YEAR);
        RepeatDate jan11 = new RepeatDate(11, 0, RepeatChooser.MODE.YEAR);

        assertEquals(jan10, jan10Another);
        assertEquals(jan10.hashCode(), jan10Another.hashCode());

        assertNotEquals(jan10, jan11);
    }
}
