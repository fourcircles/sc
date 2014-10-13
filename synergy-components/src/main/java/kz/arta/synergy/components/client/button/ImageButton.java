package kz.arta.synergy.components.client.button;

import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: user
 * Date: 30.06.14
 * Time: 16:33
 * Кнопка с пиктограммой без текста
 */
public class ImageButton extends ButtonBase {

    /**
     * Кнопка с пиктограммой без текста
     * @param iconResource  иконка кнопки
     */
    public ImageButton(ImageResource iconResource) {
        super(iconResource);
        init();
    }

    protected void init() {
        setStyleName(SynergyComponents.getResources().cssComponents().buttonSimple());
        addStyleName(SynergyComponents.getResources().cssComponents().unselectable());
        icon.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
        icon.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
    }
}
