package kz.arta.synergy.components.client.msg;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import kz.arta.synergy.components.client.Notification;

/**
 * User: user
 * Date: 19.11.14
 * Time: 15:37
 * Класс для упрощенного отображения уведомлений
 */
public class UserMessage {

    private static Notification notification;

    public static void showMessage(String message, Notification.Type type) {
        notification = new Notification(message, type);
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

        new Timer() {
            @Override
            public void run() {
                notification.hide();
            }
        }.schedule(3000);
    }
}
