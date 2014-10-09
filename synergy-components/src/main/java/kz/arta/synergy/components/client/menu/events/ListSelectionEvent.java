package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import kz.arta.synergy.components.client.menu.DropDownList;

/**
 * User: vsl
 * Date: 01.08.14
 * Time: 11:24
 *
 * Событие выбора элемента списка
 */
public class ListSelectionEvent<V> extends GwtEvent<ListSelectionEvent.Handler<V>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    /**
     * Выбор или снятие выбора для списков предусматривающих выбор нескольких элементов
     */
    public enum ActionType {
        SELECT, DESELECT
    }

    /**
     * Элемент списка
     */
    private DropDownList<V>.Item item;

    /**
     * Тип действия
     */
    private ActionType actionType;

    public ListSelectionEvent(DropDownList<V>.Item item, ActionType type) {
        this.item = item;
        this.actionType = type;
    }

    public ListSelectionEvent(DropDownList<V>.Item item) {
        this(item, ActionType.SELECT);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Type<Handler<V>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<V> handler) {
        if (actionType == ActionType.SELECT) {
            handler.onSelection(this);
        } else {
            handler.onDeselection(this);
        }
    }

    public static abstract class Handler<T> implements EventHandler {
        public abstract void onSelection(ListSelectionEvent<T> event);
        public void onDeselection(ListSelectionEvent<T> event) {
        }
    }

    public DropDownList<V>.Item getItem() {
        return item;
    }

    public static HandlerRegistration register(EventBus bus, Handler<?> handler) {
        return bus.addHandler(TYPE, handler);
    }

}
