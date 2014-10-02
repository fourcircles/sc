package kz.arta.synergy.components.client.comments;

import java.util.Date;

/**
 * User: vsl
 * Date: 26.09.14
 * Time: 12:31
 *
 * Комментарий
 */
public interface Comment {
    /**
     * @return текст комментария
     */
    String getText();

    /**
     * @return автор
     */
    String getAutor();

    /**
     * @return дата
     */
    Date getDate();

    /**
     * @return тип комментария
     */
    CommentType getType();

    /**
     * @return можно ли удалить
     */
    boolean isDeletable();
}
