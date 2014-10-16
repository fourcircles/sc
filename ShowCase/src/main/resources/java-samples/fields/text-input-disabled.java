import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.input.TextInput;

public class Sample {
    public static void main(String[] args) {
        final TextInput textInput = new TextInput();
        textInput.setPlaceHolder(SCMessages.i18n().tr("Неактивное поле"));

        // отключение
        textInput.setEnabled(false);

        textInput.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                // значение изменилось
            }
        })
    }
}

