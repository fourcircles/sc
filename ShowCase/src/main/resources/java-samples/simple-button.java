import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.synergy.components.client.button.SimpleButton;

public class Sample {
    public static void main(String[] args) {
        SimpleButton simpleButton = new SimpleButton("Простая кнопка");
        simpleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // действие при клике
            }
        })
    }
}
