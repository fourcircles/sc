import com.google.gwt.dom.client.Style;
import kz.arta.synergy.components.client.comments.*;
import kz.arta.synergy.components.client.resources.ImageResources;

import java.util.Date;

public class Sample {
    public static void main(String[] args) {
        // по умолчанию панель комментариев -- белая
        CommentsPanel commentsPanel = new CommentsPanel();

        // задание позиции
        Style style = commentsPanel.getElement().getStyle();
        style.setPosition(Style.Position.ABSOLUTE);
        style.setTop(20, Style.Unit.PX);
        style.setBottom(20, Style.Unit.PX);
        style.setLeft(20, Style.Unit.PX);
        commentsPanel.setWidth("400px");

        // обычный текстовый комментарий
        TextComment comment1 = new TextComment("Поле ввода текста комментария «растягивается» вниз при увеличении количества строк, но не более чем на 10 строк. После ввода 11-й строки появляется полоса прокрутки в поле ввода.",
                "John Doe", new Date(), CommentType.GENERAL);
        comment1.setDeletable(false);

        // текстовый комментарий подтверждения, не могут быть удалены
        Comment comment2 = new TextComment("Все хорошо.\n--\n http://arta.pro", "John Doe", new Date(), CommentType.ACCEPT);
        // текстовый комментарий отклюнения
        Comment comment3 = new TextComment("Все плохо.", "Jane Doe", new Date(), CommentType.DECLINE);

        Comment comment4 = new TextComment("Меня никто не увидит, потому что я буду удален", "Jane Doe", new Date(), CommentType.GENERAL);

        // добавление комментариев
        commentsPanel.getComments().addComment(comment1);
        commentsPanel.getComments().addComment(comment2);
        commentsPanel.getComments().addComment(comment3);

        // удаление комментария
        commentsPanel.getComments().removeComment(comment4);

        // комментарий с файлом и иконкой
        Comment fileComment = new FileComment(ImageResources.IMPL.calendarIcon(), "calendar.png", "John Doe", new Date(), CommentType.ACCEPT);
        commentsPanel.getComments().addComment(fileComment);
    }
}