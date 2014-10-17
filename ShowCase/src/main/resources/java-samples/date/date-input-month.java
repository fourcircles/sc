import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.input.date.ArtaDatePicker;
import kz.arta.synergy.components.client.input.date.DateInput;

public class Sample {
    public static void main(String[] args) {
        DateInput dateInputWeek = new DateInput(ArtaDatePicker.CalendarMode.MONTH);
        dateInputWeek.setTitle(SCMessages.i18n().tr("Неделя"));
    }
}