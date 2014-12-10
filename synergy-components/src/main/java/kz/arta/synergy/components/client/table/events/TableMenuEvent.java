package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 27.11.14
 * Time: 9:39
 *
 * События контекстного меню для внутренней таблицы.
 *
 * Для выяснения текущего выделения: {@link kz.arta.synergy.components.client.table.TableSelectionModel#getSelectedColumns(Object)}
 * и {@link kz.arta.synergy.components.client.table.TableSelectionModel#getSelectedObjects()}
 */
public class TableMenuEvent extends GwtEvent<TableMenuEvent.Handler> {
    public static final Type<Handler> TYPE = new Type<Handler>();

    /**
     * Координаты клика
     */
    private int x;
    private int y;

    public TableMenuEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onTableMenu(this);
    }

    public static interface Handler extends EventHandler {
        void onTableMenu(TableMenuEvent event);
    }
}
