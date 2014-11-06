import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.Notification;
import kz.arta.synergy.components.client.NotificationWithResponse;
import kz.arta.synergy.components.client.button.SimpleButton;

public class Sample {
    public static void main(String[] args) {

        SimpleButton question = new SimpleButton(Messages.i18n().tr("Вопрос"));
        final NotificationWithResponse neutralQuestionNotification = new NotificationWithResponse(
                Messages.i18n().tr("Сохранить внесенные изменения?"),
                Notification.Type.QUESTION,
                false);
        neutralQuestionNotification.addYesClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                neutralQuestionNotification.hide();
            }
        });
        neutralQuestionNotification.addNoClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                neutralQuestionNotification.hide();
            }
        });
        question.addClickHandler(createClickHandlerForNotification(neutralQuestionNotification, false));
        root.add(question);
    }

    private ClickHandler createClickHandlerForNotification(final Notification notification, boolean hide) {
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

                if (hide) {
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