package kz.arta.synergy.components.client.table.column;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.table.Header;

/**
 * User: vsl
 * Date: 02.09.14
 * Time: 11:57
 *
 * Столбец. "Заимствован" из {@link com.google.gwt.user.cellview.client.Column}.
 */
public interface ArtaColumn<T> {
//    /**
//     * Возвращает логическое значение столбца.
//     * По этому значение будет производиться сортировка.
//     * @param object объект
//     */
//    Object getValue(T object);

    /**
     * Создает новый виджет для отображения объекта
     * @param object объект
     * @param bus event bus
     */
    Widget createWidget(T object, EventBus bus);

    /**
     * Обновляет виджет с новым объектом
     * @param widget виджет
     * @param object новый объект
     */
    void updateWidget(Widget widget, T object);

    /**
     * Задает название столбца
     * @param name название
     */
    void setDataStoreName(String name);

    /**
     * Возвращает название столбца
     */
    String getDataStoreName();

    /**
     * Можно ли изменять значения в этой колонке.
     * При навигации клавиатурой ячейки в этой колонке будут пропускаться.
     */
    boolean isEditable();

    /**
     * Поддерживается ли сортировка по этому столбцу
     */
    boolean isSortable();

    /**
     * Минимальная ширина столбца
     */
    int getMinWidth();

    /**
     * Возвращает заголовок столбца
     */
    Header getHeader();
}
