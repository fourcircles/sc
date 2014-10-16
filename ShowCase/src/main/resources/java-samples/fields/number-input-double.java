import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.input.number.DoubleConstraint;
import kz.arta.synergy.components.client.input.number.InputConstraint;
import kz.arta.synergy.components.client.input.number.NumberInput;

import java.util.ArrayList;
import java.util.List;

public class Sample {
    public static void main(String[] args) {
        List<InputConstraint> constraintsForDouble = new ArrayList<InputConstraint>();
        constraintsForDouble.add(DoubleConstraint.getInstance());

        NumberInput doublesInput = new NumberInput(constraintsForDouble);
        doublesInput.setPlaceHolder(SCMessages.i18n().tr("Дробные"));
    }
}

