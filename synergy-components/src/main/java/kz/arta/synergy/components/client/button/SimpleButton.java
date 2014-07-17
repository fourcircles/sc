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
    protected BorderType borderType;

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
        if (type == null) {
            type = Type.REGULAR;
        }
        if (borderType == null) {
            borderType = BorderType.ALL;
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
        REGULAR;
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

