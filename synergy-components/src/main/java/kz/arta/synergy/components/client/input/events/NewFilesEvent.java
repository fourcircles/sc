package kz.arta.synergy.components.client.input.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 03.11.14
 * Time: 17:16
 *
 * Событие добавления новых файлов в панель файлов
 * Никакой информации не несет, просто индикация.
 */
public class NewFilesEvent extends GwtEvent<NewFilesEvent.Handler> {
    public static final Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onNewFiles(this);
    }

    public static interface Handler extends EventHandler {
        void onNewFiles(NewFilesEvent event);
    }
}
