package kz.arta.synergy.components.client.table.column;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;

/**
 * User: vsl
 * Date: 07.10.14
 * Time: 17:44
 *
 * Столбец для отображения элементов дерева объекта
 */
public abstract class TreeColumn<T extends TreeTableItem<T>> extends AbstractArtaColumn<T> {

    public TreeColumn(String headerText) {
        super(headerText);
        isSortable = false;
    }

    @Override
    public Widget createWidget(T object, EventBus bus) {
        return new TreeTableWidget<T>(object, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateWidget(Widget widget, T object) {
        TreeTableWidget<T> treeWidget = (TreeTableWidget<T>) widget;
        treeWidget.update(object);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public int getMinWidth() {
        return 60;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    public abstract String getText(T object);
}
