package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 03.09.14
 * Time: 10:30
 *
 * Событие выбора объекта в таблице
 */
public class TableSelectionEvent extends GwtEvent<TableSelectionEvent.Handler> {
    private static Type<Handler> TYPE;

    private int row;
    private int column;

    public TableSelectionEvent(int row) {
        this.row = row;
        column = -1;
    }

    public TableSelectionEvent(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    public Type<Handler> getAssociatedType() {
        return getType();
    }

    protected void dispatch(Handler handler) {
        handler.onTableSelection(this);
    }

    public static interface Handler extends EventHandler {
        void onTableSelection(TableSelectionEvent event);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
