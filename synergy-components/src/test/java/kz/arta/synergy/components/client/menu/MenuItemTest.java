package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
import kz.arta.synergy.components.client.menu.events.MenuItemFocusEvent;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

/**
 * User: vsl
 * Date: 22.10.14
 * Time: 9:37
 */
@RunWith(GwtMockitoTestRunner.class)
public class MenuItemTest {
    private static final String OVER = "OVER";
    private static final String SELECTED = "SELECTED";
    @GwtMock ComponentResources resources;
    @GwtMock Label label;

    @Mock CssComponents cssComponents;

    private MenuItem<Integer> item;
    private Set<String> styles;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        when(cssComponents.over()).thenReturn(OVER);
        when(cssComponents.selected()).thenReturn(SELECTED);

        styles = new HashSet<String>();
        item = new MenuItem<Integer>(5, "") {
            @Override
            public void addStyleName(String style) {
                super.addStyleName(style);
                styles.add(style);
            }

            @Override
            public void removeStyleName(String style) {
                super.removeStyleName(style);
                styles.remove(style);
            }
        };

    }

    /**
     * Небольшое движение мыши - фокуса нет.
     */
    @Test
    public void testThickMoveFail() {
        item.fireEvent(TestUtils.createMouseMoveEvent(10, 10));
        item.fireEvent(TestUtils.createMouseMoveEvent(13, 13));

        assertFalse(item.isFocused());
    }

    @Test
    public void testClick() {
        item.fireEvent(TestUtils.createClickEvent(null));
        assertEquals(true, item.getValue());

        item.fireEvent(TestUtils.createClickEvent(null));
        assertEquals(false, item.getValue());
    }

    @Test
    public void testSetFocus() {
        item.setFocused(true, false);
        assertTrue(styles.contains(OVER));

        // повтор
        item.setFocused(true, false);
        assertTrue(styles.contains(OVER));

        item.setFocused(false, false);
        assertFalse(styles.contains(OVER));
    }

    @Test
    public void testSetFocusEvents() {
        MenuItemFocusEvent.Handler<Integer> focusHandler = mock(MenuItemFocusEvent.Handler.class);

        item.addFocusHandler(focusHandler);
        item.setFocused(true, true);

        verify(focusHandler, times(1)).onFocus(any(MenuItemFocusEvent.class));
    }

    /**
     * Событие не должно создаваться
     */
    @Test
    public void testSetFocusEventsNoEvents() {
        MenuItemFocusEvent.Handler<Integer> focusHandler = mock(MenuItemFocusEvent.Handler.class);

        item.addFocusHandler(focusHandler);
        item.setFocused(true, false);

        verify(focusHandler, times(0)).onFocus(any(MenuItemFocusEvent.class));
    }

    /**
     * Событие не должно создаваться, если элемент уже был сфокусирован
     */
    @Test
    public void testSetFocusOnFocused() {
        MenuItemFocusEvent.Handler<Integer> focusHandler = mock(MenuItemFocusEvent.Handler.class);

        item.addFocusHandler(focusHandler);
        item.setFocused(true, false);
        item.setFocused(true, true);

        verify(focusHandler, times(0)).onFocus(any(MenuItemFocusEvent.class));
    }

    @Test
    public void testSetValue() {
        item.setValue(true, false);
        assertTrue(styles.contains(SELECTED));

        item.setValue(true, false);
        assertTrue(styles.contains(SELECTED));

        item.setValue(false, false);
        assertFalse(styles.contains(SELECTED));
    }

    @Test
    public void testSetValueEvent() {
        ValueChangeHandler<Boolean> valueHandler = mock(ValueChangeHandler.class);
        item.addValueChangeHandler(valueHandler);

        item.setValue(true, true);

        ArgumentCaptor<ValueChangeEvent> captor = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueHandler, times(1)).onValueChange(captor.capture());

        assertTrue((Boolean) captor.getValue().getValue());
    }

    @Test
    public void testSetValueRepeat() {
        ValueChangeHandler<Boolean> valueHandler = mock(ValueChangeHandler.class);
        item.addValueChangeHandler(valueHandler);

        item.setValue(true, true);
        item.setValue(true, true);

        ArgumentCaptor<ValueChangeEvent> captor = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueHandler, times(1)).onValueChange(captor.capture());

        assertTrue((Boolean) captor.getValue().getValue());
    }

    /**
     * fireEvents == false, событие при втором изменение значения не должно создаваться
     */
    @Test
    public void testSetValueNoEvents() {
        ValueChangeHandler<Boolean> valueHandler = mock(ValueChangeHandler.class);
        item.addValueChangeHandler(valueHandler);

        item.setValue(true, true);
        item.setValue(false, false);

        ArgumentCaptor<ValueChangeEvent> captor = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueHandler, times(1)).onValueChange(captor.capture());

        assertTrue((Boolean) captor.getValue().getValue());
    }

    @Test
    public void testSetValueFalse() {
        ValueChangeHandler<Boolean> valueHandler = mock(ValueChangeHandler.class);
        item.addValueChangeHandler(valueHandler);

        item.setValue(true, false);
        item.setValue(false, true);

        ArgumentCaptor<ValueChangeEvent> captor = ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueHandler, times(1)).onValueChange(captor.capture());

        assertFalse((Boolean) captor.getValue().getValue());
    }
}
