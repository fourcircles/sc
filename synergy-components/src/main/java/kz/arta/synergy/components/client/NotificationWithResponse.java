package kz.arta.synergy.components.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.client.util.Br;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 05.11.14
 * Time: 12:15
 *
 * Уведомление с кнопками
 * Предполагается, что две кнопки будут всегда. Кнопка "отмена" опциональна.
 */
public class NotificationWithResponse extends Notification {
    static final int BUTTON_WIDTH = 111;

    /**
     * Есть ли кнопка "отмена"
     */
    private final boolean hasCancel;
    /**
     * Кнопка "да"
     */
    SimpleButton yesButton;
    /**
     * Кнопка "нет"
     */
    private SimpleButton noButton;
    /**
     * Кнопка "отмена"
     */
    private SimpleButton cancelButton;

    /**
     * @param text текст уведомления
     * @param type тип
     * @param hasCancel есть ли кнопка "отмена"
     */
    public NotificationWithResponse(String text, Type type, boolean hasCancel) {
        super(text, type);
        popup.setModal(true);

        this.hasCancel = hasCancel;

        root.add((Br) GWT.create(Br.class));
        yesButton = new SimpleButton(Messages.i18n().tr("Да"), SimpleButton.Type.APPROVE);
        root.add(yesButton);
        noButton = new SimpleButton(Messages.i18n().tr("Нет"), SimpleButton.Type.DECLINE);
        root.add(noButton);
        if (hasCancel) {
            cancelButton = new SimpleButton(Messages.i18n().tr("Отмена"));
            root.add(cancelButton);
        }
    }

    /**
     * Центрирует кнопки или текст с иконкой (в зависимости от того, что шире)
     *
     * @see {@link Notification#align()}
     */
    @Override
    protected void align() {
        super.align();

        int buttonsCount = 2;
        if (hasCancel) {
            buttonsCount++;
        }
        int popupWidth = popup.getOffsetWidth();

        int contentWidth = popupWidth - 2 * Constants.BORDER_WIDTH - Constants.NOTIFICATION_OFFSET * 2;

        int buttonsWidth = BUTTON_WIDTH * buttonsCount + Constants.NOTIFICATION_OFFSET * (buttonsCount - 1);
        if (buttonsWidth < contentWidth) {
            double delta = ((double) contentWidth - buttonsWidth) / 2;
            yesButton.getElement().getStyle().setMarginLeft(delta, Style.Unit.PX);
        }

        int topWidth = contentContainer.getOffsetWidth();
        if (hasIcon) {
            topWidth += Constants.NOTIFICATION_ICON_SIZE + Constants.NOTIFICATION_OFFSET;
        }
        if (topWidth < contentWidth) {
            double delta = ((double) contentWidth - topWidth) / 2;
            if (hasIcon) {
                icon.getElement().getStyle().setMarginLeft(delta, Style.Unit.PX);
            } else {
                contentContainer.getElement().getStyle().setMarginLeft(delta, Style.Unit.PX);
            }
        }
    }

    /**
     * Добавляет clickhandler на кнопку согласия
     *
     * @param handler хэндлер
     * @return регистрация хэндлера
     */
    public HandlerRegistration addYesClickHandler(ClickHandler handler) {
        return yesButton.addClickHandler(handler);
    }

    /**
     * Добавляет clickhandler на красную кнопку
     *
     * @param handler хэндлер
     * @return регистрация хэндлера
     */
    public HandlerRegistration addNoClickHandler(ClickHandler handler) {
        return noButton.addClickHandler(handler);
    }

    /**
     * Добавляет clickhandler на кнопку "отмена"
     *
     * @param handler хэндлер
     * @return регистрация хэндлера
     */
    @SuppressWarnings("UnusedDeclaration")
    public HandlerRegistration addCancelHandler(ClickHandler handler) {
        if (!hasCancel) {
            throw new IllegalStateException("Кнопки отмена нет");
        }
        return cancelButton.addClickHandler(handler);
    }
}
