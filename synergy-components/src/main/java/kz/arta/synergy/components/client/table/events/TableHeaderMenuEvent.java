package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.table.column.ArtaColumn;

/**
 * User: vsl
 * Date: 30.09.14
 * Time: 15:30
 *
 * Событие контекстного меню для хедера
 */
public class TableHeaderMenuEvent<T> extends GwtEvent<TableHeaderMenuEvent.Handler<T>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    private ArtaColumn<T> column;

    private int x;
    private int y;

    public TableHeaderMenuEvent(ArtaColumn<T> column, int x, int y) {
        this.column = column;
        this.x = x;
        this.y = y;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<T> handler) {
        handler.onTableHeaderMenu(this);
    }

    public static interface Handler<V> extends EventHandler {
        void onTableHeaderMenu(TableHeaderMenuEvent<V> event);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ArtaColumn<T> getColumn() {
        return column;
    }
}
