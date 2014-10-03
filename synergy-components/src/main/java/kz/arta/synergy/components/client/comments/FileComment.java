package kz.arta.synergy.components.client.comments;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

import java.util.Date;

/**
 * User: vsl
 * Date: 03.10.14
 * Time: 17:38
 *
 * Комментарий с файлом
 */
public class FileComment implements Comment {
    /**
     * Иконка файла
     */
    private ImageResource image;

    /**
     * Имя файла
     */
    private String fileName;

    private String author;
    private Date date;
    private CommentType type;

    private Image icon;

    public FileComment(ImageResource image, String fileName,
                       String author, Date date, CommentType type) {
        this.image = image;
        this.fileName = fileName;
        this.author = author;
        this.date = date;
        this.type = type;

        if (image != null) {
            icon = new Image();
            icon.setResource(image);
        }

    }

    private native String getOuterHTML(Element e) /*-{
        return e.outerHTML;
    }-*/;

    @Override
    public String getText() {
        return icon == null ? fileName : getOuterHTML(icon.getElement()) + fileName;
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
        return false;
    }
}
