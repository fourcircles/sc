package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.table.column.ArtaColumn;

/**
 * User: vsl
 * Date: 03.09.14
 * Time: 12:39
 *
 * Событие изменения значения в таблице
 */
public class CellEditEvent<T> extends GwtEvent<CellEditEvent.Handler<T>> {
    public static Type<Handler<?>> TYPE = new Type<Handler<?>>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<T> handler) {
        switch (editType) {
            case EDIT_START:
                handler.onEdit(this);
                break;
            case COMMIT:
                handler.onCommit(this);
                break;
            case CANCEL:
                handler.onCancel(this);
                break;
        }
    }

    public static interface Handler<V> extends EventHandler {
        void onCommit(CellEditEvent<V> event);
        void onCancel(CellEditEvent<V> event);
        void onEdit(CellEditEvent<V> event);
    }

    /**
     * Объект
     */
    private T object;

    /**
     * Столбец
     */
    private ArtaColumn<T, ?> column;

    /**
     * Тип события
     */
    private EditType editType;

    /**
     * Перевести ли выделение на следующую ячейку после завершения
     * изменения
     */
    private boolean jumpForward;

    public CellEditEvent(T object, ArtaColumn<T, ?> column, EditType type, boolean jumpForward) {
        this.object = object;
        this.column = column;
        this.editType = type;
        this.jumpForward = jumpForward;
    }

    public CellEditEvent(T object, ArtaColumn<T, ?> column, EditType type) {
        this(object, column, type, false);
    }

    public T getObject() {
        return object;
    }

    public ArtaColumn<T, ?> getColumn() {
        return column;
    }

    public boolean jumpForward() {
        return jumpForward;
    }

    /**
     * Вид события
     */
    public enum EditType {
        /**
         * Начало изменения
         */
        EDIT_START,

        /**
         * Успешное изменение значения
         */
        COMMIT,

        /**
         * Отмена изменения
         */
        CANCEL
    }
}
