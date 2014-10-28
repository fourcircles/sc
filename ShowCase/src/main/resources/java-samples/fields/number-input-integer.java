import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.input.number.NumberInput;

public class Sample {
    public static void main(String[] args) {
        //по умолчанию числовое поле принимает только цифры
        NumberInput integersInput = new NumberInput();
        integersInput.setPlaceHolder(Messages.i18n().tr("Только цифры"));
    }
}

