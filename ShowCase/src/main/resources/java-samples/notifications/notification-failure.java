import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.Notification;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.checkbox.ArtaCheckBox;

import java.util.Arrays;

public class Sample {
    public static void main(String[] args) {
        ArtaCheckBox checkBox = new ArtaCheckBox();
        checkBox.setValue(true, false);

        final SimpleButton failure = new SimpleButton(Messages.i18n().tr("Ошибка"));

        final Notification failureNotification = new Notification(Messages.i18n().tr("Ошибка"),
                Arrays.asList(Messages.i18n().tr("Первая ошибка в уведомлении"),
                        Messages.i18n().tr("Вторая ошибка в уведомлении"),
                        Messages.i18n().tr("Ошибка в классе kz.arta.synergy.components.client.delayInput.date.repeat.MonthlyRepeatChooser")),
                Notification.Type.FAILURE);
        failure.addClickHandler(createClickHandlerForNotification(failureNotification, hideCheckbox));
    }

    private ClickHandler createClickHandlerForNotification(final Notification notification, final ArtaCheckBox checkBox) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (notification.isShowing()) {
                    notification.hide();
                } else {
                    notification.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = (Window.getClientWidth() - offsetWidth) / 2;
                            notification.setPopupPosition(left, 40);
                        }
                    });
                }

                if (checkBox.getValue()) {
                    new Timer() {
                        @Override
                        public void run() {
                            notification.hide();
                        }
                    }.schedule(notificationsDelay);
                }
            }
        };
    }

}