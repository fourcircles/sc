package kz.arta.synergy.components.client.comments;

import java.util.Date;

/**
 * User: vsl
 * Date: 09.10.14
 * Time: 15:16
 */
public abstract class AbstractComment implements Comment {
    protected String author;
    protected Date date;
    protected CommentType type;
    protected boolean isDeletable = false;

    public AbstractComment(String author, Date date, CommentType type) {
        this.author = author;
        this.date = date;
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public CommentType getType() {
        return type;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }
}
