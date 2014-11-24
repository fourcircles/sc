import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.ComboBox;
import kz.arta.synergy.components.client.resources.ImageResources;

public class Sample {
    public static void main(String[] args) {

        final ComboBox<String> combo = new ComboBox<String>();
        fillCombobox(combo);
        // изменение состояние комбобокса
        combo.setReadOnly(false);

        // комбобокс изменяет состояния главного комбобокса
        ComboBox<ComboState> statesCombo = new ComboBox<ComboState>();
        comboBoxPanel.add(statesCombo);

        statesCombo.setReadOnly(true);

        statesCombo.addItem(Messages.i18n().tr("Включен, изменяем"), ComboState.ON);
        statesCombo.addItem(Messages.i18n().tr("Включен, неизменяем"), ComboState.ON_READONLY);
        statesCombo.addItem(Messages.i18n().tr("Выключен"), ComboState.OFF);

        // по умолчанию главный комбобокс включен и можно вводить значения для поиска
        statesCombo.selectValue(ComboState.ON, false);

        // добавление хэндлера для выбора нового значения
        statesCombo.addValueChangeHandler(new ValueChangeHandler<ComboState>() {
            @Override
            public void onValueChange(ValueChangeEvent<ComboState> event) {
                switch(event.getValue()) {
                    case ON:
                        combo.setEnabled(true);
                        combo.setReadOnly(false);
                        break;
                    case ON_READONLY:
                        combo.setEnabled(true);
                        combo.setReadOnly(true);
                        break;
                    case OFF:
                        combo.setEnabled(false);
                        break;
                    default:
                }
            }
        })
    }

    /**
     * Заполнение комбобокса
     */
    private void fillCombobox(ComboBox<String> comboBox) {
        combobox.addItem(Messages.i18n().tr("Приблизить"), ImageResources.IMPL.zoom(), Messages.i18n().tr("Приблизить"));
        combobox.addItem(Messages.i18n().tr("Налево"), ImageResources.IMPL.navigationLeft(), Messages.i18n().tr("Налево"));
        combobox.addItem(Messages.i18n().tr("Направо"), ImageResources.IMPL.navigationRight(), Messages.i18n().tr("Направо"));
        for (int i = 1; i < 30; i++) {
            combobox.addItem(Messages.i18n().tr("Пункт меню ") + i, Messages.i18n().tr("Пункт меню ") + i);
        }
    }

    public enum ComboState {
        OFF, ON, ON_READONLY
    }
}
