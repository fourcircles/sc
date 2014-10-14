package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * User: vsl
 * Date: 06.10.14
 * Time: 10:52
 */
@RunWith(GwtMockitoTestRunner.class)
public class BaseRepeatChooserTest {

    private BaseRepeatChooser chooser;

    @Mock
    private ValueChangeHandler<Collection<RepeatDate>> valueHandler;

    @Before
    public void setUp() {
        chooser = new BaseRepeatChooser();
    }

    @Test
    public void testAdd() {
        chooser.add(new RepeatDate(0, RepeatChooser.MODE.WEEK), false);
        chooser.add(new RepeatDate(0, RepeatChooser.MODE.WEEK), false);

        chooser.add(new RepeatDate(10, 0, RepeatChooser.MODE.YEAR), false);
        chooser.add(new RepeatDate(10, 0, RepeatChooser.MODE.YEAR), false);

        assertTrue(chooser.contains(new RepeatDate(0, RepeatChooser.MODE.WEEK)));
        assertTrue(chooser.contains(new RepeatDate(0, 0, RepeatChooser.MODE.WEEK)));
        assertTrue(chooser.contains(new RepeatDate(10, 0, RepeatChooser.MODE.YEAR)));

        assertEquals(2, chooser.size());
    }

    @Test
    public void testRemove() {
        chooser.add(new RepeatDate(0, RepeatChooser.MODE.WEEK), false);
        chooser.add(new RepeatDate(1, RepeatChooser.MODE.WEEK), false);

        chooser.remove(null, false);
        chooser.remove(new RepeatDate(1, RepeatChooser.MODE.WEEK), false);

        assertTrue(chooser.contains(new RepeatDate(0, RepeatChooser.MODE.WEEK)));
        assertFalse(chooser.contains(new RepeatDate(1, RepeatChooser.MODE.WEEK)));
        assertFalse(chooser.contains(null));
    }

    @Test
    public void testAdd_events() {
        chooser.addValueChangeHandler(valueHandler);

        chooser.add(new RepeatDate(0, RepeatChooser.MODE.WEEK), true);
        chooser.add(new RepeatDate(1, RepeatChooser.MODE.WEEK), false);
        chooser.add(new RepeatDate(0, RepeatChooser.MODE.WEEK), true);
        chooser.add(null, true);

        verify(valueHandler, times(1)).onValueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testRemove_events() {
        chooser.addValueChangeHandler(valueHandler);

        chooser.add(new RepeatDate(0, RepeatChooser.MODE.WEEK), false);
        chooser.add(new RepeatDate(1, RepeatChooser.MODE.WEEK), false);

        chooser.remove(new RepeatDate(2, RepeatChooser.MODE.WEEK), true);
        chooser.remove(null, true);
        chooser.remove(new RepeatDate(1, RepeatChooser.MODE.WEEK), true);

        verify(valueHandler, times(1)).onValueChange(any(ValueChangeEvent.class));
    }

    @Test
    public void testAddAll() {
        chooser.addValueChangeHandler(valueHandler);

        chooser.add(new RepeatDate(0, RepeatChooser.MODE.WEEK), false);
        chooser.add(new RepeatDate(1, RepeatChooser.MODE.WEEK), false);
        chooser.add(new RepeatDate(2, RepeatChooser.MODE.WEEK), false);
        chooser.add(new RepeatDate(3, RepeatChooser.MODE.WEEK), false);

        Set<RepeatDate> addDates = new HashSet<RepeatDate>();
        addDates.addAll(Arrays.asList(
                new RepeatDate(2, RepeatChooser.MODE.WEEK),
                new RepeatDate(3, RepeatChooser.MODE.WEEK),
                new RepeatDate(4, RepeatChooser.MODE.WEEK),
                new RepeatDate(5, RepeatChooser.MODE.WEEK)
        ));

        chooser.addAll(addDates, true);

        ArgumentCaptor<ValueChangeEvent> captor = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueHandler, times(1)).onValueChange(captor.capture());

        Set<RepeatDate> newDates = (Set<RepeatDate>) captor.getValue().getValue();
        assertEquals(2, newDates.size());
    }
}
