package kz.arta.synergy.components.client.table.column;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: vsl
 * Date: 02.09.14
 * Time: 15:12
 * Столбец, в котором можно изменять значения
 */
public abstract class ArtaEditableTextColumn<T> extends AbstractEditableColumn<T> {

    protected ArtaEditableTextColumn(String headerText) {
        super(headerText);
    }

    @Override
    public EditableText createWidget(T object, EventBus bus) {
        return new EditableText(this, object, bus);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateWidget(Widget widget, T object) {
        super.updateWidget(widget, object);
        EditableText textWidget = (EditableText) widget;
        textWidget.setObject(object);
    }

    @Override
    public int getMinWidth() {
        return 60;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    public void setSortable(boolean isSortable) {
        this.isSortable = isSortable;
    }

}
