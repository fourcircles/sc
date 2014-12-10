package kz.arta.synergy.components.client.table.column;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.table.Header;
import kz.arta.synergy.components.client.table.events.ColumnLockEvent;

/**
 * User: vsl
 * Date: 02.09.14
 * Time: 11:57
 *
 * Столбец. "Заимствован" из {@link com.google.gwt.user.cellview.client.Column}.
 */
public interface ArtaColumn<T> {
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
    @SuppressWarnings("UnusedDeclaration")
    void setDataStoreName(String name);

    /**
     * Возвращает название столбца
     */
    @SuppressWarnings("UnusedDeclaration")
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

    /**
     * Можно ли изменять ширину пользователю
     *
     * @param locked true - можно, false - нельзя
     */
    void setResizeLock(boolean locked);

    /**
     * Можно ли изменять ширину пользователю.
     * Реализация должна принимать во внимание метод {@link #setResizeLock(boolean)}
     */
    boolean isResizable();

    EventBus getBus();
    void setBus(EventBus bus);

    HandlerRegistration addLockHandler(ColumnLockEvent.Handler handler);
}
