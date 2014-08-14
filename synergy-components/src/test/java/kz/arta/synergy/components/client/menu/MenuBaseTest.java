package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.menu.events.FilterUpdateEvent;
import kz.arta.synergy.components.client.menu.filters.ListFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * User: vsl
 * Date: 13.08.14
 * Time: 12:17
 */
@RunWith(GwtMockitoTestRunner.class)
public class MenuBaseTest {
    private MenuBase menu;
    private ArrayList<MenuItem> items;

    @Before
    public void setUp() {
        menu = new MenuBase() {
            @Override
            ArrayList<? extends MenuItem> getItems() {
                return items;
            }

            @Override
            protected void keyDown(Event.NativePreviewEvent event) {
            }

            @Override
            protected void keyUp(Event.NativePreviewEvent event) {
            }

            @Override
            protected void keyLeft(Event.NativePreviewEvent event) {
            }

            @Override
            protected void keyRight(Event.NativePreviewEvent event) {
            }

            @Override
            protected void keyEnter(Event.NativePreviewEvent event) {
            }
        };
        items = new ArrayList<MenuItem>();
    }

    private MenuItem createMenuItem(boolean shouldBeSkipped) {
        MenuItem item = Mockito.mock(MenuItem.class);
        when(item.shouldBeSkipped()).thenReturn(shouldBeSkipped);

        return item;
    }

    @Test
    public void testNext() {
        menu.clear();
        MenuItem[] itemsArray = new MenuItem[]{
                createMenuItem(true),
                createMenuItem(false),
                createMenuItem(true),
                createMenuItem(false),
                createMenuItem(true),
                createMenuItem(true),
        };
        items.addAll(Arrays.asList(itemsArray));

        assertNull(menu.getFocused());

        menu.focus(menu.getNext());
        assertEquals(itemsArray[1], menu.getFocused());
        menu.focus(menu.getNext());
        assertEquals(itemsArray[3], menu.getFocused());
        menu.focus(menu.getNext());
        assertEquals(itemsArray[1], menu.getFocused());
        menu.focus(menu.getNext());
        assertEquals(itemsArray[3], menu.getFocused());
    }

    @Test
    public void testFirst() {
        menu.clear();
        MenuItem[] itemsArray = new MenuItem[]{
                createMenuItem(true),
                createMenuItem(false),
                createMenuItem(true),
                createMenuItem(false),
                createMenuItem(true),
                createMenuItem(true),
        };
        items.addAll(Arrays.asList(itemsArray));

        assertEquals(1, menu.getFirst());

        menu.clear();
        assertNull(menu.getFocused());
    }

    @Test
    public void testPrevious() {
        menu.clear();
        MenuItem[] itemsArray = new MenuItem[]{
                createMenuItem(true),
                createMenuItem(false),
                createMenuItem(true),
                createMenuItem(false),
                createMenuItem(true),
                createMenuItem(true),
        };
        items.addAll(Arrays.asList(itemsArray));

        assertNull(menu.getFocused());

        assertEquals(3, menu.getPrevious());
        menu.focus(menu.getPrevious());

        assertEquals(1, menu.getPrevious());
        menu.focus(menu.getPrevious());

        assertEquals(3, menu.getPrevious());
    }

    @Test
    public void testLast() {
        menu.clear();
        MenuItem[] itemsArray = new MenuItem[]{
                createMenuItem(true),
                createMenuItem(false),
                createMenuItem(true),
                createMenuItem(false),
                createMenuItem(true),
                createMenuItem(true),
        };
        items.addAll(Arrays.asList(itemsArray));

        assertEquals(3, menu.getLast());

        menu.clear();
        assertNull(menu.getFocused());
    }

}
