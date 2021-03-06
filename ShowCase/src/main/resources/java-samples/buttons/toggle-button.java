import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.button.SimpleToggleButton;

public class Sample {
    public static void main(String[] args) {
        final SimpleToggleButton toggleButton = new SimpleToggleButton(Messages.i18n().tr("Кнопка с длинным текстом"));

        final SimpleToggleButton toggleButton1 = new SimpleToggleButton(Messages.i18n().tr("Не нажата"));
        toggleButton1.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (toggleButton1.isPressed()) {
                    toggleButton1.setText(Messages.i18n().tr("Нажата"));
                } else {
                    toggleButton1.setText(Messages.i18n().tr("Не нажата"));
                }
            }
        });
    }
}
