package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.HasSelectionEventHandlers;
import kz.arta.synergy.components.client.menu.events.SelectionEvent;
import kz.arta.synergy.components.client.scroll.ArtaVerticalScrollPanel;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 28.07.14
 * Time: 13:27
 *
 * Выпадающий список
 */
public class DropDownList<V> extends MenuBase implements HasSelectionEventHandlers<DropDownList<V>.ListItem>{
    private EventBus bus;

    /**
     * Список добавленных элементов меню
     */
    private ArrayList<ListItem> items;

    /**
     * Панель с вертикальным скроллом
     */
    private ArtaVerticalScrollPanel scroll;

    /**
     * Текущий префикс примененный к списку
     */
    private String prefix = "";

    /**
     * Отключены ли кнопки "влево" "вправо"
     */
    private boolean leftRightEnabled;

    public DropDownList(EventBus bus) {
        super();

        if (bus == null) {
            bus = new SimpleEventBus();
        }
        this.bus = bus;

        scroll = new ArtaVerticalScrollPanel(root);
        popup.setWidget(scroll);
        setRelativeWidget(relativeWidget);
        popup.getElement().getStyle().setProperty("maxHeight", Constants.listMaxHeight());

        items = new ArrayList<ListItem>();

        popup.setStyleName(SynergyComponents.resources.cssComponents().contextMenu());
    }

    public DropDownList(Widget relativeWidget, EventBus bus) {
        this(bus);
        setRelativeWidget(relativeWidget);
    }

    public EventBus getBus() {
        return bus;
    }

    /**
     * Возвращает выбранный элемент
     */
    public ListItem getSelectedItem() {
        if (selectedIndex >=0 && selectedIndex < items.size()) {
            return items.get(selectedIndex);
        } else {
            return null;
        }
    }

    @Override
    ArrayList<ListItem> getItems() {
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
    public ListItem addItem(String text, V value) {
        ListItem item = new ListItem(bus);
        item.setText(text);
        item.setValue(value);
        items.add(item);

        addItem(item);

        return item;
    }

    public ListItem addItem(String text, ImageResource icon, V value) {
        ListItem item = new ListItem(bus);
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

    @Override
    public void addSelectionHandler(SelectionEvent.Handler<ListItem> handler) {
        SelectionEvent.register(bus, handler);
    }

    /**
     * Показывает список под элементом указанным при создании
     */
    public void show() {
        popup.setHeight(getHeight() + "px");
        super.showUnderParent();
    }

    /**
     * Выделяет элемент на указанной позиции. Вызывается при навигации клавишами.
     * @param index позиция
     */
    private void overItemKeyboard(int index) {
        if (index >= 0 && index < items.size()) {
            ListItem item = items.get(index);
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

    public ListItem getItemWidthText(String text) {
        for (ListItem item : items) {
            if (item.getText().equals(text)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Элемент списка
     */
    public class ListItem extends MenuItem implements HasSelectionEventHandlers<ListItem> {
        /**
         * Значение элемента
         */
        private V value;

        private boolean isSelected;

        public ListItem(EventBus bus) {
            this.bus = bus;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
        public boolean isSelected() {
            return isSelected;
        }

        @Override
        public void addSelectionHandler(SelectionEvent.Handler<ListItem> handler) {
            if (bus != null) {
                bus.addHandler(SelectionEvent.TYPE, handler);
            }
        }

        @Override
        protected void focusItem() {
            if (!isSelected) {
                clearOverStyles();
                selectedIndex = items.indexOf(this);
                super.focusItem();
            }
        }

        @Override
        protected void selectItem() {
            if (!isSelected) {
                super.selectItem();
                isSelected = true;
            }
        }

        @Override
        public boolean shouldBeSkipped() {
            return !hasPrefix(this) || isSelected();
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
}
