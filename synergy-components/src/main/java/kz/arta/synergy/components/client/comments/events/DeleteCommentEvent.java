package kz.arta.synergy.components.client.comments.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.comments.Comment;

/**
 * User: vsl
 * Date: 26.09.14
 * Time: 15:18
 *
 * Событие удаления комментария
 */
public class DeleteCommentEvent extends GwtEvent<DeleteCommentEvent.Handler> {
    private static Type<Handler> TYPE;

    private Comment comment;

    public DeleteCommentEvent(Comment comment) {
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
        handler.onDeleteComment(this);
    }

    public static interface Handler extends EventHandler {
        void onDeleteComment(DeleteCommentEvent event);
    }

    public Comment getComment() {
        return comment;
    }
}
