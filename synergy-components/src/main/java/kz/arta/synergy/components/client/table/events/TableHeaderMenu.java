package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.table.column.ArtaColumn;

/**
 * User: vsl
 * Date: 30.09.14
 * Time: 15:30
 */
public class TableHeaderMenu<T> extends GwtEvent<TableHeaderMenu.Handler<T>> {
    public static Type<Handler<?>> TYPE = new Type<Handler<?>>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<T> handler) {
        handler.onTableHeaderMenu(this);
    }

    public static interface Handler<V> extends EventHandler {
        void onTableHeaderMenu(TableHeaderMenu<V> event);
    }

    private ArtaColumn<T, ?> column;

    private int x;
    private int y;

    public TableHeaderMenu(ArtaColumn<T, ?> column, int x, int y) {
        this.column = column;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArtaColumn<T, ?> getColumn() {
        return column;
    }
}
