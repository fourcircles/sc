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
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onDeleteComment(this);
    }

    public static interface Handler extends EventHandler {
        void onDeleteComment(DeleteCommentEvent event);
    }

    private Comment comment;

    public DeleteCommentEvent(Comment comment) {
        this.comment = comment;
    }

    public Comment getComment() {
        return comment;
    }
}
