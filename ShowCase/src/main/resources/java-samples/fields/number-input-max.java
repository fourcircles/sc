import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.input.number.InputConstraint;
import kz.arta.synergy.components.client.input.number.MaxNumberConstraint;
import kz.arta.synergy.components.client.input.number.NumberInput;
import kz.arta.synergy.components.client.input.number.OnlyDigitsConstraint;

import java.util.ArrayList;
import java.util.List;

public class Sample {
    public static void main(String[] args) {
        // ограничения
        List<InputConstraint> max200 = new ArrayList<InputConstraint>();
        max200.add(OnlyDigitsConstraint.getInstance());
        max200.add(new MaxNumberConstraint(200));
        NumberInput maxInput = new NumberInput(max200);

        maxInput.setPlaceHolder(Messages.i18n().tr("Максимально 200"));
    }
}

