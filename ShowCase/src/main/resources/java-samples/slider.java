import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.Indicator;
import kz.arta.synergy.components.client.ProgressBar;
import kz.arta.synergy.components.client.Slider;

public class Sample {
    public static void main(String[] args) {

        // true -- зеленый
        final Slider slider = new Slider(true);

        final Indicator indicator = new Indicator(" ");

        // true -- зеленый
        final ProgressBar progressBar = new ProgressBar(true);

        slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                indicator.setText(Integer.toString((int) (event.getValue() * 100)));
            }
        });
        slider.addCircleMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                progressBar.setValue(slider.getValue());
            }
        });

        slider.setOptionalValue(0.5);
        slider.setValue(0.66);

        progressBar.setOptionalValue(0.5);
        progressBar.setOptionalValue(0.66);
    }
}