package kz.arta.synergy.components.client.table.column;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.table.Header;
import kz.arta.synergy.components.client.table.events.ColumnLockEvent;

/**
 * User: vsl
 * Date: 04.09.14
 * Time: 17:41
 */
public abstract class AbstractArtaColumn<T> implements ArtaColumn<T> {

    private EventBus columnBus;

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

    /**
     * Можно ли изменять ширину пользователю
     */
    protected boolean locked = false;

    protected AbstractArtaColumn(String headerText, EventBus bus) {
        this(headerText);
        columnBus = bus;
    }

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

    @SuppressWarnings("UnusedDeclaration")
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
        String backgroundColor = getBackgroundColor(object);
        if (backgroundColor != null) {
            widget.getElement().getParentElement().getStyle().setBackgroundColor(backgroundColor);
        }
        String textColor = getTextColor(object);
        if (textColor != null) {
            widget.getElement().getParentElement().getStyle().setColor(textColor);
        }
    }

    public String getBackgroundColor(T object) {
        return null;
    }

    public String getTextColor(T object) {
        return null;
    }

    @Override
    public void setResizeLock(boolean locked) {
        this.locked = locked;
        if (columnBus != null) {
            getBus().fireEventFromSource(new ColumnLockEvent(locked), this);
        }
    }

    @Override
    public boolean isResizable() {
        return !locked;
    }

    @Override
    public EventBus getBus() {
        return columnBus;
    }

    @Override
    public void setBus(EventBus bus) {
        this.columnBus = bus;
    }

    @Override
    public HandlerRegistration addLockHandler(ColumnLockEvent.Handler handler) {
        if (getBus() != null) {
            return getBus().addHandlerToSource(ColumnLockEvent.TYPE, this, handler);
        }
        return null;
    }
}
