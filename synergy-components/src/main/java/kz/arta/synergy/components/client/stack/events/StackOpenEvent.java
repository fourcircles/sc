package kz.arta.synergy.components.client.stack.events;

import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.stack.SingleStack;

/**
 * User: vsl
 * Date: 20.08.14
 * Time: 12:36
 *
 * Событие открытия одной из стек-панелей
 */
public class StackOpenEvent extends GwtEvent<StackOpenEvent.Handler> {
    private static Type<Handler> TYPE;

    /**
     * Одна из стек панелей
     */
    private SingleStack stack;

    /**
     * Позиция стек панели
     */
    private int index;

    public StackOpenEvent(SingleStack stack, int index) {
        this.stack = stack;
        this.index = index;
    }

    public Type<Handler> getAssociatedType() {
        return getType();
    }

    protected void dispatch(Handler handler) {
        handler.onStackOpened(this);
    }

    public static interface Handler extends EventHandler {
        void onStackOpened(StackOpenEvent event);
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }


    public SingleStack getStack() {
        return stack;
    }

    public int getIndex() {
        return index;
    }
}
