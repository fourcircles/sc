package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 26.11.14
 * Time: 16:53
 *
 * Событие, которым таблицы оповещает виджет таблицы о начале редактирования.
 */
public class StartEditEvent extends GwtEvent<StartEditEvent.StartEditEventHandler> {
    public static final Type<StartEditEventHandler> TYPE = new Type<StartEditEventHandler>();

    public Type<StartEditEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(StartEditEventHandler handler) {
        handler.onStartEdit(this);
    }

    public static interface StartEditEventHandler extends EventHandler {
        void onStartEdit(StartEditEvent event);
    }
}
