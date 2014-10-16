import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.button.GroupButtonPanel;
import kz.arta.synergy.components.client.button.SimpleToggleButton;

public class Sample {
    public static void main(String[] args) {
        GroupButtonPanel groupButtonPanel = new GroupButtonPanel(true);
        groupButtonPanel.addButton(SCMessages.i18n().tr("Первая кнопка длинная"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            }
        });
        groupButtonPanel.addButton(SCMessages.i18n().tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (((SimpleToggleButton) event.getSource()).isPressed()) {
                    ((SimpleToggleButton) event.getSource()).setText(SCMessages.i18n().tr("Вторая нажата"));
                } else {
                    ((SimpleToggleButton) event.getSource()).setText(SCMessages.i18n().tr("Вторая"));
                }
            }
        });
        groupButtonPanel.addButton(SCMessages.i18n().tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (((SimpleToggleButton) event.getSource()).isPressed()) {
                    ((SimpleToggleButton) event.getSource()).setText(SCMessages.i18n().tr("Третья нажата"));
                } else {
                    ((SimpleToggleButton) event.getSource()).setText(SCMessages.i18n().tr("Третья"));
                }
            }
        });
        groupButtonPanel.buildPanel();
    }
}