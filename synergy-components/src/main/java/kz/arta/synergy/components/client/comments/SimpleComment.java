package kz.arta.synergy.components.client.comments;

import java.util.Date;

/**
 * User: vsl
 * Date: 26.09.14
 * Time: 14:03
 *
 * Простой пример реализации {@link kz.arta.synergy.components.client.comments.Comment}
 */
public class SimpleComment implements Comment {
    private String text;
    private String author;
    private Date date;
    private CommentType type;

    public SimpleComment(String text, String author, Date date, CommentType type) {
        this.text = text;
        this.author = author;
        this.date = date;
        this.type = type;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getAutor() {
        return author;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public CommentType getType() {
        return type;
    }

    @Override
    public boolean isDeletable() {
        return true;
    }
}
