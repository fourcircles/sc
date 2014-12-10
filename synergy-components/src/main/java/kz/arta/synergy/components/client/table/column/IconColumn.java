package kz.arta.synergy.components.client.table.column;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 27.11.14
 * Time: 15:54
 *
 * Столбец с иконкой
 */
public abstract class IconColumn<T> extends AbstractArtaColumn<T> {

    public IconColumn(String headerText) {
        super(headerText);
    }

    @Override
    public Widget createWidget(T object, EventBus bus) {
        Image image = new Image();
        updateWidget(image, object);
        image.getElement().getStyle().setMarginLeft(5, Style.Unit.PX);
        image.getElement().getStyle().setMarginTop(5, Style.Unit.PX);
        image.getElement().getStyle().setWidth(Constants.STD_ICON_WIDTH, Style.Unit.PX);
        image.getElement().getStyle().setHeight(Constants.STD_ICON_WIDTH, Style.Unit.PX);
        return image;
    }

    @Override
    public void updateWidget(Widget widget, T object) {
        Image image = (Image) widget;
        ImageResource icon = getImage(object);
        if (icon == null) {
            image.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        } else {
            image.setResource(getImage(object));
        }
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public int getMinWidth() {
        return Constants.STD_ICON_WIDTH + 5 * 2;
    }

    /**
     * Пользователь картинка для объекта
     * @param object объект
     * @return картинка
     */
    public abstract ImageResource getImage(T object);
}
