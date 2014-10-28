import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.button.SimpleButton;

public class Sample {
    public static void main(String[] args) {
        SimpleButton colorButton = new SimpleButton((Messages.i18n().tr("Создать")), SimpleButton.Type.APPROVE);
        simpleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // действие при клике
            }
        })
    }
}
