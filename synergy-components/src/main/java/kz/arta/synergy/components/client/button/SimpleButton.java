package kz.arta.synergy.components.client.button;

import com.google.gwt.resources.client.ImageResource;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: user
 * Date: 23.06.14
 * Time: 11:11
 * Кнопка простая
 */
public class SimpleButton extends ButtonBase {

    public SimpleButton() {
        super();
        init();
    }

    /**
     * Кпопка простая с текстом
     * @param text  текст кнопки
     */
    public SimpleButton(String text) {
        super();
        this.text = text;
        init();
    }

    /**
     * Кнопка с иконкой
     * @param text  текст кнопки
     * @param iconResource  иконка кнопки
     */
    public SimpleButton(String text, ImageResource iconResource) {
        super();
        this.text = text;
        this.iconResource = iconResource;
        init();
    }

    /**
     * Кнопка с иконкой
     * @param text  текст кнопки
     * @param iconResource  иконка кнопки
     * @param placement положение иконки (слева или справа)
     */

    public SimpleButton(String text, ImageResource iconResource, IconPosition placement) {
        super();
        this.text = text;
        this.iconResource = iconResource;
        iconPosition = placement;
        init();
    }

    protected void init() {
        super.init();

        setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
    }



}
