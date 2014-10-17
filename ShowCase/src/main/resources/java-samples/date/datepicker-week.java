import kz.arta.synergy.components.client.input.date.ArtaDatePicker;
import kz.arta.synergy.components.client.theme.ColorType;

public class Sample {
    public static void main(String[] args) {
        ArtaDatePicker datePicker = new ArtaDatePicker(ArtaDatePicker.CalendarMode.WEEK);

        ArtaDatePicker datePickerDark = new ArtaDatePicker(ArtaDatePicker.CalendarMode.WEEK, ColorType.BLACK);
    }
}