import kz.arta.synergy.components.client.input.date.ArtaDatePicker;
import kz.arta.synergy.components.client.theme.ColorType;

public class Sample {
    public static void main(String[] args) {
        // по умолчание режима выбора - день
        /*светлый календарь*/
        ArtaDatePicker datePicker = new ArtaDatePicker();

        /*темный календарь*/
        ArtaDatePicker datePickerDark = new ArtaDatePicker(ColorType.BLACK);
    }
}