import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.input.TextInput;

public class Sample {
    public static void main(String[] args) {
        // по умолчанию разрешается вводить пустое значение
        final TextInput textInput = new TextInput();
        textInput.setPlaceHolder(Messages.i18n().tr("Необязательное поле"));

        textInput.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                // значение изменилось
            }
        })
    }
}

