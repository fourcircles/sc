package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.menu.MenuItem;

/**
 * User: vsl
 * Date: 20.10.14
 * Time: 11:13
 *
 * Событие выбора элемента в списке
 */
public class MenuItemSelection<V> extends GwtEvent<MenuItemSelection.Handler<V>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    /**
     * Элемент
     */
    private MenuItem<V> item;

    /**
     * Выбран или наоборот
     */
    private boolean isSelected;

    public MenuItemSelection(MenuItem<V> item, boolean isSelected) {
        this.item = item;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public MenuItem<V> getItem() {
        return item;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<V>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<V> handler) {
        handler.onItemSelection(this);
    }

    public static interface Handler<T> extends EventHandler {
        void onItemSelection(MenuItemSelection<T> event);
    }
}
