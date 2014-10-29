package kz.arta.synergy.components.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.GwtEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: vsl
 * Date: 22.10.14
 * Time: 11:44
 *
 * Утилиты для тестирования
 */
public class TestUtils {

    private TestUtils() {
        // nope
    }

    /**
     * Создает мок события движения мыши с заданными координатами
     * @param moveEvent мок события, которое передается хендлерам
     * @return событие, которое можно запускать на любом {@link com.google.gwt.event.shared.HasHandlers}
     */
    public static GwtEvent<MouseMoveHandler> createMouseMoveEvent(final MouseMoveEvent moveEvent) {
        return new GwtEvent<MouseMoveHandler>() {
            @Override
            public com.google.gwt.event.shared.GwtEvent.Type<MouseMoveHandler> getAssociatedType() {
                return MouseMoveEvent.getType();
            }

            @Override
            protected void dispatch(MouseMoveHandler handler) {
                handler.onMouseMove(moveEvent);
            }
        };
    }

    /**
     * {@link #createMouseMoveEvent(com.google.gwt.event.dom.client.MouseMoveEvent)}
     * @param x x-координата события движения мыши
     * @param y y-координата события движения мыши
     */
    public static GwtEvent<MouseMoveHandler> createMouseMoveEvent(final int x, final int y) {
        final MouseMoveEvent moveEvent = mock(MouseMoveEvent.class);
        when(moveEvent.getClientX()).thenReturn(x);
        when(moveEvent.getClientY()).thenReturn(y);
        return createMouseMoveEvent(moveEvent);
    }

    /**
     * Создает мок события клика мыши.
     * @param clickEvent мок события клика, который передается хэндлерам
     * @return событие, которое можно запускать на любом {@link com.google.gwt.event.shared.HasHandlers}
     */
    public static GwtEvent<ClickHandler> createClickEvent(final ClickEvent clickEvent) {
        return new GwtEvent<ClickHandler>() {
            @Override
            public com.google.gwt.event.shared.GwtEvent.Type<ClickHandler> getAssociatedType() {
                return ClickEvent.getType();
            }
            @Override
            protected void dispatch(ClickHandler handler) {
                handler.onClick(clickEvent);
            }
        };
    }

    /**
     * {@link #createClickEvent(com.google.gwt.event.dom.client.ClickEvent)}
     * @param source источник события клика
     */
    public static GwtEvent<ClickHandler> createClickEvent(Object source) {
        final ClickEvent clickEvent = mock(ClickEvent.class);
        when(clickEvent.getSource()).thenReturn(source);
        return createClickEvent(clickEvent);
    }

    public static GwtEvent<MouseOverHandler> createMouseOverEvent(final MouseOverEvent overEvent) {
        return new GwtEvent<MouseOverHandler>() {
            @Override
            public com.google.gwt.event.shared.GwtEvent.Type<MouseOverHandler> getAssociatedType() {
                return MouseOverEvent.getType();
            }
            @Override
            protected void dispatch(MouseOverHandler handler) {
                handler.onMouseOver(overEvent);
            }
        };
    }

    public static GwtEvent<MouseOverHandler> createMouseOverEvent(Object source) {
        final MouseOverEvent overEvent = mock(MouseOverEvent.class);
        when(overEvent.getSource()).thenReturn(source);
        return createMouseOverEvent(overEvent);
    }

    public static GwtEvent<MouseOutHandler> createMouseOutEvent(final MouseOutEvent outEvent) {
        return new GwtEvent<MouseOutHandler>() {
            @Override
            public com.google.gwt.event.shared.GwtEvent.Type<MouseOutHandler> getAssociatedType() {
                return MouseOutEvent.getType();
            }
            @Override
            protected void dispatch(MouseOutHandler handler) {
                handler.onMouseOut(outEvent);
            }
        };
    }

    public static GwtEvent<MouseOutHandler> createMouseOutEvent(Object source) {
        final MouseOutEvent outEvent = mock(MouseOutEvent.class);
        when(outEvent.getSource()).thenReturn(source);
        return createMouseOutEvent(outEvent);
    }

}
