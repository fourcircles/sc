package kz.arta.synergy.components.client.comments;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.comments.events.NewCommentEvent;

import java.util.Date;

/**
 * User: vsl
 * Date: 29.09.14
 * Time: 15:42
 *
 * Панель комментариев
 */
public class CommentsPanel extends Composite {

    /**
     * Виджет ввода комментария
     */
    private CommentInput commentInput;

    /**
     * Виджет отображения комментариев
     */
    private Comments comments;

    public CommentsPanel() {
        this(false);
    }

    /**
     * @param dark темная ли панель
     */
    public CommentsPanel(boolean dark) {
        FlowPanel root = new FlowPanel();
        initWidget(root);

        root.setStyleName(SynergyComponents.getResources().cssComponents().commentsPanel());

        if (dark) {
            root.addStyleName(SynergyComponents.getResources().cssComponents().dark());
        }

        commentInput = new CommentInput(dark);

        comments = new Comments(dark);
        comments.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        comments.getElement().getStyle().setLeft(0, Style.Unit.PX);
        comments.getElement().getStyle().setRight(0, Style.Unit.PX);

        root.add(commentInput);
        root.add(comments);

        commentInput.addNewCommentHandler(new NewCommentEvent.Handler() {
            @Override
            public void onNewComment(NewCommentEvent event) {
                TextComment comment = new TextComment(event.getComment(), "John Doe", new Date(), CommentType.GENERAL);
                comment.setDeletable(true);
                comments.addComment(comment);
            }
        });
        commentInput.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                comments.getElement().getStyle().setTop(commentInput.getOffsetHeight(), Style.Unit.PX);
                comments.onResize();
            }
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        comments.getElement().getStyle().setTop(commentInput.getOffsetHeight(), Style.Unit.PX);
    }

    public Comments getComments() {
        return comments;
    }
}
