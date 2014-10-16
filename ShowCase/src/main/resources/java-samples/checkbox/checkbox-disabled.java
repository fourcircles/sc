import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.checkbox.ArtaCheckBox;

public class Sample {
    public static void main(String[] args) {
        final ArtaCheckBox checkBox = new ArtaCheckBox();
        checkBox.setValue(true, false);
        checkBox.setEnabled(false);

        checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                System.out.println("Новое значение: " + event.getValue());
            }
        });
    }
}
