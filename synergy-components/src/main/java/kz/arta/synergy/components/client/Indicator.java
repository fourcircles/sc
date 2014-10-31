package kz.arta.synergy.components.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;

/**
 * User: vsl
 * Date: 30.10.14
 * Time: 13:47
 *
 * Индикатор
 */
public class Indicator extends Composite implements HasText, HasValue<String> {
    /**
     * Элемент текста
     */
    private final InlineLabel label;

    /**
     * @param startValue начальный текст индикатора
     */
    public Indicator(String startValue) {
        SimplePanel root = new SimplePanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().notifier());

        label = new InlineLabel();
        root.add(label);

        setValue(startValue, false);
    }

    @Override
    public String getText() {
        return getValue();
    }

    @Override
    public void setText(String text) {
        setValue(text, true);
    }

    @Override
    public String getValue() {
        return label.getText();
    }

    @Override
    public void setValue(String value) {
        setValue(value, true);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        boolean same = getValue().equals(value);
        label.setText(value);
        if (!same) {
            ValueChangeEvent.fire(this, getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
