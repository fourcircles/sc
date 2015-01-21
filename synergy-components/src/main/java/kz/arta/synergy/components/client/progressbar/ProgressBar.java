package kz.arta.synergy.components.client.progressbar;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.Messages;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 31.10.14
 * Time: 15:20
 *
 * Прогресс бар
 *
 * Надписи на прогресс-баре можно задавать с помощью конструктора {@link #ProgressBar(boolean, java.util.List)}.
 * По умолчанию отображается текущее значение в процентах и надпись "завершено" при завершении.
 */
public class ProgressBar extends Composite implements HasValue<Double>, HasClickHandlers, HasText {
    /**
     * Максимальное значение прогресс бара
     */
    private static final double MAX_VALUE = 1.0;
    /**
     * Минимальное значение прогресс бара
     */
    private static final double MIN_VALUE = 0.0;

    /**
     * Надпись, отображающая значение в процентах. Добавляется везде последней, как страховка.
     */
    public static final ProgressBarCustomLabel PERCENTAGE_LABEL = new ProgressBarCustomLabel() {
        @Override
        public String getMessage(double value, double optional) {
            return ((int) (value * 100) + "%") + " / " + ((int) (optional * 100) + "%");
        }

        @Override
        public String getMessage(double value) {
            return (int) (value * 100) + "%";
        }

        @Override
        public boolean isApplicable(double value, double optional) {
            return true;
        }

        @Override
        public boolean isApplicable(double value) {
            return true;
        }
    };

    /**
     * Обычная надпись при завершении задания
     */
    public static final ProgressBarCustomLabel FINAL_LABEL = new ProgressBarConstraintLabel(1.0, 1.0, Messages.i18n().tr("Завершено"));

    /**
     * Значение прогресс-бара
     */
    private double value;

    /**
     * Есть ли у прогресс-бара опциональное значение.
     */
    private boolean hasOptionalValue;
    
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

    private List<ProgressBarCustomLabel> customLabels;

    /**
     * @param type true - зеленый, false - красный
     */
    public ProgressBar(boolean type) {
        this(type, null);
    }

    /**
     * Здесь задается список надписей. При изменении значения подставляется первая валидная надпись прогресс бара.
     * @param customLabels список с надписями
     */
    public ProgressBar(boolean type, List<ProgressBarCustomLabel> customLabels) {
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

        if (customLabels == null) {
            this.customLabels = new ArrayList<ProgressBarCustomLabel>();
            this.customLabels.add(FINAL_LABEL);
        } else {
            this.customLabels = customLabels;
        }

        // надпись, которая отображает просто значение в процентах добавляется всегда и всегда
        // последней, в качестве страховки от случая, когда ни одна надпись не сработает
        this.customLabels.add(PERCENTAGE_LABEL);
    }

    /**
     * Этот метод надо использовать для модификации кастомных надписей уже созданного прогресс-бара
     * @return список, по которому выбирается надпись при изменении значения
     */
    @SuppressWarnings("UnusedDeclaration")
    public List<ProgressBarCustomLabel> getCustomLabels() {
        return customLabels;
    }
    
    private void split() {
        addStyleName(SynergyComponents.getResources().cssComponents().split());
    }
    
    private void merge() {
        removeStyleName(SynergyComponents.getResources().cssComponents().split());
    }

    /**
     * Задает опциональное значение
     * @param optionalValue новое значение
     */
    public void setOptionalValue(Double optionalValue) {
        if (optionalValue == null) {
            if (hasOptionalValue) {
                hasOptionalValue = false;
                merge();
            }
            return;
        }
        
        double trueValue = constraint(optionalValue);
        this.optionalValue = trueValue;

        if (!hasOptionalValue) {
            hasOptionalValue = true;
            split();
        }
        
        updateText();

        optionalLine.getElement().getStyle().setWidth(trueValue * 100, Style.Unit.PCT);
    }

    @SuppressWarnings("UnusedDeclaration")
    public double getOptionalValue() {
        return optionalValue;
    }

    static double constraint(double value) {
        double trueValue = Math.max(value, MIN_VALUE);
        trueValue = Math.min(trueValue, MAX_VALUE);
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
        
        updateText();

        valueLine.getElement().getStyle().setWidth(trueValue * 100, Style.Unit.PCT);

        if (fireEvents) {
            ValueChangeEvent.fire(this, trueValue);
        }
    }

    private void updateText() {
        for (ProgressBarCustomLabel customLabel : customLabels) {
            String newMessage = null;
            if (hasOptionalValue) {
                if (customLabel.isApplicable(value, optionalValue)) {
                    newMessage = customLabel.getMessage(value, optionalValue);
                }
            } else {
                if (customLabel.isApplicable(value)) {
                    newMessage = customLabel.getMessage(value);
                }
            }
            if (newMessage != null) {
                label.setText(newMessage);
                // первое подходящее сообщение
                break;
            }
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
