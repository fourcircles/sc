package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.PopupPanel;
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
    private static final int BUTTON_WIDTH = 111;

    /**
     * Есть ли кнопка "отмена"
     */
    private final boolean hasCancel;

    /**
     * Кнопка "да"
     */
    private SimpleButton yesButton;
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

        root.add(new Br());
        yesButton = new SimpleButton(Messages.i18n().tr("Да"), SimpleButton.Type.APPROVE);
        root.add(yesButton);
        noButton = new SimpleButton(Messages.i18n().tr("Нет"), SimpleButton.Type.DECLINE);
        root.add(noButton);
        if (hasCancel) {
            cancelButton = new SimpleButton(Messages.i18n().tr("Отмена"));
            root.add(cancelButton);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setPopupPositionAndShow(PopupPanel.PositionCallback callback) {
        popup.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        popup.center();
        popup.show();
        align();

        int buttonsCount = 2;
        if (hasCancel) {
            buttonsCount++;
        }
        int popupWidth = popup.getOffsetWidth();
        int popupHeight = popup.getOffsetHeight();

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

        callback.setPosition(popupWidth, popupHeight);
        popup.getElement().getStyle().clearVisibility();
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
