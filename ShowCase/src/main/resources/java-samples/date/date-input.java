import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.input.date.DateInput;

import java.util.Date;

public class Sample {
    public static void main(String[] args) {
        DateInput dateInputNoEmpty = new DateInput();
        dateInputNoEmpty.setTitle(Messages.i18n().tr("Обязательное поле ввода"));

        DateInput dateInput = new DateInput(true);
        dateInput.setTitle(Messages.i18n().tr("Необязательное поле ввода"));
        secondRowPanel.add(dateInput);

        // задаем значение
        dateInput.setDate(new Date());

        DateInput disabledDateInput = new DateInput();
        disabledDateInput.setTitle(Messages.i18n().tr("Неактивное поле ввода"));
        disabledDateInput.setEnabled(false);
    }
}
