package kz.arta.synergy.components.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.MenuItemFocusEvent;
import kz.arta.synergy.components.client.util.Selection;

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
public class MenuItem<V> extends Composite implements HasValue<Boolean>, HasValueChangeHandlers<Boolean>, HasText, HasMouseMoveHandlers, HasClickHandlers {
    /**
     * Значение элемента меню
     */
    private V userValue;
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
    Label label;

    /**
     * Бас для внешних событий.
     * Можно и без него, но для так удобнее для тестирования.
     */
    private EventBus bus = new SimpleEventBus();

    public MenuItem(V userValue, String text, ImageResource iconResource) {
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

        root.sinkEvents(Event.ONCLICK |
                Event.ONMOUSEMOVE |
                Event.ONMOUSEOVER |
                Event.ONMOUSEOUT);

        addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                setFocused(true, true);
            }
        }, MouseMoveEvent.getType());

        // клик по пункту выбирает его
        bus.addHandlerToSource(ClickEvent.getType(), this, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setValue(!isSelected(), true);
            }
        });
    }

    public MenuItem(V value, String text) {
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
            fireEvent(new MenuItemFocusEvent<V>(this));
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



    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (event instanceof MouseMoveEvent) {
            super.fireEvent(event);
        } else {
            bus.fireEventFromSource(event, this);
        }
    }

    /**
     * Добавляет хэндлер на изменение "выбранности" элемента
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler);
    }

    /**
     * Добавляет хендлер на фокусировку
     */
    public HandlerRegistration addFocusHandler(MenuItemFocusEvent.Handler<V> handler) {
        return bus.addHandlerToSource(MenuItemFocusEvent.TYPE, this, handler);
    }

    /**
     * Для дебага
     */
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

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return bus.addHandlerToSource(MouseMoveEvent.getType(), this, handler);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return bus.addHandlerToSource(ClickEvent.getType(), this, handler);
    }
}
