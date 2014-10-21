package kz.arta.synergy.components.client.dagger.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.dagger.DaggerItem;

/**
 * User: vsl
 * Date: 17.10.14
 * Time: 18:10
 */
public class DaggerFocusEvent<V> extends GwtEvent<DaggerFocusEvent.Handler<V>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    private DaggerItem<V> item;

    public DaggerFocusEvent(DaggerItem<V> item) {
        this.item = item;
    }

    public DaggerItem<V> getItem() {
        return item;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<V>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<V> handler) {
        handler.onDaggerFocus(this);
    }

    public static interface Handler<T> extends EventHandler {
        void onDaggerFocus(DaggerFocusEvent<T> event);
    }
}
