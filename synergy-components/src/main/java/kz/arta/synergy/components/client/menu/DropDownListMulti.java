package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: vsl
 * Date: 21.10.14
 * Time: 11:06
 *
 * Выпадающий список с возможностью выбора нескольких значений
 */
public class DropDownListMulti<V> extends DropDownList<V> {

    private Set<V> selectedValues = new HashSet<V>();
    private Set<MenuItem<V>> selectedItems = new HashSet<MenuItem<V>>();
    @Override
    protected ValueChangeHandler<Boolean> getSelectionHandler(MenuItem<V> newItem) {
        if (selectionHandler == null) {
            selectionHandler = new ValueChangeHandler<Boolean>() {
                @Override
                @SuppressWarnings({"unchecked"})
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    MenuItem<V> item = (MenuItem) event.getSource();
                    update(item, event.getValue());
                    fireEvent(new MenuItemSelection<V>(item, event.getValue()));
                }
            };
        }
        return selectionHandler;
    }

    /**
     * Обновляет множества добавленных элементов
     */
    private void update(MenuItem<V> item, boolean value) {
        if (value) {
            selectedItems.add(item);
            selectedValues.add(item.getUserValue());
        } else {
            selectedItems.remove(item);
            selectedValues.remove(item.getUserValue());
        }
    }

    @Override
    public void selectItem(MenuItem<V> item, boolean value, boolean fireEvents) {
        boolean changed = item.getValue() != value;
        item.setValue(value, false);
        update(item, value);
        if (changed && fireEvents) {
            fireEvent(new MenuItemSelection<V>(item, value));
        }
    }

    @Override
    public V getSelectedValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MenuItem<V> getSelectedItem() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return выбранные значения
     */
    public Set<V> getSelectedValues() {
        return selectedValues;
    }

    /**
     * @return выбранные элементы
     */
    public Set<MenuItem<V>> getSelectedItems() {
        return selectedItems;
    }
}
