package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 31.10.14
 * Time: 9:35
 *
 * Слайдер
 */
public class Slider extends Composite implements HasValue<Double> {

    /**
     * Круг
     */
    private final SimplePanel circle;

    /**
     * Перетаскивается ли круг. Если нет -- не реагируем на mouseover на нем.
     */
    boolean dragging;

    /**
     * Координата клика по кругу при начале перетаскивания.
     */
    private int circleStartX;

    /**
     * Значение слайдера
     */
    private double value = 0;

    /**
     * Опциональное значение
     */
    private double optionalValue = 0;

    /**
     * Линия значения
     */
    SimplePanel valueLine;

    /**
     * Линия опционального значения
     */
    private SimplePanel optionalLine;

    /**
     * @param type true - зеленый, false - красный
     */
    public Slider(boolean type) {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().slider());
        root.addStyleName(SynergyComponents.getResources().cssComponents().unselectable());
        if (!type) {
            root.addStyleName(SynergyComponents.getResources().cssComponents().decline());
        }

        optionalLine = new SimplePanel();
        optionalLine.setStyleName(SynergyComponents.getResources().cssComponents().sliderLine());
        optionalLine.getElement().getStyle().setWidth(0, Style.Unit.PCT);

        valueLine = new SimplePanel();
        valueLine.setStyleName(SynergyComponents.getResources().cssComponents().sliderLine());
        valueLine.getElement().getStyle().setWidth(0, Style.Unit.PCT);

        root.add(optionalLine);
        root.add(valueLine);

        root.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                click(event);
            }
        }, ClickEvent.getType());

        circle = new SimplePanel();
        circle.setStyleName(SynergyComponents.getResources().cssComponents().sliderCircle());

        circle.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                circleMouseDown(event);
            }
        }, MouseDownEvent.getType());

        circle.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                circleMouseMove(event);
            }
        }, MouseMoveEvent.getType());

        circle.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                circleMouseUp(event);
            }
        }, MouseUpEvent.getType());

        circle.getElement().getStyle().setLeft(0, Style.Unit.PCT);

        root.add(circle);
    }

    /**
     * Клик
     */
    void click(ClickEvent event) {
        int x = event.getClientX() - getAbsoluteLeft();
        setValue((double) x / getOffsetWidth(), true);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setCirclePosition(value);
    }

    /**
     * mouseup при перетаскивании круга
     */
    private void circleMouseUp(MouseUpEvent event) {
        Event.releaseCapture(circle.getElement());
        dragging = false;
        event.stopPropagation();
        RootPanel.get().getElement().getStyle().clearCursor();
    }

    /**
     * Начало перетаскивания круга
     */
    private void circleMouseDown(MouseDownEvent event) {
        Event.setCapture(circle.getElement());
        circleStartX = event.getClientX() - (circle.getAbsoluteLeft() + Constants.SLIDER_OUTERCIRCLE_RADIUS);
        dragging = true;
        RootPanel.get().getElement().getStyle().setCursor(Style.Cursor.POINTER);
    }

    /**
     * mousemove при перетаскивании круга
     */
    void circleMouseMove(MouseMoveEvent event) {
        if (!dragging) {
            return;
        }
        int circlePosition = event.getClientX() - getAbsoluteLeft() - circleStartX;
        circlePosition = Math.max(circlePosition, 0);
        circlePosition = Math.min(circlePosition, getOffsetWidth());

        setValue((double) circlePosition / getOffsetWidth(), false);
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
        double trueValue = Math.max(value, 0);
        trueValue = Math.min(trueValue, 1);

        this.value = trueValue;

        setCirclePosition(this.value);
        valueLine.getElement().getStyle().setWidth(this.value * 100, Style.Unit.PCT);

        if (fireEvents) {
            ValueChangeEvent.fire(this, this.value);
        }
    }

    /**
     * Возвращает опциональное значение (желтая полоска)
     */
    @SuppressWarnings("UnusedDeclaration")
    public double getOptionalValue() {
        return optionalValue;
    }

    public void setOptionalValue(double value) {
        double trueValue = Math.max(value, 0);
        trueValue = Math.min(trueValue, 1);

        this.optionalValue = trueValue;
        optionalLine.getElement().getStyle().setWidth(trueValue * 100, Style.Unit.PCT);
    }

    /**
     * Указывает позицию круга соответствующую значению
     * @param value значение
     */
    private void setCirclePosition(double value) {
        circle.getElement().getStyle().setLeft(getOffsetWidth() * value - Constants.SLIDER_OUTERCIRCLE_RADIUS, Style.Unit.PX);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Добавляет хэндлер на событие mouseup круга
     * @param handler хэндлер
     */
    public HandlerRegistration addCircleMouseUpHandler(MouseUpHandler handler) {
        return circle.addDomHandler(handler, MouseUpEvent.getType());
    }
}
