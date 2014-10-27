import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.button.SimpleButton;

public class Sample {
    public static void main(String[] args) {
        SimpleButton simpleButton = new SimpleButton("Кнопка с кликом");
        simpleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(Messages.i18n().tr("Кнопка была нажата!"));
            }
        })
    }
}
