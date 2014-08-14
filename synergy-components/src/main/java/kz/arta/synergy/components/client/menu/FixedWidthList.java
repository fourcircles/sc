package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: user
 * Date: 06.08.14
 * Time: 14:51
 * Выпадающий список с фиксированной шириной
 */
public class FixedWidthList<V> extends DropDownList<V> {

    public FixedWidthList(Widget relativeWidget) {
        super(relativeWidget, null);
    }

    public FixedWidthList(Widget relativeWidget, EventBus bus) {
        super(relativeWidget, bus);
    }

    public void setWidth(String width) {
        root.setWidth(width);
        popup.setWidth(width);
    }

    public void show() {
        popup.setHeight(getHeight() + "px");

        super.showUnderParent();
    }
}
