package kz.arta.synergy.components.client.button;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: user
 * Date: 08.07.14
 * Time: 9:15
 */
public class HasClickHandlersMock implements HasClickHandlers {

    /** The clickhandler that can be clicked upon later on */
    public ClickHandler clickHandler;

    public HandlerRegistration addClickHandler(ClickHandler clickHandler) {

        this.clickHandler = clickHandler;
        return new HandlerRegistration() {
            public void removeHandler() { }
        };
    }

    /**
     * Use together with ClickEventMock as event...
     */
    public void fireEvent(GwtEvent event) {
        if (event instanceof ClickEventMock) {
            ClickEventMock clickEventMock = (ClickEventMock) event;
            this.clickHandler.onClick(clickEventMock);
        }
    }
}
