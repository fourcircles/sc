package kz.arta.synergy.components.client.menu;

import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
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
 * Time: 14:51
 */
@RunWith(GwtMockitoTestRunner.class)
public class DropDownListTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    @GwtMock Label label;
    private DropDownList<Integer> list;
    private List<MenuItem<Integer>> items;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        list = new DropDownList<Integer>();
        items = new ArrayList<MenuItem<Integer>>();

        for (int i = 0; i < 10; i++) {
            MenuItem<Integer> item = new MenuItem<Integer>(i, "");

            Label label = mock(Label.class);
            when(label.getText()).thenReturn("item_" +
                    (i % 2 == 0 ? "even" : "odd") +
                    "_" + i);
            item.label = label;

            items.add(item);
            list.add(item);
        }
    }

    @Test
    public void testSelection() {
        assertNull(list.getSelectedItem());
        items.get(0).fireEvent(TestUtils.createClickEvent(null));
        assertEquals(items.get(0), list.getSelectedItem());
        assertTrue(items.get(0).isSelected());
    }

    /**
     * Может быть выбран только один элемент
     */
    @Test
    public void testSingleSelection() {
        items.get(0).fireEvent(TestUtils.createClickEvent(null));
        assertTrue(items.get(0).isSelected());

        items.get(3).fireEvent(TestUtils.createClickEvent(null));
        assertFalse(items.get(0).isSelected());
        assertTrue(items.get(3).isSelected());
        assertEquals(items.get(3), list.getSelectedItem());
    }

    /**
     * При клике по выбранному элементу событие выбора указывает на deselect
     */
    @Test
    public void testSelectionEvent() {
        MenuItemSelection.Handler<Integer> handler = mock(MenuItemSelection.Handler.class);
        list.addItemSelectionHandler(handler);

        items.get(0).fireEvent(TestUtils.createClickEvent(null));
        items.get(0).fireEvent(TestUtils.createClickEvent(null));

        ArgumentCaptor<MenuItemSelection> captor = ArgumentCaptor.forClass(MenuItemSelection.class);
        verify(handler, times(2)).onItemSelection(captor.capture());
        assertTrue(captor.getAllValues().get(0).isSelected());
        assertFalse(captor.getAllValues().get(1).isSelected());
    }

    /**
     * Выбирается элемент без событий
     */
    @Test
    public void testSelectionNoEvents() {
        MenuItemSelection.Handler<Integer> handler = mock(MenuItemSelection.Handler.class);
        list.addItemSelectionHandler(handler);

        list.selectItem(items.get(0), true, false);

        assertTrue(items.get(0).isSelected());
        verify(handler, times(0)).onItemSelection(any(MenuItemSelection.class));
    }

    @Test
    public void testDeselection() {
        MenuItemSelection.Handler<Integer> handler = mock(MenuItemSelection.Handler.class);
        list.addItemSelectionHandler(handler);

        list.selectItem(items.get(0), true, false);
        list.selectItem(items.get(0), false, true);

        assertFalse(items.get(0).isSelected());
        verify(handler, times(1)).onItemSelection(any(MenuItemSelection.class));
    }

    @Test
    public void testNavigationOdd() {
        ListTextFilter filter = ListTextFilter.createPrefixFilter();
        list.setFilter(filter);

        filter.setText("item_odd");
        list.keyDown();
        assertEquals(items.get(1), list.getFocusedItem());

        list.keyDown();
        assertEquals(items.get(3), list.getFocusedItem());

        list.keyRight(null);
        assertEquals(items.get(9), list.getFocusedItem());

    }

    /**
     * При изменении параметров фильтра сфокусированный элемент не меняется.
     */
    @Test
    public void testFilterChange() {
        ListTextFilter filter = ListTextFilter.createPrefixFilter();
        list.setFilter(filter);

        filter.setText("item_even");
        list.keyDown();
        list.keyDown();
        assertEquals(items.get(2), list.getFocusedItem());
        filter.setText("");
        list.keyDown();
        assertEquals(items.get(3), list.getFocusedItem());
    }

    @Test
    public void testBadFilter() {
        ListTextFilter filter = ListTextFilter.createPrefixFilter();
        list.setFilter(filter);

        items.get(5).setFocused(true, true);
        assertEquals(items.get(5), list.getFocusedItem());

        // таких элементов нет
        filter.setText("item_X");
        list.keyDown();
        // сфокусированный элемент не меняется
        assertEquals(items.get(5), list.getFocusedItem());

        filter.setText("item_odd");
        list.keyDown();
        assertEquals(items.get(7), list.getFocusedItem());
    }
}
