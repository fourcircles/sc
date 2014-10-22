package kz.arta.synergy.components.client.menu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.MenuFocusEvent;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;
import kz.arta.synergy.components.client.util.ThickMouseMoveHandler;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: vsl
 * Date: 17.10.14
 * Time: 18:05
 *
 * Базовый класс для меню, выпадающего списка и мультисписка (можно выбирать несколько значений).
 */
public abstract class Menu<V> {
    private static final String MIN_WIDTH_PROPERTY = "minWidth";

    protected EventBus bus = new SimpleEventBus();
    /**
     * При изменении размеров окна открытое меню немедленно закрывается
     */
    private HandlerRegistration resizeRegistration;
    /**
     * Хэндлер на немедленное закрытие
     */
    private ResizeHandler resizeHandler;

    /**
     * Находится ли мышь над меню.
     * Если не находится, то события колеса мыши закрывают меню (немедленно).
     */
    private boolean mouseOver = false;

    /**
     * Индекс сфокусированного элемента. Может быть только один.
     */
    protected int focusedIndex = -1;

    /**
     * Список добавленных элементов
     */
    protected List<MenuItem<V>> items;

    /**
     * Попап в котором находится меню
     */
    protected final PopupPanel popup;

    /**
     * Корневая панель
     */
    protected final FlowPanel root;

    /**
     * Эта переменная отключает события MOUSEOVER при навигации клавиатурой.
     * Они включаются обратно при шевелении мышью.
     */
    private boolean keyboardNavigation = false;

    /**
     * Разделители
     */
    private Set<MenuItem> separators = new HashSet<MenuItem>();

    /**
     * Разрешена ли навигация влево-вправо (в конец-начало списка)
     */
    private boolean leftRightNavigation = true;
    protected int minWidth = Integer.MIN_VALUE;

    public Menu() {
        root = new FlowPanel();

        items = new ArrayList<MenuItem<V>>();

        popup = new PopupPanel(true) {
            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                Menu.this.onPreview(event);
            }
            @Override
            public void hide(boolean auto) {
                if (resizeRegistration != null) {
                    resizeRegistration.removeHandler();
                    resizeRegistration = null;
                }
                mouseOver = false;
                noFocused();
                super.hide(auto);
            }
        };

