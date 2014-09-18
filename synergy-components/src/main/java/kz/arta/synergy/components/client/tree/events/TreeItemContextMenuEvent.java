package kz.arta.synergy.components.client.tree.events;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.tree.TreeItem;

/**
 * User: vsl
 * Date: 17.09.14
 * Time: 18:01
 */
public class TreeItemContextMenuEvent extends GwtEvent<TreeItemContextMenuEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onTreeContextMenu(this);
    }

    public static interface Handler extends EventHandler {
        void onTreeContextMenu(TreeItemContextMenuEvent event);
    }

    private ContextMenuEvent event;
    private TreeItem item;
    private int x;
    private int y;

    public TreeItemContextMenuEvent(TreeItem item, int x, int y, ContextMenuEvent event) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.event = event;
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
