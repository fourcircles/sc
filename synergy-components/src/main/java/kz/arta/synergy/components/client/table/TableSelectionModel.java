package kz.arta.synergy.components.client.table;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import kz.arta.synergy.components.client.table.column.ArtaColumn;

/**
 * User: vsl
 * Date: 16.09.14
 * Time: 14:18
 *
 * Модель выбора для таблицы
 */
public class TableSelectionModel<T> implements SelectionModel<T> {
    private EventBus bus;
    /**
     * Выбранный объект
     */
    private T selectedObject;

    /**
     * Выбранный столбец
     */
    private ArtaColumn<T, ?> selectedColumn;

    /**
     * Предоставляет ключи для объектов
     */
    private ProvidesKey<T> keyProvider;

    public TableSelectionModel(EventBus bus, ProvidesKey<T> keyProvider) {
        this.bus = bus;
        this.keyProvider = keyProvider;
    }

    @Override
    public HandlerRegistration addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
        return bus.addHandler(SelectionChangeEvent.getType(), handler);
    }

    @Override
    public boolean isSelected(T object) {
        return selectedObject == object;
    }

    public void setSelected(T object, ArtaColumn<T, ?> column,
                            boolean selected, boolean fireEvents) {
        T newObject;
        ArtaColumn<T, ?> newColumn;
        if (selected) {
            newObject = object;
            newColumn = column;
        } else {
            if (object == selectedObject && (column == null || column == selectedColumn)) {
                //если объекты совпадают или деселект всей строки
                newObject = null;
                newColumn = null;
            } else {
                //деселект невыбранного объекта
                return;
            }
        }
        if (newObject != selectedObject || newColumn != selectedColumn) {
            selectedObject = newObject;
            selectedColumn = newColumn;
            if (fireEvents) {
                SelectionChangeEvent.fire(this);
            }
        }
    }

    /**
     * Выбирает или снимает выделение ячейки или ряда
     * @param object объект
     * @param column столбец; если null то предполагается, что работаем с рядом
     * @param selected выделить или снять выделение
     */
    public void setSelected(T object, ArtaColumn<T, ?> column, boolean selected) {
        setSelected(object, column, selected, true);
    }

    /**
     * Выделить/снять выделение ряда
     */
    @Override
    public void setSelected(T object, boolean selected) {
        setSelected(object, null, selected);
    }

    /**
     * Снять выделение
     */
    public void clear() {
        setSelected(selectedObject, selectedColumn, false);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEvent(event);
    }

    @Override
    public Object getKey(T item) {
        if (keyProvider != null) {
            return keyProvider.getKey(item);
        }
        return null;
    }

    public T getSelectedObject() {
        return selectedObject;
    }

    public ArtaColumn<T, ?> getSelectedColumn() {
        return selectedColumn;
    }
}
