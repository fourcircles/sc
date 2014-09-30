package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.table.column.ArtaColumn;

/**
 * User: vsl
 * Date: 30.09.14
 * Time: 15:10
 */
public class TableCellMenu<T> extends GwtEvent<TableCellMenu.Handler<T>> {
    public static Type<Handler<?>> TYPE = new Type<Handler<?>>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<T> handler) {
        handler.onTableCellMenu(this);
    }

    public static interface Handler<V> extends EventHandler {
        void onTableCellMenu(TableCellMenu<V> event);
    }

    private T object;
    private ArtaColumn<T, ?> column;

    private int x;
    private int y;

    public TableCellMenu(T object, ArtaColumn<T, ?> column, int x, int y) {
        this.object = object;
        this.column = column;
        this.x = x;
        this.y = y;
    }

    public ArtaColumn<T, ?> getColumn() {
        return column;
    }

    public T getObject() {
        return object;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
