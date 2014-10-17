import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.ComboBox;
import kz.arta.synergy.components.client.resources.ImageResources;

public class Sample {
    public static void main(String[] args) {

        final ComboBox<String> combo = new ComboBox<String>();
        fillCombobox(combo);
        // изменение состояние комбобокса
        combo.setReadOnly(false);

        // комбобокс изменяет состояния главного комбобокса
        ComboBox<Integer> statesCombo = new ComboBox<Integer>();
        comboBoxPanel.add(statesCombo);

        // в него нельзя вводить значения для поиска
        statesCombo.setReadOnly(true);

        statesCombo.addItem(SCMessages.i18n().tr("Включен, изменяем"), 1);
        statesCombo.addItem(SCMessages.i18n().tr("Включен, неизменяем"), 2);
        statesCombo.addItem(SCMessages.i18n().tr("Выключен"), 3);

        // по умолчанию главный комбобокс включен и можно вводить значения для поиска
        statesCombo.selectValue(1, false);

        // добавление хэндлера для выбора нового значения
        statesCombo.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                // также можно использовать statesCombo.getSelectedValue(), но так лучше
                switch (event.getValue()) {
                    case 1:
                        combo.setEnabled(true);
                        combo.setReadOnly(false);
                        break;
                    case 2:
                        combo.setEnabled(true);
                        combo.setReadOnly(true);
                        break;
                    case 3:
                        combo.setEnabled(false);
                        break;
                    default:
                        // ничего не происходит
                }
            }
        });
    }

    /**
     * Заполнение комбобокса
     */
    private void fillCombobox(ComboBox<String> comboBox) {
        combobox.addItem(SCMessages.i18n().tr("Приблизить"), ImageResources.IMPL.zoom(), SCMessages.i18n().tr("Приблизить"));
        combobox.addItem(SCMessages.i18n().tr("Налево"), ImageResources.IMPL.navigationLeft(), SCMessages.i18n().tr("Налево"));
        combobox.addItem(SCMessages.i18n().tr("Направо"), ImageResources.IMPL.navigationRight(), SCMessages.i18n().tr("Направо"));
        for (int i = 1; i < 30; i++) {
            combobox.addItem(SCMessages.i18n().tr("Пункт меню ") + i, SCMessages.i18n().tr("Пункт меню ") + i);
        }
    }
}
