package kz.arta.synergy.components.client.dagger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.dagger.events.DaggerFocusEvent;
import kz.arta.synergy.components.client.util.Selection;
import kz.arta.synergy.components.client.util.ThickMouseMoveHandler;

/**
 * User: vsl
 * Date: 17.10.14
 * Time: 17:56
 *
 * Элемент меню
 *
 * Здесь "значением" для интерфейса {@link com.google.gwt.user.client.ui.HasValue} является факт того,
 * выбран ли элемент или нет.
 */
public class DaggerItem<V> extends Composite implements HasValue<Boolean>, HasValueChangeHandlers<Boolean>, HasText {
    /**
     * Значение элемента меню
     */
    private V userValue;
    /**
     * Картинка иконки
     */
    private ImageResource iconResource;

    /**
     * Выбран ли элемент
     */
    private boolean isSelected = false;
    /**
     * Сфокусирован ли элемент
     */
    private boolean isFocused = false;
    /**
     * Элемент иконки
     */
    private Image icon;
    /**
     * Элемент текста
     */
    private Label label;

    public DaggerItem(V userValue, String text, ImageResource iconResource) {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().contextMenuItem());

        this.userValue = userValue;

        icon = GWT.create(Image.class);
        setIcon(iconResource);
        root.add(icon);

        label = GWT.create(Label.class);
        label.setText(text);
        Selection.disableTextSelectInternal(label.getElement());
        root.add(label);

        // не регистрируем незначительные дерганья мыши
        root.sinkEvents(Event.ONMOUSEMOVE);
        root.addDomHandler(new ThickMouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (over(event)) {
                    setFocused(true, true);
                }
            }
        }, MouseMoveEvent.getType());

        // из-за того, что мы не следим за небольшими движениями мыши
        // надо также следить за событием MOUSEOVER, иначе будет некрасиво
        root.sinkEvents(Event.ONMOUSEOVER);
        root.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                setFocused(true, true);
            }
        }, MouseOverEvent.getType());

        // клик по пункту выбирает его
        root.sinkEvents(Event.ONCLICK);
        root.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setValue(!isSelected(), true);
            }
        }, ClickEvent.getType());
    }

    public DaggerItem(V value, String text) {
        this(value, text, null);
    }

    /**
     * Задать иконку
     * @param iconResource картинка иконки
     */
    public void setIcon(ImageResource iconResource) {
        if (iconResource == null) {
            icon.getElement().getStyle().setDisplay(Style.Display.NONE);
        } else {
            icon.setResource(iconResource);
            icon.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        }
    }

    public boolean isFocused() {
        return isFocused;
    }

    /**
     * Изменяет состояние "фокусированности" с опциональным созданием события при изменении этого состояния.
     */
    public void setFocused(boolean isFocused, boolean fireEvents) {
        boolean changed = this.isFocused != isFocused;
        this.isFocused = isFocused;
        if (changed) {
            if (isFocused) {
                getElement().focus();
                addStyleName(SynergyComponents.getResources().cssComponents().over());
            } else {
                removeStyleName(SynergyComponents.getResources().cssComponents().over());
            }
        }
        if (changed && isFocused && fireEvents) {
            fireEvent(new DaggerFocusEvent<V>(this));
        }
    }

    /**
     * Выбран ли элемент
     */
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public Boolean getValue() {
        return isSelected();
    }

    @Override
    public void setValue(Boolean value) {
        setValue(value, true);
    }

    /**
     * Выделяет элемент или снимает выделение. С опциональным созданием события при изменении значения.
     */
    @Override
    public void setValue(Boolean isSelected, boolean fireEvents) {
        boolean changed = this.isSelected != isSelected;
        this.isSelected = isSelected;
        if (isSelected) {
            addStyleName(SynergyComponents.getResources().cssComponents().selected());
        } else {
            removeStyleName(SynergyComponents.getResources().cssComponents().selected());
        }
        if (changed && fireEvents) {
            ValueChangeEvent.fire(this, isSelected);
        }
    }

    public V getUserValue() {
        return userValue;
    }

    /**
     * Добавляет хэндлер на изменение "выбранности" элемента
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Добавляет хендлер на фокусировку
     */
    public HandlerRegistration addFocusHandler(DaggerFocusEvent.Handler<V> handler) {
        return addHandler(handler, DaggerFocusEvent.TYPE);
    }

    @Override
    public String toString() {
        return label.getText();
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
