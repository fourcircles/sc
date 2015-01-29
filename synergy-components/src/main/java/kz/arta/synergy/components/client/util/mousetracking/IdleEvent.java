package kz.arta.synergy.components.client.util.mousetracking;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 29.01.15
 * Time: 14:58
 */
public class IdleEvent extends GwtEvent<IdleEvent.Handler> {
    public static final Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onMouseTracking(this);
    }

    public static interface Handler extends EventHandler {
        void onMouseTracking(IdleEvent event);
    }
}
