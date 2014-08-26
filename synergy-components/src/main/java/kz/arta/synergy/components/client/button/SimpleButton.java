package kz.arta.synergy.components.client.button;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasText;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: user
 * Date: 23.06.14
 * Time: 11:11
 * Кнопка простая
 */
public class SimpleButton extends ButtonBase implements HasText {
    protected Type type;
    protected BorderType borderType;

    protected SimpleButton() {
        super();
        init();
    }

    public SimpleButton(String text, ImageResource iconResource, boolean enabled) {
        super(text, iconResource);
        setEnabled(enabled);
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

    /**
     * Кнопка с текстом и указанием типа
     * @param text текст кнопки
     * @param type тип кнопки
     */
    public SimpleButton(String text, Type type) {
        super(text);
        this.type = type;
        init();
    }

    /**
     * Кнопка простая с текстом и указанием типа границы
     * @param text  текст кнопки
     * @param borderType  тип границы
     */
    public SimpleButton(String text, BorderType borderType) {
        super(text);
        this.borderType = borderType;
        init();
    }

    protected void init() {
        setType(type);

        if (borderType == null) {
            borderType = BorderType.ALL;
        }
        switch (borderType) {
            case EDGE:
                addStyleName(SynergyComponents.resources.cssComponents().edge());
                break;
            case LEFT:
                addStyleName(SynergyComponents.resources.cssComponents().left());
                break;
            case RIGHT:
                addStyleName(SynergyComponents.resources.cssComponents().right());
                break;
            default:
                break;
        }
        addStyleName(SynergyComponents.resources.cssComponents().unselectable());
        setFontStyle(SynergyComponents.resources.cssComponents().mainTextBold());
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if (type == null) {
            type = Type.REGULAR;
        }

        this.type = type;
        switch (type) {
            case APPROVE:
                approveButton();
                break;
            case DECLINE:
                declineButton();
                break;
            default:
                regularButton();
        }
    }

    protected void approveButton() {
        removeStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
        removeStyleName(SynergyComponents.resources.cssComponents().declineButton());
        addStyleName(SynergyComponents.resources.cssComponents().approveButton());
    }

    protected void declineButton() {
        removeStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
        removeStyleName(SynergyComponents.resources.cssComponents().approveButton());
        addStyleName(SynergyComponents.resources.cssComponents().declineButton());
    }

    protected void regularButton() {
        removeStyleName(SynergyComponents.resources.cssComponents().approveButton());
        removeStyleName(SynergyComponents.resources.cssComponents().declineButton());
        addStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
    }

    /**
     * Тип кнопки
     */
    public enum Type {
        /**
         * зеленая кнопка "approve"
         */
        APPROVE,
        /**
         * красная кнопка "decline"
         */
        DECLINE,
        /**
         * обычная кнопка
         */
        REGULAR
    }

    /**
     * Тип границ кнопки
     */
    public enum BorderType {
        /**
         *  Границы не закруглены
         */
        EDGE,
        /**
         * Только правая граница закруглена
         */
        RIGHT,
        /**
         * Только левая граница закруглена
         */
        LEFT,
        /**
         * Все  границы закруглены
         */
        ALL
    }
}

