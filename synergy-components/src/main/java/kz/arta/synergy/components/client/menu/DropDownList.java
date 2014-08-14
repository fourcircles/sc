package kz.arta.synergy.components.client.menu;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.FilterUpdateEvent;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.menu.filters.ListFilter;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 28.07.14
 * Time: 13:27
 *
 * Выпадающий список.
 * Когда показывать и скрывать список решает пользователь.
 *
 * При открытом списке нажатия клавиш навигации перехватываются.
 *
 * При попытке добавления значения, которое уже присутствует в списке
 * уже существующий элемент списка с данным значением будет заменен на новый.
 * Таким образом гарантируется, что в списке нет элементов с одинаковыми значениями.
 */
public class DropDownList<V> extends MenuBase {
    protected EventBus bus;

    /**
     * Список добавленных элементов меню
     */
    protected ArrayList<Item> items;

    /**
     * Панель с вертикальным скроллом
     */
    private ArtaScrollPanel scroll;

    /**
     * Фильтр примененный к списку
     */
    private ListFilter filter;
    private HandlerRegistration filterRegistration;

    /**
     * Отключены ли кнопки "влево" "вправо".
     */
    private boolean leftRightEnabled;

    /**
     * Элемент который представляет уже выбранный элемент
     */
    private Item selectedItem;

