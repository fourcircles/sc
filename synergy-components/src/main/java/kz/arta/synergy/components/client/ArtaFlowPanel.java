package kz.arta.synergy.components.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * User: vsl
 * Date: 21.07.14
 * Time: 10:37
 */
public class ArtaFlowPanel extends FlowPanel implements
        HasAllMouseHandlers, HasClickHandlers, HasContextMenuHandlers {

    public ArtaFlowPanel() {
        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONCONTEXTMENU);
    }

    public void onBrowserEvent(Event event) {
        if (event.getTypeInt() == Event.ONCONTEXTMENU) {
            event.preventDefault();
        }
        super.onBrowserEvent(event);
    }

    @Override
    public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
        return addDomHandler(handler, ContextMenuEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler(handler, MouseOutEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler(handler, MouseOverEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler(handler, MouseUpEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return addDomHandler(handler, MouseWheelEvent.getType());
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
