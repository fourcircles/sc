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
public class TagAddEvent<V> extends GwtEvent<TagAddEvent.Handler<V>> {
    private static Type<Handler<?>> TYPE;

    private Tag<V> tag;

    public TagAddEvent(Tag<V> tag) {
        this.tag = tag;
    }

    public static Type<Handler<?>> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler<?>>();
        }
        return TYPE;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<V>> getAssociatedType() {
        return (Type) getType();
    }

    protected void dispatch(Handler<V> handler) {
        handler.onTagAdd(this);
    }

    public Tag<V> getTag() {
        return tag;
    }

    public static interface Handler<T> extends EventHandler {
        void onTagAdd(TagAddEvent<T> event);
    }

    public static interface HasHandler<T> extends HasHandlers {
            public HandlerRegistration addTagAddHandler(Handler<T> handler);
    }

    public static HandlerRegistration register(EventBus bus, Handler<?> handler) {
        return bus.addHandler(getType(), handler);
    }
}
