package kz.arta.synergy.components.client.table.column;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.progressbar.ProgressBar;

/**
 * User: vsl
 * Date: 27.11.14
 * Time: 16:56
 *
 * Столбец с прогресс-баром.
 * Изменение значения пользователем не реализовано (слайдер). Только отображение.
 */
public abstract class ProgressColumn<T> extends AbstractArtaColumn<T> {
    public static final int WIDTH = 115;

    public ProgressColumn(String headerText) {
        super(headerText);
        locked = true;
    }

    @Override
    public Widget createWidget(T object, EventBus bus) {
        ProgressBar bar = new ProgressBar(getType(object));

        bar.getElement().getStyle().setLineHeight(19, Style.Unit.PX);
        bar.getElement().getStyle().setWidth(WIDTH, Style.Unit.PX);
        bar.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        bar.getElement().getStyle().setProperty("marginLeft", "auto");
        bar.getElement().getStyle().setProperty("marginRight", "auto");

        bar.setValue(getValue(object));
        return bar;
    }

    @Override
    public void updateWidget(Widget widget, T object) {
        ProgressBar bar = (ProgressBar) widget;
        bar.setValue(getValue(object));
        bar.setOptionalValue(getOptionalValue(object));
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public int getMinWidth() {
        return WIDTH;
    }

    /**
     * Значение для прогресс-бара
     *
     * @param object объект
     * @return значение прогресс-бара
     */
    public abstract double getValue(T object);

    /**
     * Тип прогресс бара. {@link kz.arta.synergy.components.client.progressbar.ProgressBar#ProgressBar(boolean)}
     *
     * @param object объект
     * @return тип
     */
    public abstract boolean getType(T object);

    /**
     * Опциональное значение объекта
     *
     * @param object объект
     * @return опциональное значение
     */
    public abstract double getOptionalValue(T object);
}
