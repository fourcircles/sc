import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.checkbox.ArtaCheckBox;
import kz.arta.synergy.components.client.checkbox.ArtaRadioButton;

public class Sample {
    public static void main(String[] args) {
        final ArtaRadioButton radioButton1 = new ArtaRadioButton("showCaseRadio");
        final ArtaRadioButton radioButton2 = new ArtaRadioButton("showCaseRadio");
        final ArtaRadioButton radioButton3 = new ArtaRadioButton("showCaseRadio");
        final ArtaRadioButton radioButton4 = new ArtaRadioButton("showCaseRadio");
        // третий radioButton неактивен
        radioButton4.setEnabled(false);

        radioButton1.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                System.out.println("Новое значение: " + event.getValue());
            }
        })
    }
}

