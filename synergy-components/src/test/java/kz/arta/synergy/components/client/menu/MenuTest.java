package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 22.10.14
 * Time: 12:34
 */

@RunWith(GwtMockitoTestRunner.class)
public class MenuTest {

    private static final String SEPARATOR = "SEPARATOR";
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    @GwtMock Label label;

    private Menu<Integer> menu;
    private List<MenuItem<Integer>> items;
    private List<Set<String>> styles;
    private ValueChangeHandler<Boolean> changeHandler;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        when(cssComponents.menuSeparator()).thenReturn(SEPARATOR);

        changeHandler = mock(ValueChangeHandler.class);

        menu = new Menu<Integer>() {
            @Override
            protected ValueChangeHandler<Boolean> getSelectionHandler(MenuItem<Integer> newItem) {
                return changeHandler;
            }
        };

        items = new ArrayList<MenuItem<Integer>>();
        styles = new ArrayList<Set<String>>();
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            styles.add(new HashSet<String>());
            final MenuItem<Integer> item = new MenuItem<Integer>(finalI, "") {
                @Override
                public void addStyleName(String style) {
                    super.addStyleName(style);
                    styles.get(finalI).add(style);
                }

                @Override
                public void removeStyleName(String style) {
                    super.removeStyleName(style);
                    styles.get(finalI).remove(style);
                }
            };
            items.add(item);
            menu.add(item);
        }
    }

    /**
     * Только один элемент может быть сфокусирован
     */
    @Test
    public void testFocusOnlyOne() {
        items.get(1).setFocused(true, true);
        assertTrue(items.get(1).isFocused());

        items.get(0).setFocused(true, true);

        assertFalse(items.get(1).isFocused());
        assertTrue(items.get(0).isFocused());
    }

    @Test
    public void testNoFocused() {
        for (MenuItem<Integer> item : items) {
            item.setFocused(true, false);
            assertTrue(item.isFocused());
        }
        menu.noFocused(items.get(0));

        for (int i = 1; i < items.size(); i++) {
            assertFalse(items.get(i).isFocused());
        }
        assertTrue(items.get(0).isFocused());
    }

    @Test
    public void testNoFocusedAll() {
        for (MenuItem<Integer> item : items) {
            item.setFocused(true, false);
            assertTrue(item.isFocused());
        }
        menu.noFocused();

        for (MenuItem<Integer> item : items) {
            assertFalse(item.isFocused());
        }
    }

    @Test
    public void testRemoveSeparator() {
        menu.addSeparator(5);
        assertTrue(styles.get(5).contains(SEPARATOR));

        menu.remove(5);
        assertTrue(styles.get(4).contains(SEPARATOR));
    }

    @Test
    public void testRemoveItem() {
        assertTrue(menu.contains(2));
        menu.remove(items.get(3));
        assertFalse(menu.contains(3));

        menu.remove(5);
        assertFalse(menu.contains(5));
    }

    @Test
    public void testSelectionEvent() {
        items.get(0).fireEvent(TestUtils.createClickEvent(items.get(0)));
        items.get(0).fireEvent(TestUtils.createClickEvent(items.get(0)));

        ArgumentCaptor<ValueChangeEvent> captor = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(changeHandler, times(2)).onValueChange(captor.capture());
        assertTrue((Boolean) captor.getAllValues().get(0).getValue());
        assertFalse((Boolean) captor.getAllValues().get(1).getValue());
    }

    @Test
    public void testClear() {
        items.get(0).setFocused(true, true);
        assertEquals(items.get(0), menu.getFocusedItem());

        menu.clear();

        assertEquals(0, menu.size());
        assertEquals(null, menu.getFocusedItem());
        assertEquals(-1, menu.focusedIndex);
    }

    /**
     * Если мышь не на меню, сфокусированных элементов нет.
     */
    @Test
    public void testMouseOutNoFocus() {
        items.get(1).setFocused(true, true);
        assertTrue(menu.getFocusedItem() != null);

        menu.fireEvent(TestUtils.createMouseOutEvent(menu));

        assertTrue(menu.getFocusedItem() == null);
    }

    @Test
    public void testKeyUpDown() {
        menu.root.fireEvent(TestUtils.createMouseOutEvent(menu.root));
        menu.keyDown();
        assertEquals(items.get(0), menu.getFocusedItem());

        menu.keyDown();
        assertEquals(items.get(1), menu.getFocusedItem());

    }

    @Test
    public void testKeyUp() {
        menu.root.fireEvent(TestUtils.createMouseOutEvent(menu.root));
        menu.keyUp();
        assertEquals(items.get(items.size() - 1), menu.getFocusedItem());

        menu.keyUp();
        assertEquals(items.get(items.size() - 2), menu.getFocusedItem());
    }

    @Test
    public void testKeyLeft() {
        menu.root.fireEvent(TestUtils.createMouseOutEvent(menu.root));
        menu.keyLeft(null);
        assertEquals(items.get(0), menu.getFocusedItem());

        menu.keyDown();
        menu.keyDown();
        menu.keyDown();

        menu.keyLeft(null);
        assertEquals(items.get(0), menu.getFocusedItem());
    }

    @Test
    public void testKeyRight() {
        menu.root.fireEvent(TestUtils.createMouseOutEvent(menu.root));
        menu.keyRight(null);
        assertEquals(items.get(items.size() - 1), menu.getFocusedItem());

        menu.keyUp();
        menu.keyUp();
        menu.keyUp();

        menu.keyRight(null);
        assertEquals(items.get(items.size() - 1), menu.getFocusedItem());
    }

    @Test
    public void testKeyEnter() {
        items.get(5).fireEvent(TestUtils.createClickEvent(items.get(0)));

        menu.keyEnter(null);

        ArgumentCaptor<ValueChangeEvent> captor = ArgumentCaptor.forClass(ValueChangeEvent.class);

        verify(changeHandler, times(1)).onValueChange(captor.capture());
        assertEquals(items.get(5), captor.getValue().getSource());
    }

    /**
     * Enter, когда фокуса нет
     */
    @Test
    public void testKeyEnterNoFocus() {
        menu.noFocused();
        menu.keyEnter(null);

        verify(changeHandler, times(0)).onValueChange(any(ValueChangeEvent.class));
    }
}