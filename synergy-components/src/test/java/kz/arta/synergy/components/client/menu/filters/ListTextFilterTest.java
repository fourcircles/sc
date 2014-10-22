package kz.arta.synergy.components.client.menu.filters;

import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.menu.events.FilterUpdateEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 14.08.14
 * Time: 9:39
 */
@RunWith(GwtMockitoTestRunner.class)
public class ListTextFilterTest {
    ListTextFilter prefixFilter;
    ListTextFilter containsFilter;

    @Before
    public void setUp() {
        prefixFilter = ListTextFilter.createPrefixFilter();
        containsFilter = ListTextFilter.createContainsFilter();
    }

    private MenuItem<String> mockItem(String text) {
        MenuItem<String> item = mock(MenuItem.class);
        when(item.getText()).thenReturn(text);
        return item;
    }

    @Test
    public void testEvents() {
        FilterUpdateEvent.Handler prefixHandler = mock(FilterUpdateEvent.Handler.class);
        prefixFilter.addFilterUpdateHandler(prefixHandler);

        prefixFilter.setText("abcd");
        prefixFilter.setText("abcd");

        prefixFilter.setText("abcz");

        //два раза потому что если текст не изменился, то событие не публикуется
        verify(prefixHandler, times(2)).onFilterUpdate(any(FilterUpdateEvent.class));
    }

    @Test
    public void testNullTextEvents() {
        FilterUpdateEvent.Handler prefixHandler = mock(FilterUpdateEvent.Handler.class);
        prefixFilter.addFilterUpdateHandler(prefixHandler);

        //был null, нет события
        prefixFilter.setText(null);

        prefixFilter.setText("null");

        prefixFilter.setText(null);

        verify(prefixHandler, times(2)).onFilterUpdate(any(FilterUpdateEvent.class));
    }

    @Test
    public void testPrefix() {
        prefixFilter.setText("abcd");
        assertFalse(prefixFilter.include(mockItem(null)));
        assertFalse(prefixFilter.include(mockItem("")));
        assertFalse(prefixFilter.include(mockItem("abc")));
        assertFalse(prefixFilter.include(mockItem("abcz")));

        assertTrue(prefixFilter.include(mockItem("aBcD")));
        assertTrue(prefixFilter.include(mockItem("AbCdE")));

        prefixFilter.setText("");
        assertTrue(prefixFilter.include(mockItem("whatever")));
        assertTrue(prefixFilter.include(mockItem("")));
        assertFalse(prefixFilter.include(mockItem(null)));

        prefixFilter.setText(null);
        assertTrue(prefixFilter.include(mockItem("whatever")));
    }

    @Test
    public void testContainsFilter() {
        containsFilter.setText("abcd");
        assertFalse(containsFilter.include(mockItem(null)));
        assertFalse(containsFilter.include(mockItem("")));
        assertFalse(containsFilter.include(mockItem("abd")));

        assertTrue(containsFilter.include(mockItem("abcd")));
        assertTrue(containsFilter.include(mockItem("zabcdzzz")));

        containsFilter.setText("");
        assertTrue(containsFilter.include(mockItem("zabcdzzz")));
        assertTrue(containsFilter.include(mockItem("")));
        assertFalse(containsFilter.include(mockItem(null)));

        containsFilter.setText(null);
        assertTrue(containsFilter.include(mockItem("zabcdzzz")));
        assertTrue(containsFilter.include(mockItem("")));
        assertTrue(containsFilter.include(mockItem(null)));
    }


}
