package kz.arta.synergy.components.client.input.tags.events;

import com.google.gwt.event.shared.*;
import kz.arta.synergy.components.client.input.tags.Tag;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 16:39
 *
 * Событие для удаления тега
 */
public class TagRemoveEvent<V> extends GwtEvent<TagRemoveEvent.Handler<V>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    private Tag<V> tag;

    public TagRemoveEvent(Tag<V> tag) {
        this.tag = tag;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<V>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<V> handler) {
        handler.onTagRemove(this);
    }

    public Tag<V> getTag() {
        return tag;
    }

    public static interface Handler<T> extends EventHandler {
        void onTagRemove(TagRemoveEvent<T> event);
    }

    public static interface HasHandler<T> extends HasHandlers {
        public HandlerRegistration addTagRemoveHandler(Handler<T> handler);
    }

    public static HandlerRegistration register(EventBus bus, Handler<?> handler) {
        return bus.addHandler(TYPE, handler);
    }
}
