import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.button.GroupButtonPanel;

public class Sample {
    public static void main(String[] args) {
        GroupButtonPanel groupButtonPanel4 = new GroupButtonPanel();
        groupButtonPanel4.addButton(Messages.i18n().tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.addButton(Messages.i18n().tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.addButton(Messages.i18n().tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
    }
}