package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 06.10.14
 * Time: 12:13
 */
@RunWith(GwtMockitoTestRunner.class)
public class MonthlyRepeatChooserTest {
    private static final String OUT_MONTH = "";
    private static final String PRESSED = "";

    private MonthlyRepeatChooser chooser;

    private List<InlineLabel> days;
    @GwtMock private ComponentResources resources;

    @Before
    public void setUp() {
        CssComponents cssComponents = mock(CssComponents.class);

        when(resources.cssComponents()).thenReturn(cssComponents);

        when(cssComponents.outMonth()).thenReturn(OUT_MONTH);
        when(cssComponents.pressed()).thenReturn(PRESSED);
        new SynergyComponents().onModuleLoad();

        days = new ArrayList<InlineLabel>();

        chooser = new MonthlyRepeatChooser() {
            @Override
            InlineLabel createDay() {
                InlineLabel newDay = mock(InlineLabel.class);
                MonthlyRepeatChooserTest.this.days.add(newDay);
                return newDay;
            }
        };
        reset(days.toArray());
    }

    @Test
    public void testInit() {
        final List<InlineLabel> daysLabels = new ArrayList<InlineLabel>();

        MonthlyRepeatChooser chooser = new MonthlyRepeatChooser(10) {
            @Override
            InlineLabel createDay() {
                InlineLabel newDay = mock(InlineLabel.class);
                daysLabels.add(newDay);
                return newDay;
            }
        };

        for (int i = 10; i < days.size(); i++) {
            verify(daysLabels.get(i), times(1)).addStyleName(same(OUT_MONTH));
        }
    }

    @Test
    public void testSetDayCountSmall() {
        int daysCount = 5;
        chooser.setDaysCount(daysCount);
        for (int i = daysCount; i < RepeatDate.MAX_DAYS; i++) {
            verify(days.get(i), times(1)).addStyleName(anyString());
        }
        for (int i = RepeatDate.MAX_DAYS; i < days.size(); i++) {
            verifyZeroInteractions(days.get(i));
        }
        for (int i = 0; i < daysCount; i++) {
            verifyZeroInteractions(days.get(i));
        }
    }

    @Test
    public void testSetDayCountMax() {
        chooser.setDaysCount(33);
        for (InlineLabel day : days) {
            verify(day, times(0)).addStyleName(same(OUT_MONTH));
        }
        verify(days.get(31)).removeStyleName(same(OUT_MONTH));
        verify(days.get(32)).removeStyleName(same(OUT_MONTH));

    }

    @Test
    public void testDayClickNonActive() {
        int dayNum = 25;
        chooser.setDaysCount(dayNum);
        reset(days.toArray());

        chooser.dayClick(days.get(dayNum));

        verify(days.get(dayNum), never()).addStyleName(anyString());
        assertEquals(0, chooser.size());
    }

    @Test
    public void testDayClickActive() {
        int dayNum = 20;
        chooser.dayClick(days.get(dayNum));

        verify(days.get(dayNum), times(1)).addStyleName(same(PRESSED));
        assertEquals(1, chooser.size());
    }
}
