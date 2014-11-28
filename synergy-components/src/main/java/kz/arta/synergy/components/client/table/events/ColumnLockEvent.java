package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 28.11.14
 * Time: 13:53
 */
public class ColumnLockEvent extends GwtEvent<ColumnLockEvent.Handler> {
    public static final Type<Handler> TYPE = new Type<Handler>();

    private boolean locked;

    public ColumnLockEvent(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onColumnLock(this);
    }

    public static interface Handler extends EventHandler {
        void onColumnLock(ColumnLockEvent event);
    }
}
