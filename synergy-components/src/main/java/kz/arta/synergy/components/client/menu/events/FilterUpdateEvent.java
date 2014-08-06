package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 06.08.14
 * Time: 13:50
 */
public class FilterUpdateEvent extends GwtEvent<FilterUpdateEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onFilterUpdate(this);
    }

    public static interface Handler extends EventHandler {
        void onFilterUpdate(FilterUpdateEvent event);
    }

    public static interface HasHandlers {
        void onFilterUpdate(Handler handler);
    }
}
