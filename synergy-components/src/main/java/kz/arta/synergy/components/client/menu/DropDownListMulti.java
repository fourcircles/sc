package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;

/**
 * User: vsl
 * Date: 21.10.14
 * Time: 11:06
 *
 * Выпадающий список с возможностью выбора нескольких значений
 */
public class DropDownListMulti<V> extends DropDownList<V> {

    @Override
    protected ValueChangeHandler<Boolean> getSelectionHandler(MenuItem<V> newItem) {
        if (selectionHandler == null) {
            selectionHandler = new ValueChangeHandler<Boolean>() {
                @Override
                @SuppressWarnings({"unchecked"})
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    selectItem((MenuItem) event.getSource(), event.getValue(), true);
                }
            };
        }
        return selectionHandler;
    }

    @Override
    public void selectItem(MenuItem<V> item, boolean value, boolean fireEvents) {
        boolean changed = item.getValue() != value;
        item.setValue(value, false);
        if (changed && fireEvents) {
            fireEvent(new MenuItemSelection<V>(item, value));
        }
    }
}
