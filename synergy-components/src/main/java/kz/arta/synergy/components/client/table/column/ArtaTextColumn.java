package kz.arta.synergy.components.client.table.column;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: vsl
 * Date: 02.09.14
 * Time: 14:05
 *
 * Столбец, в ячейках находится только текст.
 */
public abstract class ArtaTextColumn<T> extends AbstractArtaColumn<T, String> {

    protected ArtaTextColumn(String headerText) {
        super(headerText);
    }

    public abstract String getValue(T object);

    @Override
    public void updateWidget(Widget widget, T object) {
        ((Label) widget).setText(getValue(object));
    }

    @Override
    public Label createWidget(T object, EventBus bus) {
        Label label = new Label(getValue(object));
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            label.getElement().getStyle().setPaddingRight(14, Style.Unit.PX);
        } else {
            label.getElement().getStyle().setPaddingLeft(14, Style.Unit.PX);
        }
        label.setStyleName("");
        return label;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public int getMinWidth() {
        return 60;
    }
}
