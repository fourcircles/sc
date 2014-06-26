package kz.arta.synergy.components.client.button;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import kz.arta.synergy.components.client.util.Selection;

/**
 * User: user
 * Date: 23.06.14
 * Time: 18:20
 */
public class ButtonBase extends FlowPanel implements HasClickHandlers, HasFocusHandlers, HasEnabled {

    protected boolean enabled = true;
    protected int width;

    protected void init() {
        getElement().getStyle().setCursor(Style.Cursor.DEFAULT);

        Selection.disableTextSelectInternal(getElement());
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addHandler(handler, FocusEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        super.setWidth(width + "px");
        this.width = width;
    }
}
