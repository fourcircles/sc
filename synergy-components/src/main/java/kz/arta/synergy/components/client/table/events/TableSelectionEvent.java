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
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onTableSelection(this);
    }

    public static interface Handler extends EventHandler {
        void onTableSelection(TableSelectionEvent event);
    }

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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
