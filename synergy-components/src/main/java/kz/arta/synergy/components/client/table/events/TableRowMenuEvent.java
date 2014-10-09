package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 30.09.14
 * Time: 15:28
 */
public class TableRowMenuEvent<T> extends GwtEvent<TableRowMenuEvent.Handler<T>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    private T object;

    private int x;
    private int y;

    public TableRowMenuEvent(T object, int x, int y) {
        this.object = object;
        this.x = x;
        this.y = y;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<T> handler) {
        handler.onTableRowMenu(this);
    }

    public static interface Handler<V> extends EventHandler {
        void onTableRowMenu(TableRowMenuEvent<V> event);
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
