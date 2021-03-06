import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.input.date.DateTimeInput;

public class Sample {
    public static void main(String[] args) {
        DateTimeInput dateTimeInput = new DateTimeInput();
        dateTimeInput.setTitle(Messages.i18n().tr("Обязательное поле ввода"));
        fourthRowPanel.add(dateTimeInput);

        DateTimeInput dateTimeInput1 = new DateTimeInput(true);
        dateTimeInput1.setTitle(Messages.i18n().tr("Необязательное поле ввода"));
        fourthRowPanel.add(dateTimeInput1);

        DateTimeInput dateTimeInput2 = new DateTimeInput(true);
        dateTimeInput2.setTitle(Messages.i18n().tr("Неактивное поле ввода"));
        dateTimeInput2.setEnabled(false);
        fourthRowPanel.add(dateTimeInput2);
        panel.add(fourthRow);
    }
}