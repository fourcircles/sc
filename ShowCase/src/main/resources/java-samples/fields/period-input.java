import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.checkbox.ArtaCheckBox;
import kz.arta.synergy.components.client.input.date.repeat.FullRepeatChooser;
import kz.arta.synergy.components.client.input.date.repeat.RepeatChooser;
import kz.arta.synergy.components.client.input.date.repeat.RepeatDate;

public class Sample {
    public static void main(String[] args) {
        // чекбокс отключает-включает выбор периода
        ArtaCheckBox box = new ArtaCheckBox();
        box.setValue(true, false);
        root.add(box);

        final FullRepeatChooser chooser = new FullRepeatChooser();

        chooser.setMode(RepeatChooser.MODE.YEAR);
        chooser.getChooser().addSelected(new RepeatDate(22, 2, RepeatChooser.MODE.YEAR));

        box.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                chooser.setEnabled(event.getValue());
            }
        });

        for (RepeatDate date : chooser.getChooser().getSelected()) {
            System.out.println(date.toString());
        }
    }
}