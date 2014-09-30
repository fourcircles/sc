package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 30.09.14
 * Time: 15:28
 */
public class TableRowMenu<T> extends GwtEvent<TableRowMenu.Handler<T>> {
    public static Type<Handler<?>> TYPE = new Type<Handler<?>>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<T> handler) {
        handler.onTableRowMenu(this);
    }

    public static interface Handler<V> extends EventHandler {
        void onTableRowMenu(TableRowMenu<V> event);
    }

    private T object;

    private int x;
    private int y;

    public TableRowMenu(T object, int x, int y) {
        this.object = object;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public T getObject() {
        return object;
    }
}
