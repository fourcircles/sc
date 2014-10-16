import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.input.number.NumberInput;

public class Sample {
    public static void main(String[] args) {
        //по умолчанию числовое поле принимает только цифры
        NumberInput integersInput = new NumberInput();
        integersInput.setPlaceHolder(SCMessages.i18n().tr("Только цифры"));
    }
}

