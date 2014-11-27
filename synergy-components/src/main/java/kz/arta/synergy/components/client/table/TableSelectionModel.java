package kz.arta.synergy.components.client.table;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import kz.arta.synergy.components.client.table.column.ArtaColumn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: vsl
 * Date: 26.11.14
 * Time: 10:46
 *
 * Модель выбора ячейки для таблицы.
 * Поддерживает одновременный выбор нескольких объектов.
 */
public class TableSelectionModel<T> implements SelectionModel<T> {
    private final EventBus bus;
    private final ProvidesKey<T> keyProvider;

    /**
     * Выбранные объекты.
     */
    private Map<T, Set<ArtaColumn<T>>> selection;

    public TableSelectionModel(EventBus bus, ProvidesKey<T> keyProvider) {
        this.bus = bus;
        this.keyProvider = keyProvider;

        selection = new HashMap<T, Set<ArtaColumn<T>>>();
    }

    @Override
    public HandlerRegistration addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
        return bus.addHandler(SelectionChangeEvent.getType(), handler);
    }

    /**
     * Есть ли выбранные объекты на строке, соответствующей объекту (это включает саму строку).
     * @param object объект
     */
    @Override
    public boolean isSelected(T object) {
        return selection.containsKey(object);
    }

    /**
     * Выбран ли заданный объект.
     * @param object объект соответствующий строке
     * @param column столбец, если null, то запрос о строке
     */
    public boolean isSelected(T object, ArtaColumn<T> column) {
        return selection.containsKey(object) && selection.get(object).contains(column);
    }

    /**
     * Добавляет или удаляет из выделения строку, соответствующую заданному объекту
     * @param object объект
     * @param selected true - добавить, false - удалить
     */
    @Override
    public void setSelected(T object, boolean selected) {
        setSelected(object, selected, true);
    }

    /**
     * @param fireEvents создавать ли событие об изменении выделения
     */
    public void setSelected(T object, boolean selected, boolean fireEvents) {
        setSelected(object, null, selected, fireEvents);
    }

    /**
     * Добавляет или удалет из выделения строку или столбец.
     * @param object объект
     * @param column строка, если null, то речь идет о строке
     * @param selected true - добавить, false - удалить
     * @param fireEvents создавать ли событие об изменении выделения
     */
    public void setSelected(T object, ArtaColumn<T> column,
                            boolean selected, boolean fireEvents) {
        boolean change = false;

        if (selected) {
            if (selection.containsKey(object)) {
                change = !selection.get(object).contains(column);
                selection.get(object).add(column);
            } else {
                change = true;

                Set<ArtaColumn<T>> newSet = new HashSet<ArtaColumn<T>>();
                selection.put(object, newSet);
                newSet.add(column);
            }
        } else if (selection.containsKey(object) && selection.get(object).contains(column)) {
            change = true;
            selection.get(object).remove(column);
            if (selection.get(object).isEmpty()) {
                selection.remove(object);
            }
        }

        if (change && fireEvents) {
            SelectionChangeEvent.fire(this);
        }
    }

    /**
     * Возвращает выделенные столбцы в строке заданного объекта.
     * @param object объект
     * @return выделенные ячейки
     */
    public Set<ArtaColumn<T>> getSelectedColumns(T object) {
        return selection.containsKey(object) ? selection.get(object) : null;
    }

    public Set<T> getSelectedObjects() {
        return selection.keySet();
    }

    public void clear() {
        clear(true);
    }

    /**
     * Удаляет все объекты из выделения
     * @param fireEvents создавать ли событие об изменении выделения
     */
    public void clear(boolean fireEvents) {
        boolean change = !selection.keySet().isEmpty();
        selection.clear();

        if (change && fireEvents) {
            SelectionChangeEvent.fire(this);
        }
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEvent(event);
    }

    @Override
    public Object getKey(T item) {
        return keyProvider != null ? keyProvider.getKey(item) : null;
    }
}
