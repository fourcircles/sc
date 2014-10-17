import kz.arta.synergy.components.client.input.date.ArtaDatePicker;
import kz.arta.synergy.components.client.theme.ColorType;

public class Sample {
    public static void main(String[] args) {
        // по умолчание режима выбора - день
        ArtaDatePicker datePicker = new ArtaDatePicker();

        ArtaDatePicker datePickerDark = new ArtaDatePicker(ColorType.BLACK);
    }
}