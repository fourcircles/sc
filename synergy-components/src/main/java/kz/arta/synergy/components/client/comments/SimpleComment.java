package kz.arta.synergy.components.client.comments;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import kz.arta.synergy.components.client.util.Utils;

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
        this.text = Utils.parseComment(SafeHtmlUtils.htmlEscape(text));
        this.author = author;
        this.date = date;
        this.type = type;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getAuthor() {
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
