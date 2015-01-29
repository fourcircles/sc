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
 * Надписи на прогресс-баре можно задавать с помощью конструктора {@link #ProgressBar(kz.arta.synergy.components.client.progressbar.ProgressBar.Type, java.util.List)}.
 * По умолчанию отображается текущее значение в процентах и надпись "завершено" при завершении.
 */
public class ProgressBar extends Composite implements HasValue<Double>, HasClickHandlers, HasText {
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
    protected final InlineLabel label;

    /**
     * Текущий тип прогресс-бара. 
     */
    private Type type;

    private List<ProgressBarCustomLabel> customLabels;

    /**
     * Максимальное значение. 
     */
    private double maxValue = 1.0;

    /**
     * Минимальное значение.
     */
    private double minValue = 0.0;
    
    public ProgressBar() {
        this(Type.NEGATIVE);
    }
    
    /**
     * @param type true - зеленый, false - красный
     */
    public ProgressBar(Type type) {
        this(type, null);
    }

    /**
     * Пока что возможности изменять минимальное и максимальное значения на лету не предусмотрено.
     *  
     * @param minValue минимальное значение
     * @param maxValue максимальное значение
     */
    public ProgressBar(Type type, double minValue, double maxValue) {
        this(type);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    /**
     * Здесь задается список надписей. При изменении значения подставляется первая валидная надпись прогресс бара.
     * @param customLabels список с надписями
     */
    public ProgressBar(Type type, List<ProgressBarCustomLabel> customLabels) {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().progressBar());
        root.addStyleName(SynergyComponents.getResources().cssComponents().mainText());
        setType(type);

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

    public void setType(Type type) {
        if (type == Type.NEGATIVE) {
            addStyleName(SynergyComponents.getResources().cssComponents().decline());
        } else {
            removeStyleName(SynergyComponents.getResources().cssComponents().decline());
        }
        this.type = type;
    }
    
    public Type getType() {
        return type;
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
     * Задает опциональное значение.
     *
     * @param optionalValue новое значение, если не null, то прогресс-бар разделяется
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

        optionalLine.getElement().getStyle().setWidth(getNormalizedValue(trueValue) * 100, Style.Unit.PCT);
    }

    @SuppressWarnings("UnusedDeclaration")
    public double getOptionalValue() {
        return optionalValue;
    }
    
    public static double normalizedConstrains(double value) {
        double trueValue = Math.max(value, 0.0);
        trueValue = Math.min(trueValue, 1.0);
        return trueValue;
    }
    
    private double constraint(double value) {
        double trueValue = Math.max(value, getMinValue());
        trueValue = Math.min(trueValue, getMaxValue());
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

        this.value = constraint(value);
        
        updateText();

        valueLine.getElement().getStyle().setWidth(getNormalizedValue(value) * 100, Style.Unit.PCT);

        if (fireEvents) {
            ValueChangeEvent.fire(this, getValue());
        }
    }

    private double getNormalizedValue(double value) {
        double trueValue = constraint(value);
        return (trueValue - getMinValue()) / (getMaxValue() - getMinValue());
    }
    
    public double getMinValue() {
        return minValue;
    }
    
    public double getMaxValue() {
        return maxValue;
    }

    private void updateText() {
        double normValue = getNormalizedValue(value);
        double optionalNormValue = getNormalizedValue(optionalValue);
        
        for (ProgressBarCustomLabel customLabel : customLabels) {
            String newMessage = null;
            if (hasOptionalValue) {
                if (customLabel.isApplicable(normValue, optionalNormValue)) {
                    newMessage = customLabel.getMessage(normValue, optionalNormValue);
                }
            } else {
                if (customLabel.isApplicable(normValue)) {
                    newMessage = customLabel.getMessage(normValue);
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


    /**
     * Тип прогресс бара
     */
    public static enum Type {
        /**
         * Зеленый 
         */
        POSITIVE,

        /**
         * Красный 
         */
        NEGATIVE,

        /**
         * Синий??
         */
        BLUE
    }
}
