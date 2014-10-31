package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.resources.Messages;

/**
 * User: vsl
 * Date: 31.10.14
 * Time: 15:20
 *
 * Прогресс бар
 */
public class ProgressBar extends Composite implements HasValue<Double>, HasClickHandlers, HasText {
    /**
     * Значение прогресс-бара
     */
    private double value;

    /**
     * Опциональное значение (желтая полоска)
     */
    private double optionalValue;

    /**
     * Полоска значения прогресс-бара. Цвет зависит от типа
     */
    private final SimplePanel valueLine;

    /**
     * Полоска опцинального значения.
     */
    private final SimplePanel optionalLine;

    /**
     * Элемент для текста
     */
    private final InlineLabel label;

    /**
     * @param type true - зеленый, false - красный
     */
    public ProgressBar(boolean type) {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().progressBar());
        root.addStyleName(SynergyComponents.getResources().cssComponents().mainText());
        if (!type) {
            root.addStyleName(SynergyComponents.getResources().cssComponents().decline());
        }

        valueLine = new SimplePanel();
        valueLine.setStyleName(SynergyComponents.getResources().cssComponents().progressBarLine());
        valueLine.getElement().getStyle().setWidth(0, Style.Unit.PCT);

        optionalLine = new SimplePanel();
        optionalLine.setStyleName(SynergyComponents.getResources().cssComponents().progressBarLine());
        optionalLine.getElement().getStyle().setWidth(0, Style.Unit.PCT);

        root.add(optionalLine);
        root.add(valueLine);

        label = new InlineLabel();
        root.add(label);
    }

    /**
     * Задает опциональное значение
     * @param value новое значение
     */
    public void setOptionalValue(double value) {
        double trueValue = constraint(value);
        optionalValue = trueValue;
        optionalLine.getElement().getStyle().setWidth(trueValue * 100, Style.Unit.PCT);
    }

    @SuppressWarnings("UnusedDeclaration")
    public double getOptionalValue() {
        return optionalValue;
    }

    private double constraint(double value) {
        double trueValue = Math.max(value, 0);
        trueValue = Math.min(trueValue, 1);
        return trueValue;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        setValue(value, true);
    }

    @Override
    public void setValue(Double value, boolean fireEvents) {
        double trueValue = constraint(value);

        this.value = trueValue;
        if (Double.compare(trueValue, 1.0) == 0) {
            label.setText(Messages.i18n().tr("Завершено"));
        } else {
            int intValue = (int) (trueValue * 100);
            label.setText(intValue + "%");
        }

        valueLine.getElement().getStyle().setWidth(trueValue * 100, Style.Unit.PCT);

        if (fireEvents) {
            ValueChangeEvent.fire(this, trueValue);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }
}
