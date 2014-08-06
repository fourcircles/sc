package kz.arta.synergy.components.client.input.tags.events;

import com.google.gwt.event.shared.*;
import kz.arta.synergy.components.client.input.tags.Tag;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 16:34
 *
 * Событие для добавления тега
 */
public class TagAddEvent extends GwtEvent<TagAddEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onTagAdd(this);
    }

    private Tag tag;

    public TagAddEvent(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public static interface Handler extends EventHandler {
        void onTagAdd(TagAddEvent event);
    }

    public static interface HasHandler extends HasHandlers {
            public HandlerRegistration addTagAddHandler(Handler handler);
    }

    public static HandlerRegistration register(EventBus bus, Handler handler) {
        return bus.addHandler(TYPE, handler);
    }
}
