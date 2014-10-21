import kz.arta.synergy.components.client.input.tags.MultiComboBox;

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

