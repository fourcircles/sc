package kz.arta.synergy.components.client.input.events;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: vsl
 * Date: 30.07.14
 * Time: 10:16
 *
 * Событие изменения текста
 */
public class TextChangedEvent extends GwtEvent<TextChangedEvent.Handler> {
    private static Type<Handler> TYPE = new Type<Handler>();

    private String oldText;
    private String newText;

    public TextChangedEvent(String oldText, String newText) {
        this.oldText = oldText;
        this.newText = newText;
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onTextChanged(this);
    }

    public static interface Handler extends EventHandler {
        void onTextChanged(TextChangedEvent event);
    }

    public String getOldText() {
        return oldText;
    }

    public String getNewText() {
        return newText;
    }

    public static HandlerRegistration register(EventBus bus, Handler handler) {
        return bus.addHandler(TYPE, handler);
    }
}
