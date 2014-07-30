package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 17:54
 *
 * Событие выбора значения в списке
 */
public class SelectionEvent<V> extends GwtEvent<SelectionEvent.Handler<V>> {
    public static Type<Handler<?>> TYPE = new Type<Handler<?>>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Type<Handler<V>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<V> handler) {
        handler.onSelection(this);
    }

    public interface Handler<T> extends EventHandler {
        void onSelection(SelectionEvent<T> event);
    }

    /**
     * Значение
     */
    private V value;

    public SelectionEvent(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public static HandlerRegistration register(EventBus bus, Handler<?> handler) {
        return bus.addHandler(TYPE, handler);
    }

}
