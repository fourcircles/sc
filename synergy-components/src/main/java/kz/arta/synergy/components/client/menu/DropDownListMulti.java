package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;

/**
 * User: vsl
 * Date: 01.08.14
 * Time: 12:27
 *
 * Выпадающий список с возможностью выделения нескольких пунктов
 */
public class DropDownListMulti<V> extends DropDownList<V>{
    private boolean hideAfterSelect = false;

    public DropDownListMulti(Widget relativeWidget, EventBus bus) {
        super(relativeWidget, bus);
    }

    public class Item extends DropDownList<V>.Item {
        private boolean isSelected = false;

        @Override
        protected void select() {
            setSelected(!isSelected, true);

            if (hideAfterSelect) {
                hide();
            }
        }

        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Для выделения/снятия выделения.
         * Используется, например, при закрытии тега.
         */
        public void setSelected(boolean selected, boolean fireEvents) {
            if (selected) {
                addStyleName(SynergyComponents.resources.cssComponents().selected());
                if (fireEvents) {
                    bus.fireEvent(new ListSelectionEvent<V>(this, ListSelectionEvent.ActionType.SELECT));
                }
            } else {
                removeStyleName(SynergyComponents.resources.cssComponents().selected());
                if (fireEvents) {
                    bus.fireEvent(new ListSelectionEvent<V>(this, ListSelectionEvent.ActionType.DESELECT));
                }
            }
            this.isSelected = selected;
        }
    }

    @Override
    public DropDownListMulti<V>.Item addItem(String text, V value) {
        Item item = new Item();
        item.setText(text);
        item.setValue(value);
        items.add(item);

        addItem(item);
        return item;
    }

    @Override
    public DropDownListMulti<V>.Item addItem(String text, ImageResource icon, V value) {
        Item item = new Item();
        item.setText(text);
        item.setValue(value);
        item.setIcon(icon);
        items.add(item);

        addItem(item);
        return item;
    }

    public boolean isHideAfterSelect() {
        return hideAfterSelect;
    }

    public void setHideAfterSelect(boolean hideAfterSelect) {
        this.hideAfterSelect = hideAfterSelect;
    }
}
