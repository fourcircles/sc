package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 17:54
 *
 * Событие выбора значения в списке
 */
public class SelectionEvent<V> extends GwtEvent<SelectionEventHandler> {
    public static Type<SelectionEventHandler> TYPE = new Type<SelectionEventHandler>();

    public Type<SelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SelectionEventHandler handler) {
        handler.onSelection(this);
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
}
