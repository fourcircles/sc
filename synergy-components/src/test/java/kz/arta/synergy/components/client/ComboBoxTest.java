package kz.arta.synergy.components.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 14.08.14
 * Time: 11:00
 */
@RunWith(GwtMockitoTestRunner.class)
public class ComboBoxTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    ComboBox<Integer> combo;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();
        combo = new ComboBox<Integer>();
    }

    @Test
    public void testAdd() {
        combo.addItem("zero", 0);
        combo.addItem("one", 1);
        combo.addItem("two", 2);

        assertTrue(combo.contains(0));
        assertTrue(combo.contains(1));
        assertFalse(combo.contains(42));

        combo.clear();
        assertFalse(combo.contains(0));
    }

    @Test
    public void testAddNull() {
        combo.addItem("null1", null);
        combo.addItem("null2", null);
        assertTrue(combo.contains(null));

        combo.selectValue(null, true);

        combo.addItem("regular", 5);
        //тест на нормальные сравнения обычных элементов с null
        assertTrue(combo.contains(5));
    }

    @Test
    public void testRemove() {
        combo.addItem("zero", 0);
        combo.addItem("one", 1);
        combo.addItem("two", 2);

        combo.remove(0);
        combo.remove(1);

        assertFalse(combo.contains(0));
        assertFalse(combo.contains(1));
        assertTrue(combo.contains(2));
    }

    @Test
    public void testSelectionEvent() {
        combo.addItem("zero", 0);
        combo.addItem("three", 3);

        ValueChangeHandler<Integer> handler = mock(ValueChangeHandler.class);
        combo.addValueChangeHandler(handler);

        combo.selectValue(3, true);
        combo.selectValue(42, true);

        ArgumentCaptor<ValueChangeEvent> arg = ArgumentCaptor.forClass(ValueChangeEvent.class);

        verify(handler, times(1)).onValueChange(arg.capture());
        assertEquals(3, arg.getValue().getValue());
    }
}
