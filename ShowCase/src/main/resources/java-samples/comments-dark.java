import com.google.gwt.dom.client.Style;
import kz.arta.synergy.components.client.comments.*;
import kz.arta.synergy.components.client.resources.ImageResources;

import java.util.Date;

public class Sample {
    public static void main(String[] args) {
        // черная панель комментариев == панель заметок
        CommentsPanel commentsPanel = new CommentsPanel(true);

        // обычный текстовый комментарий
        TextComment comment1 = new TextComment("Поле ввода текста комментария «растягивается» вниз при увеличении количества строк, но не более чем на 10 строк. После ввода 11-й строки появляется полоса прокрутки в поле ввода.",
                "John Doe", new Date(), CommentType.GENERAL);
        comment1.setDeletable(false);

        // добавление комментария
        commentsPanel.getComments().addComment(comment1);
    }
}