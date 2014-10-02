package kz.arta.synergy.components.client.tree.events;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.tree.TreeItem;

/**
 * User: vsl
 * Date: 17.09.14
 * Time: 18:01
 *
 * Событие для контекстного меню дерева
 */
public class TreeItemContextMenuEvent extends GwtEvent<TreeItemContextMenuEvent.Handler> {
    private static Type<Handler> TYPE;

    /**
     * Событие gwt
     */
    private ContextMenuEvent event;

    /**
     * Элемент дерева, для которого надо отобразить контекстное меню
     */
    private TreeItem item;

    /**
     * Координаты клика мыши
     */
    private int x;
    private int y;

    public TreeItemContextMenuEvent(TreeItem item, ContextMenuEvent event) {
        this.item = item;
        x = event.getNativeEvent().getClientX();
        y = event.getNativeEvent().getClientY();
        this.event = event;
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
        handler.onTreeContextMenu(this);
    }

    public static interface Handler extends EventHandler {
        void onTreeContextMenu(TreeItemContextMenuEvent event);
    }

    public TreeItem getItem() {
        return item;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ContextMenuEvent getEvent() {
        return event;
    }
}
