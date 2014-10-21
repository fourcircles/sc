package kz.arta.synergy.components.client.dagger.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.dagger.DaggerItem;

/**
 * User: vsl
 * Date: 20.10.14
 * Time: 11:13
 */
public class DaggerItemSelectionEvent<V> extends GwtEvent<DaggerItemSelectionEvent.Handler<V>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    private DaggerItem<V> item;
    private boolean isSelected;

    public DaggerItemSelectionEvent(DaggerItem<V> item, boolean isSelected) {
        this.item = item;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public DaggerItem<V> getItem() {
        return item;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<V>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<V> handler) {
        handler.onDaggerItemSelection(this);
    }

    public static interface Handler<T> extends EventHandler {
        void onDaggerItemSelection(DaggerItemSelectionEvent<T> event);
    }
}
