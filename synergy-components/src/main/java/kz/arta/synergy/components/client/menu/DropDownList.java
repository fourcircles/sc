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
import kz.arta.synergy.components.client.menu.events.HasSelectionEventHandlers;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.menu.events.SelectionEvent;
import kz.arta.synergy.components.client.menu.filters.ListFilter;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 28.07.14
 * Time: 13:27
 *
 * Выпадающий список
 */
public class DropDownList<V> extends MenuBase {
    private EventBus bus;

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
     * Отключены ли кнопки "влево" "вправо"
     */
    private boolean leftRightEnabled;

    public DropDownList(Widget relativeWidget) {
        this();
        setRelativeWidget(relativeWidget);
    }

    public DropDownList() {
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
        this(relativeWidget);
        this.bus = bus;
    }

    /**
     * Возвращает выбранный элемент
     */
    public Item getSelectedItem() {
        if (selectedIndex >=0 && selectedIndex < items.size()) {
            return items.get(selectedIndex);
        } else {
            return null;
        }
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Удаляет все элементы
     */
    public void clear() {
        items.clear();
        super.clearItems();
    }

    public void clearSelection() {
        clearOverStyles();
        selectedIndex = -1;
    }

    /**
     * Создает элемент меню с текстом, добавляет его в список и возращает его.
     */
    public Item addItem(String text, V value) {
        Item item = new Item();
        item.setText(text);
        item.setValue(value);
        items.add(item);

        addItem(item);

        return item;
    }

    public Item addItem(String text, ImageResource icon, V value) {
        Item item = new Item();
        item.setText(text);
        item.setValue(value);
        item.setIcon(icon);

        addItem(item);

        return item;
    }

    /**
     * Клавиша "вниз"
     */
    @Override
    protected void keyDown(Event.NativePreviewEvent event) {
        event.cancel();
        overItemKeyboard(getNext());
    }

    /**
     * Клавиша "вверх"
     */
    @Override
    protected void keyUp(Event.NativePreviewEvent event) {
        event.cancel();
        overItemKeyboard(getPrevious());
    }

    public void setLeftRightKeysEnabled(boolean enabled) {
        leftRightEnabled = enabled;
    }


    /**
     * Клавиша "влево"
     */
    @Override
    protected void keyLeft(Event.NativePreviewEvent event) {
        if (leftRightEnabled) {
            event.cancel();
            overItemKeyboard(getFirst());
        }
    }

    /**
     * Клавиша "вправо"
     */
    @Override
    protected void keyRight(Event.NativePreviewEvent event) {
        if (leftRightEnabled) {
            event.cancel();
            overItemKeyboard(getLast());
        }
    }

    /**
     * Клавиша "Enter"
     */
    @Override
    protected void keyEnter(Event.NativePreviewEvent event) {
        event.cancel();
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            items.get(selectedIndex).selectItem();
        }
    }

    /**
     * Показывает список под элементом указанным при создании
     */
    public void show() {
        popup.setHeight(getHeight() + "px");

        popup.getElement().getStyle().setProperty("maxWidth", relativeWidget.getOffsetWidth() - 8 + "px");
        root.getElement().getStyle().setProperty("maxWidth", relativeWidget.getOffsetWidth() - 8 + "px");

        super.showUnderParent();
        if (selectedIndex != -1) {
            scroll.ensureVisible(getSelectedItem());
        }
    }

    /**
     * Выделяет элемент на указанной позиции. Вызывается при навигации клавишами.
     * @param index позиция
     */
    private void overItemKeyboard(int index) {
        if (index >= 0 && index < items.size()) {
            Item item = items.get(index);
            item.focusItem();
            scroll.ensureVisible(item);
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

    public Item getItemWidthText(String text) {
        for (Item item : items) {
            if (item.getText().equals(text)) {
                return item;
            }
        }
        return null;
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
    public class Item extends MenuItem implements HasSelectionEventHandlers<Item> {
        /**
         * Значение элемента
         */
        private V value;

        public Item() {
            super();
            this.bus = DropDownList.this.bus;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public HandlerRegistration addSelectionHandler(SelectionEvent.Handler<Item> handler) {
            if (bus != null) {
                return bus.addHandler(SelectionEvent.TYPE, handler);
            }
            return null;
        }

        @Override
        public void focusItem() {
            clearOverStyles();
            selectedIndex = items.indexOf(this);
            super.focusItem();
        }

        @Override
        protected void selectItem() {
            bus.fireEvent(new ListSelectionEvent<V>(this, ListSelectionEvent.ActionType.SELECT));
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
     * Задать EventBus на который будут публиковаться события выбора
     */
    public void setBus(EventBus bus) {
        this.bus = bus;
        for (Item item : items) {
            item.setBus(bus);
        }
    }

    public void ensureVisible(Item item) {
        scroll.ensureVisible(item);
    }
}
