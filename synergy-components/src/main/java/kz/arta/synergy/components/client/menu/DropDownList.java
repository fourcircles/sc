package kz.arta.synergy.components.client.menu;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.HasSelectionEventHandlers;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.menu.events.SelectionEvent;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;

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
     * Текущий префикс примененный к списку
     */
    private String prefix = "";

    /**
     * Отключены ли кнопки "влево" "вправо"
     */
    private boolean leftRightEnabled;

    public DropDownList(Widget relativeWidget) {
        super();
        setRelativeWidget(relativeWidget);
        scroll = new ArtaScrollPanel(root);
        popup.setWidget(scroll);
        setRelativeWidget(relativeWidget);
        popup.getElement().getStyle().setProperty("maxHeight", Constants.listMaxHeight());

        items = new ArrayList<Item>();

        popup.setStyleName(SynergyComponents.resources.cssComponents().contextMenu());

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            root.getElement().getStyle().setPosition(Style.Position.RELATIVE);
            // вроде как не происходит сдвига для стандартного скрываемого скролла
            if (!Window.Navigator.getAppVersion().contains("MSIE") &&
                    !Window.Navigator.getAppVersion().contains("Trident")) {
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

    @Override
    ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Удаляет все элементы
     */
    public void clear() {
        items.clear();
        super.clearItems();
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
     * Определяет начинается ли текст элемента меню с префикса этого списка.
     * @param item элемент меню
     * @return true - начинается, false - нет
     */
    private boolean hasPrefix(MenuItem item) {
        if (prefix == null || prefix.isEmpty()) {
            return true;
        }
        String str = item.getText();
        if (str == null || prefix.length() > str.length()) {
            return false;
        }
        int len = prefix.length();
        str = str.toLowerCase();
        prefix = prefix.toLowerCase();

        return str.substring(0, len).equals(prefix);
    }

    /**
     * Применяет префикс к списку, показывая только элементы, текст которых начинается с
     * этого префикса
     * @param prefix префикс
     */
    public void applyPrefix(String prefix) {
        root.clear();
        this.prefix = prefix;
        int cnt = 0;
        for (MenuItem item: items) {
            if (hasPrefix(item)) {
                root.add(item.asWidget());
                cnt++;
            }
        }
        popup.setHeight(getHeight() + "px");
    }

    /**
     * Убирает примененный префикс, отображаются все элементы.
     */
    public void removePrefix() {
        applyPrefix("");
    }

    public Item getItemWidthText(String text) {
        for (Item item : items) {
            if (item.getText().equals(text)) {
                return item;
            }
        }
        return null;
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
            this.bus = DropDownList.this.bus;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public void addSelectionHandler(SelectionEvent.Handler<Item> handler) {
            if (bus != null) {
                bus.addHandler(SelectionEvent.TYPE, handler);
            }
        }

        @Override
        protected void focusItem() {
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
            return super.shouldBeSkipped() || !hasPrefix(this);
        }
    }

    /**
     * Определяет высоту из количества добавленых элементов
     * @return высота
     */
    private int getHeight() {
        int cnt = root.getWidgetCount();
        return Math.min(cnt * 32 + Math.max((cnt - 1), 0) * 2, Constants.LIST_MAX_HEIGHT);
    }

    public EventBus getBus() {
        return bus;
    }

    public void setBus(EventBus bus) {
        this.bus = bus;
        for (Item item : items) {
            item.setBus(bus);
        }
    }
}