        // этот хэндлера включает события мыши обратно после навигации клавиатурой
        root.sinkEvents(Event.ONMOUSEMOVE);
        root.addDomHandler(new ThickMouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (over(event)) {
                    keyboardNavigation = false;
                }
            }
        }, MouseMoveEvent.getType());

        root.sinkEvents(Event.ONMOUSEOVER);
        root.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                mouseOver = true;
            }
        }, MouseOverEvent.getType());

        root.sinkEvents(Event.ONMOUSEOUT);
        root.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                mouseOver = false;
                // если мышь вне открытого меню - ничего не сфокусировано
                noFocused();
            }
        }, MouseOutEvent.getType());


        resizeHandler = new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                popup.hide();
            }
        };

    }

    /**
     * Убирает состояние фокусирования у всех элементов
     */
    public void noFocused() {
        noFocused(null);
    }

    /**
     * Убирает состояния фокусировани у всех элементов, кроме заданного
     */
    public void noFocused(MenuItem<V> onlyFocusedItem) {
        for (MenuItem<V> item : items) {
            if (item != onlyFocusedItem) {
                item.setFocused(false, false);
            }
        }
        focusedIndex = items.indexOf(onlyFocusedItem);
    }

    /**
     * Показать меню
     */
    private void show() {
        if (resizeRegistration != null) {
            resizeRegistration = Window.addResizeHandler(resizeHandler);
        }
        popup.show();
    }

    /**
     * Предпросмотр событий.
     * Здесь обрабатываются события клавиатуры и запрещается событие MOUSEROVER при навигации
     * клавиатурой.
     * Также при открытом события нажатия на клавиши навигации перехватываются меню.
     */
    private void onPreview(Event.NativePreviewEvent event) {
        Event nativeEvent = Event.as(event.getNativeEvent());

        if (keyboardNavigation && nativeEvent.getTypeInt() == Event.ONMOUSEOVER) {
            event.cancel();
        }

        if (nativeEvent.getTypeInt() == Event.ONMOUSEWHEEL) {
            if (!mouseOver) {
                popup.hide();
            }
        } else if (nativeEvent.getTypeInt() == Event.ONKEYDOWN) {
            switch (nativeEvent.getKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    keyDown();
                    event.cancel();
                    break;
                case KeyCodes.KEY_UP:
                    keyUp();
                    event.cancel();
                    break;
                case KeyCodes.KEY_LEFT:
                    keyLeft(event);
                    break;
                case KeyCodes.KEY_RIGHT:
                    keyRight(event);
                    break;
                case KeyCodes.KEY_ENTER:
                    keyEnter(event);
                    break;
                default:
            }
        }
    }

    /**
     * Нажатие Enter
     */
    private void keyEnter(Event.NativePreviewEvent event) {
        if (focusedIndex != -1 && focusedIndex < items.size()) {
            items.get(focusedIndex).setValue(true, true);
        }
        event.cancel();
    }

    private void keyRight(Event.NativePreviewEvent event) {
        if (leftRightNavigation) {
            keyboardNavigation = true;
            event.cancel();
            focusLast();
        }
    }

    private void keyLeft(Event.NativePreviewEvent event) {
        if (leftRightNavigation) {
            keyboardNavigation = true;
            event.cancel();
            focusFirst();
        }
    }

    private void keyUp() {
        keyboardNavigation = true;
        focusPrevious();
    }

    private void keyDown() {
        keyboardNavigation = true;
        focusNext();
    }

    /**
     * Возвращает сфокусированный элемент или null, если его нет
     */
    public MenuItem<V> getFocused() {
        if (focusedIndex >= 0 && focusedIndex < items.size()) {
            return items.get(focusedIndex);
        } else {
            return null;
        }
    }
    /**
     * Стандартное действие при фокусе последнего элемента
     */
    protected void focusLast() {
        items.get(items.size() - 1).setFocused(true, true);
    }

    /**
     * Стандартное действие при фокусе первого элемента
     */
    protected void focusFirst() {
        items.get(0).setFocused(true, true);
    }

    /**
     * Стандартное действие при фокусе предыдущего элемента
     */
    protected void focusPrevious() {
        int newFocused;
        if (focusedIndex == -1 || focusedIndex == 0) {
            newFocused = items.size() - 1;
        } else {
            newFocused = focusedIndex - 1;
        }
        items.get(newFocused).setFocused(true, true);
    }

    /**
     * Стандартное действие при фокусе следующего элемента
     */
    protected void focusNext() {
        items.get((focusedIndex + 1) % items.size()).setFocused(true, true);
    }

    public boolean contains(V value) {
        return get(value) != null;
    }

    public int size() {
        return items.size();
    }

    /**
     * @param index позиция
     * @return элемент меню на позиции index
     */
    public MenuItem<V> getItemAt(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    /**
     * Возвращает первый элемент меню с заданным значением
     */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public MenuItem<V> get(V value) {
        for (MenuItem<V> item : items) {

            if (value == null) {
                if (item.getUserValue() == null) {
                    return item;
                }
            } else if (value.equals(item.getUserValue())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Удаляет первый элемент в заданным значением
     */
    public void remove(V value) {
        MenuItem<V> item = get(value);
        if (item != null) {
            removeItem(item);
        }
    }

    /**
     * Очищает меню
     */
    public void clear() {
        root.clear();
        items.clear();
        focusedIndex = -1;
    }

    /**
     * Возвращает хэндлер при выборе элемента меню.
     * Это поведение специфично для всех подклассов, поэтому метод абстрактный.
     * @param newItem выбранный элемент меню
     * @return хэндлер
     */
    protected abstract ValueChangeHandler<Boolean> getSelectionHandler(MenuItem<V> newItem);

    /**
     * Добавляет элемент в меню. Создание этого элемента оставляется на пользователя
     * @param item новый элемент
     */
    public void addItem(MenuItem<V> item) {
        if (items.contains(item)) {
            return;
        }
        item.addFocusHandler(new MenuFocusEvent.Handler<V>() {
            @Override
            public void onFocus(MenuFocusEvent<V> event) {
                noFocused(event.getItem());
                if (items.contains(event.getItem())) {
                    focusedIndex = items.indexOf(event.getItem());
                }
            }
        });
        item.addValueChangeHandler(getSelectionHandler(item));
        root.add(item);
        items.add(item);
    }

    /**
     * Удаляет элемент
     */
    public void removeItem(MenuItem<V> item) {
        if (!items.contains(item)) {
            return;
        }
        if (items.indexOf(item) == focusedIndex) {
            focusedIndex = -1;
        }
        // если удаляется элемент с разделителем, то к предыдущему элементу
        // добавляется разделитель
        if (separators.contains(item)) {
            int itemIndex = items.indexOf(item);
            if (itemIndex > 0) {
                addSeparator(itemIndex - 1);
            }
        }
        root.remove(item);
        items.remove(item);
    }

    public List<MenuItem<V>> getItems() {
        return items;
    }

    /**
     * Видно ли меню
     */
    public boolean isShowing() {
        return popup.isShowing();
    }

    public void setMinWidth(int width) {
        minWidth = width;
    }

    /**
     * Задание позиции и размеров меню перед тем, как показать его
     */
    protected void setPopupBeforeShow(Widget relativeWidget, boolean showBorder) {
        int width = Math.max(minWidth, relativeWidget.getOffsetWidth() - Constants.BORDER_RADIUS * 2);

        popup.getElement().getStyle().setProperty(MIN_WIDTH_PROPERTY, width + "px");
        root.getElement().getStyle().setProperty(MIN_WIDTH_PROPERTY, width + "px");

        if (!showBorder) {
            popup.getElement().getStyle().setProperty("borderTop", "0");
        }

        int x = relativeWidget.getAbsoluteLeft() + Constants.BORDER_RADIUS;
        int y = relativeWidget.getAbsoluteTop() + relativeWidget.getOffsetHeight();

        popup.setPopupPosition(x, y);
    }

    /**
     * Показать меню под элементом интерфейса.
     */
    public void showUnder(final Widget relativeWidget) {
        showUnder(relativeWidget, false);
    }

    public void showUnder(final Widget relativeWidget, final boolean showBorder) {
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                setPopupBeforeShow(relativeWidget, showBorder);
            }
        });
    }

    /**
     * Показать меню в заданных координатах (абсолютных).
     */
    public void show(int x, int y) {
        popup.getElement().getStyle().clearProperty("borderTop");
        popup.getElement().getStyle().clearProperty(MIN_WIDTH_PROPERTY);
        root.getElement().getStyle().clearProperty(MIN_WIDTH_PROPERTY);

        popup.setPopupPosition(x, y);
        popup.show();
    }

    public void hide() {
        popup.hide();
    }

    public void addAutoHidePartner(Element e) {
        popup.addAutoHidePartner(e);
    }

    public void removeAutoHidePartner(Element e) {
        popup.removeAutoHidePartner(e);
    }

    /**
     * Добавляет разделитель к элементу на заданной позиции.
     * К последнему элементу разделитель не добавляется.
     * @param index позиция
     */
    public void addSeparator(int index) {
        if (index > 0 && index < items.size() - 1) {
            MenuItem<V> item = items.get(index);
            separators.add(item);
            item.addStyleName(SynergyComponents.getResources().cssComponents().menuSeparator());
        }
    }

    /**
     * Добавляет разделитель к заданному элементу
     */
    public void addSeparator(MenuItem<V> item) {
        if (items.contains(item) && !separators.contains(item)) {
            addSeparator(items.indexOf(item));
        }
    }

    /**
     * Добавляет хэндлер на выделение или снятие выделения с элементов меню
     */
    public HandlerRegistration addItemSelectionHandler(MenuItemSelection.Handler<V> handler) {
        return bus.addHandlerToSource(MenuItemSelection.TYPE, this, handler);
    }

    protected void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
    }

    public void setLeftRightNavigation(boolean leftRightNavigation) {
        this.leftRightNavigation = leftRightNavigation;
    }

    public void addStyleName(String styleName) {
        popup.addStyleName(styleName);
    }

    public void removeStyleName(String styleName) {
        popup.removeStyleName(styleName);
    }
}
