package kz.arta.synergy.components.client.table.column;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.table.Header;

/**
 * User: vsl
 * Date: 04.09.14
 * Time: 17:41
 */
public abstract class AbstractArtaColumn<T> implements ArtaColumn<T> {
    /**
     * Сортируем ли столбец
     */
    protected boolean isSortable = false;

    /**
     * Название столбца
     */
    private String dataStoreName;

    /**
     * Текст заголовка
     */
    private Header header;

    protected AbstractArtaColumn(String headerText) {
        header = new Header(headerText);
    }

    public boolean isSortable() {
        return isSortable;
    }

    @Override
    public Header getHeader() {
        return header;
    }

    public void setHeaderText(String text) {
        header.setText(text);
    }

    @Override
    public void setDataStoreName(String name) {
        this.dataStoreName = name;
    }

    @Override
    public String getDataStoreName() {
        return dataStoreName;
    }

    @Override
    public String toString() {
        return getHeader().getText();
    }

    public abstract Widget createWidget(T object, EventBus bus);

    /**
     * Проставляет цвета фона и текста.
     *
     * @param widget виджет
     * @param object новый объект
     */
    public void updateWidget(Widget widget, T object) {
        widget.getElement().getParentElement().getStyle().setBackgroundColor(getBackgroundColor(object));
        String textColor = getTextColor(object);
        if (textColor != null) {
            widget.getElement().getParentElement().getStyle().setColor(textColor);
        }
    }

    public String getBackgroundColor(T object) {
        return "transparent";
    }

    public String getTextColor(T object) {
        return null;
    }
}
