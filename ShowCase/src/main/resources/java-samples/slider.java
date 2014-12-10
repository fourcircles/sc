import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.Indicator;
import kz.arta.synergy.components.client.Slider;
import kz.arta.synergy.components.client.progressbar.ProgressBar;

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
                progressBar.setValue(slider.getValue());
                indicator.setText(Integer.toString((int) (slider.getValue() * 100)));
            }
        });
        slider.addCircleMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                indicator.setText(Integer.toString((int) (slider.getValue() * 100)));
            }
        });

        slider.setOptionalValue(0.5);
        slider.setValue(0.66);

        progressBar.setOptionalValue(0.5);
        progressBar.setOptionalValue(0.66);
    }
}