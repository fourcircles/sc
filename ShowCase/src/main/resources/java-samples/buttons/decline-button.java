import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.button.SimpleButton;

public class Sample {
    public static void main(String[] args) {
        SimpleButton colorButton = new SimpleButton((SCMessages.i18n().tr("Создать")), SimpleButton.Type.DECLINE);
        simpleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // действие при клике
            }
        })
    }
}