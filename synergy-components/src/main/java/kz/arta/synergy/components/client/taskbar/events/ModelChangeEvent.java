package kz.arta.synergy.components.client.taskbar.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 03.10.14
 * Time: 10:08
 *
 * Событие изменения состояния модели
 */
public class ModelChangeEvent extends GwtEvent<ModelChangeEvent.Handler> {
    private static Type<Handler> TYPE;

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onModelChange(this);
    }

    public static interface Handler extends EventHandler {
        void onModelChange(ModelChangeEvent event);
    }
}
