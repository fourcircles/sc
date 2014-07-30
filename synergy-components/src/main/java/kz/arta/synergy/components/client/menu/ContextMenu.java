package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.SelectionEvent;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 28.07.14
 * Time: 17:34
 *
 * Контекстное меню
 */
public class ContextMenu extends MenuBase {

    /**
     * Список элементов меню
     */
    private ArrayList<ContextMenuItem> items;

    private EventBus bus;

    /**
     * Хэндлер для события выбора элемента меню
     */
    private SelectionEvent.Handler<ContextMenuItem> selectionHandler;

    /**
     * Конструктор для контекстного меню без родителя
     */
    public ContextMenu() {
        this(null);
    }

    /**
     * Конструктор для контекстного меню с родителем
     * @param relativeWidget виджет по которым будет контекстное меню
     */
    public ContextMenu(Widget relativeWidget) {
        items = new ArrayList<ContextMenuItem>();

        popup.setStyleName(SynergyComponents.resources.cssComponents().contextMenu());

        setRelativeWidget(relativeWidget);
        selectionHandler = new SelectionEvent.Handler<ContextMenuItem>() {
            @Override
            public void onSelection(SelectionEvent<ContextMenuItem> event) {
                hide();
                Command command = event.getValue().getCommand();
                if (command != null) {
                    command.execute();
                }
            }
        };
        setBus(new SimpleEventBus());
    }

    @Override
    ArrayList<ContextMenuItem> getItems() {
        return items;
    }

    /**
     * Создает, добавляет и затем возвращает новый элемент меню с
     * указанными параметрами
     * @param text текст элемента
     * @param command комманда, которую надо выполнить
     */
    public ContextMenuItem addItem(String text, Command command) {
        ContextMenuItem item = new ContextMenuItem(bus);
        item.setText(text);
        item.setCommand(command);
        items.add(item);
        addItem(item);

        return item;
    }

    public ContextMenuItem addItem(String text, ImageResource icon, Command command) {
        ContextMenuItem item = addItem(text, command);
        item.setIcon(icon);
        return item;
    }

    /**
     * Добавляет разделитель
     */
    public void addSeparator() {
        ContextMenuItem item = new ContextMenuItem(bus) {
            @Override
            public boolean shouldBeSkipped() {
                return true;
            }

            @Override
            protected String getMainStyle() {
                return SynergyComponents.resources.cssComponents().menuSeparator();
            }
        };
        items.add(item);
        addItem(item);
    }

    /**
     * Выравнивает меню так, чтобы его верхний левый угол имел заданные координаты. Если при этом
     * меню выходит за пределы окна браузера, например за правую границу, заданные координаты будет
     * иметь верхний правый угол и т. д.
     * @param posX координата X
     * @param posY координата Y
     */
    public void show(int posX, int posY) {
        show();
        int lenX = popup.getOffsetWidth();
        int lenY = popup.getOffsetHeight();

        if (posX + lenX > Window.getClientWidth()) {
            posX -= lenX;
        }
        if (posY + lenY > Window.getClientHeight()) {
            posY -= lenY;
        }

        popup.setPopupPosition(posX, posY);
    }

    public void show() {
        showUnderParent();
    }

    /**
     * Клавиша "вниз"
     */
    @Override
    protected void keyDown(Event.NativePreviewEvent event) {
        event.cancel();
        int index = getNext();
        if (index != -1) {
            items.get(index).focusItem();
        }
    }

    /**
     * Клавиша "вверх"
     */
    @Override
    protected void keyUp(Event.NativePreviewEvent event) {
        event.cancel();
        int index = getPrevious();
        if (index != -1) {
            items.get(index).focusItem();
        }
    }

    @Override
    protected void keyLeft(Event.NativePreviewEvent event) {
        event.cancel();
        int index = getFirst();
        if (index != -1) {
            items.get(index).focusItem();
        }
    }

    @Override
    protected void keyRight(Event.NativePreviewEvent event) {
        event.cancel();
        int index = getLast();
        if (index != -1) {
            items.get(index).focusItem();
        }
    }

    /**
     * При нажатии "Enter" элемент меню выбирается
     */
    @Override
    protected void keyEnter(Event.NativePreviewEvent event) {
        event.cancel();
        if (selectedIndex != -1) {
            items.get(selectedIndex).selectItem();
        }
    }

    /**
     * Класс для пункта контекстного меню
     */
    public class ContextMenuItem extends MenuItem {

        /**
         * Комманда, которая запускается при выборе пункта
         */
        private Command command;

        public ContextMenuItem(EventBus bus) {
            super();
            this.bus = bus;
        }

        public void setCommand(Command command) {
            this.command = command;
        }

        public Command getCommand() {
            return command;
        }

        @Override
        protected void focusItem() {
            clearOverStyles();
            selectedIndex = items.indexOf(this);
            super.focusItem();
        }
    }

    public void setBus(EventBus bus) {
        this.bus = bus;
        for (ContextMenuItem item : items) {
            item.bus = bus;
        }
        SelectionEvent.register(bus, selectionHandler);
    }

    public EventBus getBus() {
        return bus;
    }
}
