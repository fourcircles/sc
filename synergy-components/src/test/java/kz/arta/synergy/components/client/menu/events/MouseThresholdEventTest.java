package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * User: vsl
 * Date: 22.10.14
 * Time: 14:37
 */
@RunWith(GwtMockitoTestRunner.class)
public class MouseThresholdEventTest {

    private CustomWidget widget;

    @Before
    public void setUp() throws Exception {
        widget = new CustomWidget();
    }

    @Test
    public void testSimpleMove() {
        MouseThresholdEvent.Handler handler = mock(MouseThresholdEvent.Handler.class);
        EventBus bus = new SimpleEventBus();
        MouseThresholdEvent.register(bus, widget.getWidget(), handler);

        widget.move(10, 10);
        widget.move(50, 50);

        verify(handler, times(1)).onMouseThreshold(any(MouseThresholdEvent.class));
    }

    @Test
    public void testHandlerRemoval() {
        MouseThresholdEvent.Handler handler = mock(MouseThresholdEvent.Handler.class);
        EventBus bus = new SimpleEventBus();
        HandlerRegistration reg = MouseThresholdEvent.register(bus, widget.getWidget(), handler);

        assertTrue(MouseThresholdEvent.objects.containsKey(widget.getWidget()));

        reg.removeHandler();

        widget.move(20, 20);
        widget.move(50, 50);

        verify(handler, times(0)).onMouseThreshold(any(MouseThresholdEvent.class));
        assertFalse(MouseThresholdEvent.objects.containsKey(widget.getWidget()));
        verify(widget.moveRegistration, times(1)).removeHandler();
    }

    /**
     * Проверяет, что добавляется только один хэндлер на dom-событие mousemove
     */
    @Test
    public void testSingleMoveHandler() {
        MouseThresholdEvent.register(mock(EventBus.class),
                widget.getWidget(), mock(MouseThresholdEvent.Handler.class));
        MouseThresholdEvent.register(mock(EventBus.class),
                widget.getWidget(), mock(MouseThresholdEvent.Handler.class));
        MouseThresholdEvent.register(mock(EventBus.class),
                widget.getWidget(), mock(MouseThresholdEvent.Handler.class));
        verify(widget.getWidget(), times(1)).addDomHandler(any(EventHandler.class), any(DomEvent.Type.class));
    }

    @Test
    public void testMultipleRegistrations() {
        MouseThresholdEvent.Handler handler1 = mock(MouseThresholdEvent.Handler.class);
        EventBus bus1 = new SimpleEventBus();

        MouseThresholdEvent.Handler handler2 = mock(MouseThresholdEvent.Handler.class);
        EventBus bus2 = new SimpleEventBus();

        MouseThresholdEvent.register(bus1, widget.getWidget(), handler1);
        MouseThresholdEvent.register(bus2, widget.getWidget(), handler2);

        widget.move(20, 20);
        widget.move(50, 50);

        verify(handler1, times(1)).onMouseThreshold(any(MouseThresholdEvent.class));
        verify(handler2, times(1)).onMouseThreshold(any(MouseThresholdEvent.class));
    }

    @Test
    public void testMultipleRemovals() {
        MouseThresholdEvent.Handler handler1 = mock(MouseThresholdEvent.Handler.class);
        EventBus bus1 = new SimpleEventBus();

        MouseThresholdEvent.Handler handler2 = mock(MouseThresholdEvent.Handler.class);
        EventBus bus2 = new SimpleEventBus();

        HandlerRegistration reg1 = MouseThresholdEvent.register(bus1, widget.getWidget(), handler1);
        HandlerRegistration reg2 = MouseThresholdEvent.register(bus2, widget.getWidget(), handler2);

        reg1.removeHandler();

        widget.move(20, 20);
        // только это движение распознается вторым хэндлером
        widget.move(40, 40);

        verify(handler1, times(0)).onMouseThreshold(any(MouseThresholdEvent.class));

        reg2.removeHandler();

        widget.move(80, 80);

        verify(handler2, times(1)).onMouseThreshold(any(MouseThresholdEvent.class));
    }

    /**
     * Тестирует добавление хэндлера элемента, затем его удаление и добавление другого
     * хэндлера для этого же объекта
     */
    @Test
    public void testRemovalAndAddingAgain() {
        MouseThresholdEvent.Handler handler1 = mock(MouseThresholdEvent.Handler.class);
        EventBus bus = new SimpleEventBus();

        MouseThresholdEvent.Handler handler2 = mock(MouseThresholdEvent.Handler.class);

        HandlerRegistration reg1 = MouseThresholdEvent.register(bus, widget.getWidget(), handler1);
        reg1.removeHandler();

        // эти события не должны никак регистрироваться
        widget.move(20, 20);
        widget.move(40, 40);
        widget.move(60, 60);
        widget.move(80, 80);

        MouseThresholdEvent.register(bus, widget.getWidget(), handler2);
        widget.move(20, 20);
        widget.move(40, 40);
        widget.move(60, 60);

        verify(handler2, times(2)).onMouseThreshold(any(MouseThresholdEvent.class));
    }

    @Test
    public void testCorrectThreshold() {
        MouseThresholdEvent.Handler handler = mock(MouseThresholdEvent.Handler.class);
        EventBus bus = new SimpleEventBus();

        MouseThresholdEvent.register(bus, widget.getWidget(), handler);

        widget.move(20, 20);
        widget.move(40, 40);
        widget.move(41, 41);
        widget.move(42, 42);

        verify(handler, times(1)).onMouseThreshold(any(MouseThresholdEvent.class));
    }

    private static class CustomWidget {
        private Widget widget;
        private MouseMoveHandler moveHandler;
        private HandlerRegistration moveRegistration;

        private CustomWidget() {
            widget = mock(Widget.class);
            moveRegistration = mock(HandlerRegistration.class);
            // мокирование добавления dom-хэндлера
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    moveHandler = (MouseMoveHandler) invocationOnMock.getArguments()[0];
                    return moveRegistration;
                }
            }).when(widget).addDomHandler(any(EventHandler.class), any(DomEvent.Type.class));
        }

        public void move(int x, int y) {
            MouseMoveEvent moveEvent = mock(MouseMoveEvent.class);
            when(moveEvent.getClientX()).thenReturn(x);
            when(moveEvent.getClientY()).thenReturn(y);
            when(moveEvent.getSource()).thenReturn(widget);
            moveHandler.onMouseMove(moveEvent);
        }

        public Widget getWidget() {
            return widget;
        }

        public HandlerRegistration getMoveRegistration() {
            return moveRegistration;
        }
    }
}