    /**
     * При использовании этого конструктора необходимо присвоить relativeWidget после создания
     */
    protected DropDownList() {
        super();
        scroll = new ArtaScrollPanel(root);
        popup.setWidget(scroll);
        popup.getElement().getStyle().setProperty("maxHeight", Constants.listMaxHeight());

        items = new ArrayList<Item>();

        popup.setStyleName(SynergyComponents.resources.cssComponents().contextMenu());

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            root.getElement().getStyle().setPosition(Style.Position.RELATIVE);
            // вроде как не происходит сдвига для стандартного скрываемого скролла
            if (!Window.Navigator.getAppVersion().contains("MSIE") &&
                    !Window.Navigator.getAppVersion().contains("Trident") &&
                    !Navigator.isFirefox) {
                root.getElement().getStyle().setRight(-15, Style.Unit.PX);
            }
        }
    }

    public DropDownList(Widget relativeWidget, EventBus bus) {
        this();
        this.bus = bus;
        setRelativeWidget(relativeWidget);
    }

    /**
     * Возвращает выбранный элемент
     */
    public Item getFocusedItem() {
        if (focusedIndex != -1) {
            return items.get(focusedIndex);
        } else {
            return null;
        }
    }

    /**
     * Сфокусировать элемент с заданным значением
     * @param value значение
     */
    public void focusValue(V value) {
        if (contains(value)) {
            Item item = get(value);
            focusedIndex = items.indexOf(item);
            item.focus();
        }
    }

    /**
     * Выделяет элемент с заданным значением
     * @param value значение
     */
    public void selectValue(V value) {
        if (contains(value)) {
            get(value).select();
        }
    }

    /**
     * Выделяет элемент с заданным значением как уже выбранный
     * @param value значение
     */
    public void setSelectedValue(V value) {
        if (contains(value)) {
            if (getSelectedItem() != null) {
                getSelectedItem().removeStyleName(SynergyComponents.resources.cssComponents().selected());
            }
            selectedItem = get(value);
            selectedItem.addStyleName(SynergyComponents.resources.cssComponents().selected());
        }
    }

    /**
     * Возвращает элемент списка выделенный как уже выбранный
     * @return элемент списка
     */
    public Item getSelectedItem() {
        return selectedItem;
    }

    /**
     * Возвращает список добавленных элементов
     * @return список
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    public Item get(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    /**
     * Возвращает элемент с заданным значением
     * @param value значение
     * @return элемент
     */
    public Item get(V value) {
        for (Item item : items) {
            //noinspection NonJREEmulationClassesInClientCode
            if ((item.getValue() == null && value == null) || item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Удаляет элемент из списка с заданным значением
     * @param value значение
     */
    public void remove(V value) {
        for (Item item : items) {
            //noinspection NonJREEmulationClassesInClientCode
            if ((item.getValue() == null && value == null) || item.getValue().equals(value)) {
                items.remove(item);
                root.remove(item);
                break;
            }
        }
    }

    /**
     * Удаляет элемент на заданной позиции
     * @param index позиция
     */
    public void remove(int index) {
        root.remove(items.get(index));
        items.remove(index);
    }

    public boolean contains(V value) {
        for (Item item : items) {
            //noinspection NonJREEmulationClassesInClientCode
            if ((item.getValue() == null && value == null) || item.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }

    /**
     * Создает элемент меню с текстом, добавляет его в список и возращает его.
     */
    public Item addItem(String text, V value) {
        if (contains(value)) {
            remove(value);
        }

        Item item = new Item();
        item.setText(text);
        item.setValue(value);
        items.add(item);

        addItem(item);

        return item;
    }

    public Item addItem(String text, ImageResource icon, V value) {
        if (contains(value)) {
            remove(value);
        }

        Item item = new Item();
        item.setText(text);
        item.setValue(value);
        item.setIcon(icon);
        items.add(item);

        addItem(item);

        return item;
    }

    /**
     * В выпадающем списке только один элемент может быть сфокусирован.
     * @param index позиция
     */
    @Override
    protected void focus(int index) {
        noFocused();
        super.focus(index);
    }

    /**
     * Фокусирует элемент на позиции и
     * @param index позиция
     * @param scroll скроллить ли к элементу
     */
    private void focus(int index, boolean scroll) {
        focus(index);
        if (scroll) {
            ensureVisible(items.get(index));
        }
    }


    public void setLeftRightKeysEnabled(boolean enabled) {
        leftRightEnabled = enabled;
    }

    /**
     * Клавиша "вниз". При предпросмотре события оно отменяется.
     */
    @Override
    protected void keyDown(Event.NativePreviewEvent event) {
        event.cancel();
        focus(getNext(), true);
    }

    /**
     * Клавиша "вверх"
     */
    @Override
    protected void keyUp(Event.NativePreviewEvent event) {
        event.cancel();
        focus(getPrevious(), true);
    }

    /**
     * Клавиша "влево"
     */
    @Override
    protected void keyLeft(Event.NativePreviewEvent event) {
        if (leftRightEnabled) {
            event.cancel();
            focus(getFirst(), true);
        }
    }

    /**
     * Клавиша "вправо"
     */
    @Override
    protected void keyRight(Event.NativePreviewEvent event) {
        if (leftRightEnabled) {
            event.cancel();
            focus(getLast(), true);
        }
    }

    /**
     * Клавиша "Enter"
     */
    @Override
    protected void keyEnter(Event.NativePreviewEvent event) {
        event.cancel();
        if (focusedIndex != -1) {
            getFocusedItem().select();
        }
    }


    /**
     * Показывает список под элементом указанным при создании
     */
    public void show() {
        noFocused();
        if (selectedItem != null) {
            selectedItem.removeStyleName(SynergyComponents.resources.cssComponents().selected());
        }

        popup.setHeight(getHeight() + "px");

        popup.getElement().getStyle().setProperty("maxWidth", relativeWidget.getOffsetWidth() - Constants.BORDER_RADIUS * 2 + "px");
        root.getElement().getStyle().setProperty("maxWidth", relativeWidget.getOffsetWidth() - Constants.BORDER_RADIUS * 2 + "px");

        super.showUnderParent();
        if (focusedIndex != -1) {
            scroll.ensureVisible(getFocusedItem());
        }
    }

    /**
     * Показывает список, выделяет элемент на указанной позиции как уже выбранный
     * @param selectedIndex позиция уже выбранного элемента
     */
    public void show(int selectedIndex) {
        Item selectedItem = items.get(selectedIndex);
        show(selectedItem);
    }

    /**
     * Тоже самое, что и show(int)
     * @param item уже выбранный элемент
     */
    public void show(Item item) {
        show();
        if (item != null && items.contains(item) && filter.include(item)) {
            selectedItem = item;
            item.addStyleName(SynergyComponents.resources.cssComponents().selected());
            scroll.ensureVisible(item);

            //навигация клавиатурой при уже выбранном значении начинается с него
            focusedIndex = items.indexOf(item);
        }
    }

    /**
     * Задать фильтр для списка
     * @param filter фильтр
     */
    public void setFilter(ListFilter filter) {
        this.filter = filter;
        if (filterRegistration != null) {
            filterRegistration.removeHandler();
        }
        filterRegistration = filter.addFilterUpdateHandler(new FilterUpdateEvent.Handler() {
            @Override
            public void onFilterUpdate(FilterUpdateEvent event) {
                applyFilter();
            }
        });
        applyFilter();
    }

    /**
     * Применяет заданный фильтр
     */
    private void applyFilter() {
        root.clear();
        for (Item item: items) {
            if (filter == null || filter.include(item)) {
                root.add(item.asWidget());
            }
        }
        popup.setHeight(getHeight() + "px");
    }

    /**
     * Удалить текущий фильтр
     */
    public void clearFilter() {
        if (filterRegistration != null) {
            filterRegistration.removeHandler();
            filterRegistration = null;
        }
        this.filter = null;
        applyFilter();
    }

    public void setWidth(String width) {
        popup.setWidth(width);
    }

    /**
     * Показывать ли верхнюю границу
     * @param setBorder  true/false
     */
    public void setBorderTop(boolean setBorder) {
        if (setBorder) {
            popup.getElement().getStyle().setProperty("borderTop", "");
            popup.getElement().getStyle().setPadding(0, Style.Unit.PX);
        } else {
            popup.getElement().getStyle().setProperty("borderTop", "0px");
        }
    }


    /**
     * Элемент списка
     */
    public class Item extends MenuItem {
        /**
         * Значение элемента
         */
        private V value;

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public void focus() {
            noFocused();
            focusedIndex = items.indexOf(this);
            super.focus();
        }

        @Override
        protected void select() {
            if (bus != null) {
                bus.fireEventFromSource(new ListSelectionEvent<V>(this), DropDownList.this);
            }
        }

        @Override
        public boolean shouldBeSkipped() {
            return super.shouldBeSkipped() || (filter != null && !filter.include(this));
        }
    }

    /**
     * Определяет высоту из количества добавленых элементов
     * @return высота
     */
    protected int getHeight() {
        int cnt = root.getWidgetCount();
        return Math.min(cnt * 32 + Math.max((cnt - 1), 0) * 2, Constants.LIST_MAX_HEIGHT);
    }

    public EventBus getBus() {
        return bus;
    }

    /**
     * Задает EventBus на который будут публиковаться события выбора
     */
    public void setBus(EventBus bus) {
        this.bus = bus;
    }

    public void ensureVisible(Item item) {
        scroll.ensureVisible(item);
    }
}
