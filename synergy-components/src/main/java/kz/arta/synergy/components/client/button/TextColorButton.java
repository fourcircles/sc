package kz.arta.synergy.components.client.button;

import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: user
 * Date: 30.06.14
 * Time: 17:38
 * Кнопка зеленая/красная
 */
public class TextColorButton extends ButtonBase {

    /**
     * Зеленая кнопка создания/подтверджения/заврешения
     */
    public static final int APPROVE_BUTTON = 0;

    /**
     * Красная кнопка отклонения/удаления
     */
    public static final int DECLINE_BUTTON = 1;

    /**
     * тип кнопки
     */
    private int type = APPROVE_BUTTON;

    /**
     * Кпопка цветная с текстом
     * @param text  текст
     * @param type  тип
     *              @see kz.arta.synergy.components.client.button.TextColorButton#APPROVE_BUTTON
     *              @see kz.arta.synergy.components.client.button.TextColorButton#DECLINE_BUTTON
     */
    public TextColorButton(String text, int type) {
        super();
        this.text = text;
        this.type = type;
        init();
    }

    public void init() {
        super.init();
        if (type == APPROVE_BUTTON) {
            setStyleName(SynergyComponents.resources.cssComponents().approveButton());
            gradient.setStyleName(SynergyComponents.resources.cssComponents().approveButtonGradient());
        } else {
            setStyleName(SynergyComponents.resources.cssComponents().declineButton());
            gradient.setStyleName(SynergyComponents.resources.cssComponents().declineButtonGradient());
        }

    }

}