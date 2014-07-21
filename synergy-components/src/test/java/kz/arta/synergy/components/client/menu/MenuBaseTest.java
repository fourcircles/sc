package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: vsl
 * Date: 17.07.14
 * Time: 17:29
 */
@RunWith(GwtMockitoTestRunner.class)
public class MenuBaseTest {
    MenuBase menu;
    MenuBase spy;

    @Mock
    Event.NativePreviewEvent keyDown;
    @Mock
    Event.NativePreviewEvent keyUp;
    @Mock
    Event.NativePreviewEvent keyLeft;
    @Mock
    Event.NativePreviewEvent keyRight;

    private void mockKeyEvent(Event.NativePreviewEvent preview, int keyCode) {
        Event keyDownEvent = Mockito.mock(Event.class);
        when(keyDownEvent.getTypeInt()).thenReturn(Event.ONKEYDOWN);

        when(preview.getNativeEvent()).thenReturn(keyDownEvent);
        when(keyDownEvent.getKeyCode()).thenReturn(keyCode);
    }

    @Before
    public void setUp() {
        mockKeyEvent(keyDown, KeyCodes.KEY_DOWN);
        mockKeyEvent(keyUp, KeyCodes.KEY_UP);
        mockKeyEvent(keyLeft, KeyCodes.KEY_LEFT);
        mockKeyEvent(keyRight, KeyCodes.KEY_RIGHT);

        menu = new MenuBase() {
            @Override
            protected String getMainStyle() {
                return "style";
            }

            @Override
            protected boolean canBeChosen(MenuItem item) {
                return true;
            }
        };
        spy = Mockito.spy(menu);
    }

    @Test
    public void testNext() {
        menu.items = Mockito.mock(ArrayList.class);
        when(menu.items.size()).thenReturn(5);

        menu.selectedIndex = 0;
        assertEquals(1, menu.getNext());

        menu.selectedIndex = 4;
        assertEquals(0, menu.getNext());

        menu.selectedIndex = 2;
        assertEquals(3, menu.getNext());

        menu.selectedIndex = -1;
        assertEquals(0, menu.getNext());

        when(menu.items.size()).thenReturn(0);
        when(menu.items.isEmpty()).thenReturn(true);
        assertEquals(-1, menu.getNext());
    }

    @Test
    public void testPrevious() {
        menu.items = Mockito.mock(ArrayList.class);
        when(menu.items.size()).thenReturn(5);

        menu.selectedIndex = 0;
        assertEquals(4, menu.getPrevious());

        menu.selectedIndex = 4;
        assertEquals(3, menu.getPrevious());

        menu.selectedIndex = 1;
        assertEquals(0, menu.getPrevious());

        menu.selectedIndex = -1;
        assertEquals(4, menu.getPrevious());

        when(menu.items.size()).thenReturn(0);
        when(menu.items.isEmpty()).thenReturn(true);
        assertEquals(-1, menu.getPrevious());
    }

    @Test
    public void testKeyDown() {
        when(spy.getNext()).thenReturn(42);
        spy.onPreviewNativeEvent(keyDown);

        verify(spy).getNext();
        verify(spy).overItem(42, false);
    }

    @Test
    public void testKeyUp() {
        when(spy.getPrevious()).thenReturn(42);
        spy.onPreviewNativeEvent(keyUp);

        verify(spy).getPrevious();
        verify(spy).overItem(42, false);
    }

    @Test
    public void testKeyLeft() {
        when(spy.getFirst()).thenReturn(42);
        spy.onPreviewNativeEvent(keyLeft);

        verify(spy).getFirst();
        verify(spy).overItem(42, false);
    }

    @Test
    public void testKeyRight() {
        when(spy.getLast()).thenReturn(42);
        spy.onPreviewNativeEvent(keyRight);

        verify(spy).getLast();
        verify(spy).overItem(42, false);
    }
}
