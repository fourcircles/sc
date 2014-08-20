package kz.arta.synergy.components.client.stack.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.stack.Stack;

/**
 * User: vsl
 * Date: 20.08.14
 * Time: 12:36
 *
 * Событие открытия одной из стек-панелей
 */
public class StackOpenEvent extends GwtEvent<StackOpenEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onStackOpened(this);
    }

    public static interface Handler extends EventHandler {
        void onStackOpened(StackOpenEvent event);
    }

    /**
     * Одна из стек панелей
     */
    private Stack stack;

    /**
     * Позиция стек панели
     */
    private int index;

    public StackOpenEvent(Stack stack, int index) {
        this.stack = stack;
        this.index = index;
    }

    public Stack getStack() {
        return stack;
    }

    public int getIndex() {
        return index;
    }
}
