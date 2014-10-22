package kz.arta.synergy.components.client.input.tags;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 22.10.14
 * Time: 16:53
 */
@RunWith(GwtMockitoTestRunner.class)
public class MultiComboBoxTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    private MultiComboBox<Integer> combo;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        combo = new MultiComboBox<Integer>();

        for (int i = 0; i < 10; i++) {
            combo.addListItem(i, "");
        }
    }

    @Test
    public void trivial() {
        combo.select(5);
        assertTrue(combo.contains(5));
    }

    @Test
    public void testSelect() {
        assertFalse(combo.contains(5));
        combo.select(5);
        assertTrue(combo.contains(5));
    }

    @Test
    public void testDeselect() {
        combo.select(5);
        assertTrue(combo.contains(5));

        combo.deselect(5);
        assertFalse(combo.contains(5));
    }

    @Test
    public void testGetSelectedItems() {
        combo.select(0);
        combo.select(1);
        combo.select(2);

        Set<Integer> selectedValues = combo.getSelectedValues();

        assertEquals(3, selectedValues.size());
        assertTrue(selectedValues.contains(0));
        assertTrue(selectedValues.contains(1));
        assertTrue(selectedValues.contains(2));
    }

    @Test
    public void testSelectEvents() {
        TagAddEvent.Handler<Integer> handler = mock(TagAddEvent.Handler.class);
        combo.addTagAddHandler(handler);

        combo.select(0);
        combo.select(0);

        verify(handler, times(1)).onTagAdd(any(TagAddEvent.class));
    }

    @Test
    public void testDeselectEvents() {
        TagRemoveEvent.Handler<Integer> handler = mock(TagRemoveEvent.Handler.class);
        combo.addTagRemoveHandler(handler);

        combo.select(0);
        combo.select(1);

        combo.deselect(0);
        combo.deselect(0);

        verify(handler, times(1)).onTagRemove(any(TagRemoveEvent.class));
    }
}
