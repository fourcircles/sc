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
public class FileComment extends AbstractComment {
    /**
     * Иконка файла
     */
    private ImageResource image;

    /**
     * Имя файла
     */
    private String fileName;

    private Image icon;

    public FileComment(ImageResource image, String fileName,
                       String author, Date date, CommentType type) {
        super(author, date, type);

        this.image = image;
        this.fileName = fileName;

        if (image != null) {
            icon = new Image();
            icon.setResource(image);
        }

    }

    private native String getOuterHTML(Element e)
    /*-{
        return e.outerHTML;
    }-*/;

    @Override
    public String getText() {
        return icon == null ? fileName : getOuterHTML(icon.getElement()) + fileName;
    }

}
