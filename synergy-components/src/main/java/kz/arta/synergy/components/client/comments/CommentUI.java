package kz.arta.synergy.components.client.comments;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.comments.events.DeleteCommentEvent;
import kz.arta.synergy.components.client.resources.ImageResources;

/**
 * User: vsl
 * Date: 26.09.14
 * Time: 12:37
 *
 * Вид комментария
 */
public class CommentUI extends Composite {
    /**
     * Формат даты в комментарии
     */
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd MMM yyyy HH:mm");
    /**
     * Комментарий
     */
    private Comment comment;
    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Элемент для автора
     */
    private Label authorLabel;
    /**
     * Элемент для даты
     */
    private Label dateLabel;
    /**
     * Иконка
     */
    private Image icon;
    /**
     * Текст комментария
     */
    private HTML content;

    /**
     * @param comment комментарий
     */
    public CommentUI(final Comment comment) {
        root = new FlowPanel();
        initWidget(root);
        setStyleName(SynergyComponents.resources.cssComponents().comment());
        this.comment = comment;

        authorLabel = new HTML();
        authorLabel.setStyleName(SynergyComponents.resources.cssComponents().name());
        authorLabel.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());

        dateLabel = new Label();
        dateLabel.setStyleName(SynergyComponents.resources.cssComponents().date());

        icon = new Image();

        content = new HTML();
        content.setStyleName(SynergyComponents.resources.cssComponents().content());

        root.add(authorLabel);
        root.add(dateLabel);
        root.add(content);

        root.add(icon);

        icon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (comment.getType() == CommentType.GENERAL &&
                        comment.isDeletable()) {
                    fireEvent(new DeleteCommentEvent(comment));
                }
            }
        });

        update();
    }

    /**
     * Обновляет иконку в соответствии с комментарием
     */
    private void updateIcon() {
        root.removeStyleName(SynergyComponents.resources.cssComponents().accept());
        root.removeStyleName(SynergyComponents.resources.cssComponents().decline());
        switch (comment.getType()) {
            case ACCEPT:
                root.addStyleName(SynergyComponents.resources.cssComponents().accept());
                icon.setResource(ImageResources.IMPL.positiveComment());
                break;
            case DECLINE:
                root.addStyleName(SynergyComponents.resources.cssComponents().decline());
                icon.setResource(ImageResources.IMPL.negativeComment());
                break;
            default:
                icon.setResource(ImageResources.IMPL.deleteComment());
        }

    }

    /**
     * Общее обновление вида при изменении комментария
     */
    public void update() {
        updateIcon();

        authorLabel.setText(comment.getAuthor());
        dateLabel.setText(" - " + DATE_FORMAT.format(comment.getDate()));
        content.setHTML(comment.getText());
    }

    /**
     * Изменить комментарий
     * @param comment новый комментарий
     */
    public void setComment(Comment comment) {
        this.comment = comment;
        update();
    }

    /**
     * Добавляет хэндлер на удаление коммента
     */
    public HandlerRegistration addDeleteHandler(DeleteCommentEvent.Handler handler) {
        return addHandler(handler, DeleteCommentEvent.getType());
    }
}
