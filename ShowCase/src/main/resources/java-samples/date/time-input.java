import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.input.date.TimeInput;

public class Sample {
    public static void main(String[] args) {
        TimeInput timeInputNotAllowEmpty = new TimeInput();
        timeInputNotAllowEmpty.setTitle(SCMessages.i18n().tr("Обязательное поле ввода"));

        timeInputNotAllowEmpty.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                // значение изменилось
            }
        });

        TimeInput timeInputAllowEmpty = new TimeInput(true);
        timeInputAllowEmpty.setTitle(SCMessages.i18n().tr("Необязательное поле ввода"));

        // задаем значение
        timeInputAllowEmpty.setTime(12, 12);

        TimeInput timeInput = new TimeInput();
        timeInput.setTitle(SCMessages.i18n().tr("Неактивное поле ввода"));
        timeInput.setEnabled(false);

    }
}
