package kz.arta.synergy.components.client.comments.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 30.09.14
 * Time: 10:47
 *
 * Событие добавления нового комментария
 */
public class NewCommentEvent extends GwtEvent<NewCommentEvent.Handler> {
    private static Type<Handler> TYPE;

    private String comment;

    public NewCommentEvent(String comment) {
        this.comment = comment;
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    public Type<Handler> getAssociatedType() {
        return getType();
    }

    protected void dispatch(Handler handler) {
        handler.onNewComment(this);
    }

    public static interface Handler extends EventHandler {
        void onNewComment(NewCommentEvent event);
    }

    public String getComment() {
        return comment;
    }
}

