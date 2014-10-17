package kz.arta.synergy.components.client.comments;

import java.util.Date;

/**
 * User: vsl
 * Date: 26.09.14
 * Time: 14:03
 *
 * Простой пример реализации {@link kz.arta.synergy.components.client.comments.Comment}
 * Используется при добавлении нового комментария через форму ввода комментария {@link kz.arta.synergy.components.client.comments.CommentInput}
 */
public class TextComment extends AbstractComment {
    private String text;

    public TextComment(String text, String author, Date date, CommentType type) {
        super(author, date, type);
        this.text = text;
    }

    /**
     * Конструктор копирования
     */
    public TextComment(TextComment comment) {
        super(comment.getAuthor(), comment.getDate(), comment.getType());
        this.text = comment.getText();
    }

    @Override
    public String getText() {
        return text;
    }
}
