package kz.arta.synergy.components.client.input;

import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;
import kz.arta.synergy.components.client.menu.filters.ListTextFilter;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 22.10.14
 * Time: 17:11
 */
@RunWith(GwtMockitoTestRunner.class)
public class SearchResultInputTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    private SearchResultInput<Integer> search;
    private DropDownList<Integer> list;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        list = new DropDownList<Integer>();
        for (int i = 0; i < 10; i++) {
            list.add(new MenuItem<Integer>(i, ""));
        }
        search = new SearchResultInput<Integer>();
        search.setList(list);
    }

    @Test
    public void testSelectValueFromList() {
        list.getItems().get(0).fireEvent(TestUtils.createClickEvent(null));

        assertEquals(Integer.valueOf(0), search.getValue());
        assertTrue(list.getItems().get(0).isSelected());

        assertEquals(list.getItems().get(0).getText(), search.input.getText());
    }

    @Test
    public void testSelectSeveralValues() {
        list.getItems().get(0).fireEvent(TestUtils.createClickEvent(null));
        list.getItems().get(1).fireEvent(TestUtils.createClickEvent(null));
        list.getItems().get(2).fireEvent(TestUtils.createClickEvent(null));

        assertEquals(Integer.valueOf(2), search.getValue());
        assertTrue(list.getItems().get(2).isSelected());
        assertFalse(list.getItems().get(1).isSelected());
        assertFalse(list.getItems().get(0).isSelected());
    }
}
