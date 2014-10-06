package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 06.10.14
 * Time: 14:39
 */
@RunWith(GwtMockitoTestRunner.class)
public class YearlyRepeatChooserTest {
    private static final String PRESSED = "";

    private YearlyRepeatChooser chooser;

    private List<InlineLabel> days;
    private Map<InlineLabel, List<String>> daysStyles;

    @BeforeClass
    public static void beforeClass() {
        ComponentResources resources = mock(ComponentResources.class);
        CssComponents cssComponents = mock(CssComponents.class);

        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(cssComponents);

        when(cssComponents.pressed()).thenReturn(PRESSED);
    }

    @Before
    public void setUp() {
        days = new ArrayList<InlineLabel>();
        daysStyles = new HashMap<InlineLabel, List<String>>();

        chooser = new YearlyRepeatChooser() {
            @Override
            InlineLabel createDay() {
                final InlineLabel day = mock(InlineLabel.class);
                daysStyles.put(day, new ArrayList<String>());
                doAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        String style = (String) invocationOnMock.getArguments()[0];
                        daysStyles.get(day).add(style);
                        return null;
                    }
                }).when(day).addStyleName(anyString());
                doAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        String style = (String) invocationOnMock.getArguments()[0];
                        daysStyles.get(day).remove(style);
                        return null;
                    }
                }).when(day).removeStyleName(anyString());

                YearlyRepeatChooserTest.this.days.add(day);
                return day;
            }
        };
        reset(days.toArray());

    }

    @Test
    public void testUpdateMonth() {
        chooser.addAll(Arrays.asList(
                new RepeatDate(1, 4, RepeatChooser.MODE.YEAR),
                new RepeatDate(3, 4, RepeatChooser.MODE.YEAR),
                new RepeatDate(5, 4, RepeatChooser.MODE.YEAR)
        ), false);

        chooser.currentMonth = 4;
        chooser.updateMonth();

        verify(days.get(1), times(1)).addStyleName(PRESSED);
        verify(days.get(3), times(1)).addStyleName(PRESSED);
        verify(days.get(5), times(1)).addStyleName(PRESSED);
    }

    @Test
    public void testUpdateMonthEmpty() {
        chooser.addAll(Arrays.asList(
                new RepeatDate(0, 0, RepeatChooser.MODE.YEAR),
                new RepeatDate(2, 0, RepeatChooser.MODE.YEAR),
                new RepeatDate(4, 0, RepeatChooser.MODE.YEAR)
        ), false);
        chooser.currentMonth = 5;
        chooser.updateMonth();

        for (InlineLabel day : days) {
            for (String style : daysStyles.get(day)) {
                //noinspection StringEquality
                if (style == PRESSED) { //да, здесь именно ==, потому что значение неважно
                    fail();
                }
            }
        }
    }
}
