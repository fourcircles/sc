package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.menu.MenuItem;

/**
 * User: vsl
 * Date: 17.10.14
 * Time: 18:10
 *
 * Событие фокусировки элемента
 */
public class MenuFocusEvent<V> extends GwtEvent<MenuFocusEvent.Handler<V>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    /**
     * Элемент
     */
    private MenuItem<V> item;

    public MenuFocusEvent(MenuItem<V> item) {
        this.item = item;
    }

    public MenuItem<V> getItem() {
        return item;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<V>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<V> handler) {
        handler.onFocus(this);
    }

    public static interface Handler<T> extends EventHandler {
        void onFocus(MenuFocusEvent<T> event);
    }
}
