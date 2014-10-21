package kz.arta.synergy.components.client.dagger.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 17.10.14
 * Time: 18:16
 *
 * // todo so far we don't need this
 */
public class DaggerBlurEvent extends GwtEvent<DaggerBlurEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onDaggerBlur(this);
    }

    public static interface Handler extends EventHandler {
        void onDaggerBlur(DaggerBlurEvent event);
    }
}
