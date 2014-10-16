package kz.arta.synergy.components.client.input.number;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Event;
import kz.arta.synergy.components.client.comments.events.InputChangeEvent;
import kz.arta.synergy.components.client.input.TextInput;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 24.07.14
 * Time: 10:52
 *
 * Числовое поле ввода
 */
public class NumberInput extends TextInput {
    private static final InputConstraint DEFAULT_CONSTRAINT = OnlyDigitsConstraint.getInstance();

    private String oldValue = "";

    /**
     * Ограничение на ввод
     */
    private List<InputConstraint> constraints;

    public NumberInput() {
        this(null);
    }

    public NumberInput(List<InputConstraint> constraints) {
        this(constraints, true);
    }

    public NumberInput(boolean allowEmpty) {
        this(null, allowEmpty);
    }

    public NumberInput(List<InputConstraint> constraints, boolean allowEmpty) {
        super(allowEmpty);
        if (constraints == null) {
            this.constraints = new ArrayList<InputConstraint>();
            this.constraints.add(DEFAULT_CONSTRAINT);
        } else {
            this.constraints = constraints;
        }
    }

    /**
     * Ввод всего, кроме цифр и разделителя запрещается.
     * Количество разделителей не больше 1.
     */
    @Override
    protected void init() {
        InputChangeEvent.addInputHandler(getElement(), new InputChangeEvent.Handler() {
            @Override
            public void onInputChange(InputChangeEvent event) {
                boolean allowChange = true;
                for (InputConstraint constraint : constraints) {
                    if (!constraint.allowChange(getValue())) {
                        allowChange = false;
                        break;
                    }
                }
                if (allowChange) {
                    oldValue = getValue();
                } else {
                    setValue(oldValue);
                }
            }
        });
        super.init();
    }

    /**
     * Вставка текста (Ctrl-V) запрещается, потому что тогда можно вставить не число.
     */
    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONPASTE) {
            event.preventDefault();
        }
    }

    public Number getNumber() {
        NumberFormat nf = NumberFormat.getDecimalFormat();
        return nf.parse(getText());
    }

    public int getIntegerValue() {
        return (int) NumberFormat.getDecimalFormat().parse(getValue());
    }

    public double getDoubleValue() {
        return NumberFormat.getDecimalFormat().parse(getValue());
    }
}
