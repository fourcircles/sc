package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 17:54
 *
 * Событие выбора значения в списке
 */
public class SelectionEvent<V> extends GwtEvent<SelectionEvent.Handler<V>> {
    public static final Type<Handler<?>> TYPE = new Type<Handler<?>>();

    /**
     * Значение
     */
    private V value;

    /**
     * Тип действия
     */
    private ActionType actionType;

    public SelectionEvent(V value) {
        this(value, ActionType.SELECT);
    }

    public SelectionEvent(V value, ActionType actionType) {
        this.value = value;
        this.actionType = actionType;
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

    public interface Handler<T> extends EventHandler {
        void onSelection(SelectionEvent<T> event);
        void onDeselection(SelectionEvent<T> event);
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public static HandlerRegistration register(EventBus bus, Handler<?> handler) {
        return bus.addHandler(TYPE, handler);
    }

    /**
     * Выбор или снятие выбора для списков предусматривающих выбор нескольких элементов
     */
    public enum ActionType {
        SELECT, DESELECT
    }
}
