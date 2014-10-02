package kz.arta.synergy.components.client.tree.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.tree.TreeItem;

/**
 * User: vsl
 * Date: 12.09.14
 * Time: 14:13
 *
 * Событие выбора узла дерева
 */
public class TreeSelectionEvent extends GwtEvent<TreeSelectionEvent.Handler> {
    private static Type<Handler> TYPE;

    private TreeItem treeItem;

    public TreeSelectionEvent(TreeItem treeItem) {
        this.treeItem = treeItem;
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
        handler.onTreeSelection(this);
    }

    public static interface Handler extends EventHandler {
        void onTreeSelection(TreeSelectionEvent event);
    }

    public TreeItem getTreeItem() {
        return treeItem;
    }
}
