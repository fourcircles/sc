package kz.arta.synergy.components.client.tree.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.tree.TreeItem;

/**
 * User: vsl
 * Date: 12.09.14
 * Time: 14:49
 *
 * Событие открытия узла дерева
 */
public class TreeOpenEvent extends GwtEvent<TreeOpenEvent.Handler> {
    private static Type<Handler> TYPE;

    private TreeItem item;
    private boolean open;

    public TreeOpenEvent(TreeItem item, boolean open) {
        this.item = item;
        this.open = open;
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
        handler.onTreeOpen(this);
    }

    public static interface Handler extends EventHandler {
        void onTreeOpen(TreeOpenEvent event);
    }

    public TreeItem getItem() {
        return item;
    }

    public boolean isOpen() {
        return open;
    }
}
