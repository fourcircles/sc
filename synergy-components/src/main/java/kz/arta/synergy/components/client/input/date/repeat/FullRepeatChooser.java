package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.ComboBox;
import kz.arta.synergy.components.client.resources.Messages;

/**
 * User: vsl
 * Date: 25.09.14
 * Time: 14:20
 *
 * Компонент выбора периода
 */
public class FullRepeatChooser extends Composite {

    private final RepeatChooser chooser;

    /**
     * Комбобокс для выбора режима
     */
    private final ComboBox<RepeatChooser.MODE> modeCombo;

    public FullRepeatChooser() {
        this(null);
    }

    public FullRepeatChooser(RepeatChooser.MODE startMode) {
        FlowPanel root = new FlowPanel();
        initWidget(root);

        chooser = new RepeatChooser();
        if (startMode == null) {
            hideChooser();
        } else {
            chooser.setMode(startMode);
        }

        modeCombo = new ComboBox<RepeatChooser.MODE>();
        modeCombo.setReadOnly(true);
        modeCombo.addItem(Messages.i18n.tr("Нет"), null);
        for (RepeatChooser.MODE mode : RepeatChooser.MODE.values()) {
            modeCombo.addItem(mode.toString(), mode);
        }

        modeCombo.selectValue(startMode, false);

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            modeCombo.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        } else {
            modeCombo.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        }

        modeCombo.addValueChangeHandler(new ValueChangeHandler<RepeatChooser.MODE>() {
            @Override
            public void onValueChange(ValueChangeEvent<RepeatChooser.MODE> event) {
                RepeatChooser.MODE mode = event.getValue();
                if (mode != null) {
                    chooser.setMode(mode);
                    showChooser();
                } else {
                    hideChooser();
                }
            }
        });

        root.add(modeCombo);
        root.add(chooser);
    }

    /**
     * Возвращает внутренний объект выбора периода.
     */
    public RepeatChooser getChooser() {
        return chooser;
    }

    public void setMode(RepeatChooser.MODE mode) {
        modeCombo.selectValue(mode, true);
    }

    /**
     * Возвращает комбобокс с режимами
     */
    public ComboBox<RepeatChooser.MODE> getCombo() {
        return modeCombo;
    }

    public void hideChooser() {
        chooser.asWidget().getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    public void showChooser() {
        chooser.asWidget().getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
    }
}
