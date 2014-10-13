package kz.arta.synergy.components.client.comments;

import java.util.Date;

/**
 * User: vsl
 * Date: 26.09.14
 * Time: 14:03
 *
 * Простой пример реализации {@link kz.arta.synergy.components.client.comments.Comment}
 */
public class TextComment extends AbstractComment {
    private String text;

    public TextComment(String text, String author, Date date, CommentType type) {
        super(author, date, type);
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
