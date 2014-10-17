import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.input.date.DateInput;
import kz.arta.synergy.components.client.input.date.TimeInput;

import java.util.Date;

public class Sample {
    public static void main(String[] args) {
        DateInput dateInputNoEmpty = new DateInput();
        dateInputNoEmpty.setTitle(SCMessages.i18n().tr("Обязательное поле ввода"));

        DateInput dateInput = new DateInput(true);
        dateInput.setTitle(SCMessages.i18n().tr("Необязательное поле ввода"));
        secondRowPanel.add(dateInput);

        // задаем значение
        dateInput.setDate(new Date());

        DateInput disabledDateInput = new DateInput();
        disabledDateInput.setTitle(SCMessages.i18n().tr("Неактивное поле ввода"));
        disabledDateInput.setEnabled(false);
    }
}
