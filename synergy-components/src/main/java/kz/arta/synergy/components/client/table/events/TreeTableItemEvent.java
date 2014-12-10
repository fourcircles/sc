package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.table.column.TreeTableItem;

/**
 * User: vsl
 * Date: 08.10.14
 * Time: 10:13
 *
 * Событие для открытия/закрытия в дереве-таблице.
 */
public class TreeTableItemEvent<T extends TreeTableItem<T>> extends GwtEvent<TreeTableItemEvent.Handler<T>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    private EventType eventType;
    private T item;

    public TreeTableItemEvent(T item, EventType type) {
        this.eventType = type;
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<T> handler) {
        switch (eventType) {
            case CLOSE:
                handler.onClose(this);
                break;
            case OPEN:
                handler.onOpen(this);
                break;
            case LOADING:
                handler.onLoading(this);
                break;
            default:
        }
    }

    public static interface Handler<V extends TreeTableItem<V>> extends EventHandler {
        void onClose(TreeTableItemEvent<V> event);
        void onOpen(TreeTableItemEvent<V> event);
        void onLoading(TreeTableItemEvent<V> event);
    }

    public enum EventType {
        OPEN, LOADING, CLOSE
    }
}
