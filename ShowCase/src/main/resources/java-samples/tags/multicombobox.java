import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.synergy.components.client.input.tags.MultiComboBox;
import kz.arta.synergy.components.client.input.tags.TagInput;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.menu.DropDownListMulti;

public class Sample {
    public static void main(String[] args) {
        MultiComboBox<String> multiComboBox = new MultiComboBox<String>();

        String[] comboNames = createShuffledNames();
        for (String name : comboNames) {
            // добавление элемента в комбобокс
            multiComboBox.addItem(name, name);
        }

        // выделение некоторых элементов
        multiComboBox.select(comboNames[0]);
        multiComboBox.select(comboNames[1]);
        multiComboBox.select(comboNames[comboNames.length - 1]);

        multiComboBox.setWidth(300);

        for (String name : multiComboBox.getSelectedValues()) {
            // выбранные значения
            System.out.println(name);
        }
    }
}

