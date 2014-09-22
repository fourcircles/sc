package kz.arta.synergy.components.client.table.column;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.table.Header;

/**
 * User: vsl
 * Date: 04.09.14
 * Time: 17:41
 */
public abstract class AbstractArtaColumn<T, C> implements ArtaColumn<T, C> {
    private boolean isSortable;
    private String dataStoreName;

    private Header header;

    protected AbstractArtaColumn(String headerText) {
        header = new Header(headerText);
    }

    public boolean isSortable() {
        return isSortable;
    }

    public void setSortable(boolean isSorted) {
        this.isSortable = isSorted;
    }

    @Override
    public Header getHeader() {
        return header;
    }

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

    public abstract C getValue(T object);
    public abstract Widget createWidget(T object, EventBus bus);
    public abstract void updateWidget(Widget widget, T object);
}
