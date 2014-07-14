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
    protected Type type;

    protected SimpleButton() {
        super();
        init();
    }

    /**
     * Кпопка простая с текстом
     * @param text  текст кнопки
     */
    public SimpleButton(String text) {
        super(text);
        init();
    }

    /**
     * Кнопка с иконкой
     * @param text  текст кнопки
     * @param iconResource  иконка кнопки
     */
    public SimpleButton(String text, ImageResource iconResource) {
        super(text, iconResource);
        init();
    }

    /**
     * Кнопка с иконкой
     * @param text  текст кнопки
     * @param iconResource  иконка кнопки
     * @param position положение иконки (слева или справа)
     */

    public SimpleButton(String text, ImageResource iconResource, IconPosition position) {
        super(text, iconResource, position);
        init();
    }

    public SimpleButton(String text, Type type) {
        super(text);
        this.type = type;
        init();
    }

    protected void init() {
        if (type == null) {
            type = Type.REGULAR;
        }
        switch (type) {
            case APPROVE:
                regularButton();
                break;
            case DECLINE:
                declineButton();
                break;
            default:
                approveButton();
        }
    }

    protected void approveButton() {
        setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
    }

    protected void declineButton() {
        setStyleName(SynergyComponents.resources.cssComponents().declineButton());
    }

    protected void regularButton() {
        setStyleName(SynergyComponents.resources.cssComponents().approveButton());
    }

    public enum Type {
        APPROVE,
        DECLINE,
        REGULAR;
    }
}
