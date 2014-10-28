import kz.arta.synergy.components.client.input.date.ArtaDatePicker;
import kz.arta.synergy.components.client.theme.ColorType;

public class Sample {
    public static void main(String[] args) {
        /*светлый календарь*/
        ArtaDatePicker datePicker = new ArtaDatePicker(ArtaDatePicker.CalendarMode.WEEK);

        /*темный календарь*/
        ArtaDatePicker datePickerDark = new ArtaDatePicker(ArtaDatePicker.CalendarMode.WEEK, ColorType.BLACK);
    }
}