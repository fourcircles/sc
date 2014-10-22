package kz.arta.synergy.components.client.menu;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 22.10.14
 * Time: 16:17
 */
@RunWith(GwtMockitoTestRunner.class)
public class DropDownListMultiTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    private DropDownListMulti<Integer> list;
    private List<MenuItem<Integer>> items;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        list = new DropDownListMulti<Integer>();
        items = new ArrayList<MenuItem<Integer>>();

        for (int i = 0; i < 10; i++) {
            MenuItem<Integer> item = new MenuItem<Integer>(i, "");

            items.add(item);
            list.add(item);
        }
    }

    @Test
    public void testMultipleSelection() {
        items.get(0).fireEvent(TestUtils.createClickEvent(null));
        items.get(1).fireEvent(TestUtils.createClickEvent(null));
        items.get(2).fireEvent(TestUtils.createClickEvent(null));

        Set<MenuItem<Integer>> selectedItems = list.getSelectedItems();
        assertEquals(3, selectedItems.size());
        assertTrue(selectedItems.contains(items.get(0)));
        assertTrue(selectedItems.contains(items.get(1)));
        assertTrue(selectedItems.contains(items.get(2)));
    }

    @Test
    public void testDeselection() {
        items.get(0).fireEvent(TestUtils.createClickEvent(null));
        items.get(1).fireEvent(TestUtils.createClickEvent(null));
        items.get(2).fireEvent(TestUtils.createClickEvent(null));

        assertEquals(3, list.getSelectedItems().size());

        items.get(0).fireEvent(TestUtils.createClickEvent(null));
        items.get(1).fireEvent(TestUtils.createClickEvent(null));
        items.get(2).fireEvent(TestUtils.createClickEvent(null));

        assertEquals(0, list.getSelectedItems().size());
    }

    @Test
    public void testSelectionEvent() {
        MenuItemSelection.Handler<Integer> handler = mock(MenuItemSelection.Handler.class);
        list.addItemSelectionHandler(handler);

        items.get(0).fireEvent(TestUtils.createClickEvent(null));
        items.get(1).fireEvent(TestUtils.createClickEvent(null));
        items.get(2).fireEvent(TestUtils.createClickEvent(null));
        items.get(2).fireEvent(TestUtils.createClickEvent(null));

        assertEquals(2, list.getSelectedItems().size());

        verify(handler, times(4)).onItemSelection(any(MenuItemSelection.class));
    }

    @Test
    public void testSelectionRepeat() {
        MenuItemSelection.Handler<Integer> handler = mock(MenuItemSelection.Handler.class);
        list.addItemSelectionHandler(handler);

        list.selectItem(items.get(0), true, false);
        list.selectItem(items.get(1), true, false);
        list.selectItem(items.get(2), true, false);

        // fireEvents == true, но события не создаются
        list.selectItem(items.get(0), true, true);
        list.selectItem(items.get(5), false, true);

        verify(handler, times(0)).onItemSelection(any(MenuItemSelection.class));
    }
}
