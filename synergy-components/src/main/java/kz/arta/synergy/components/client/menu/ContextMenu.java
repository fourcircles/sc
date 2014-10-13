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
import java.util.List;

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
    private List<ContextMenuItem> items;

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

        popup.setStyleName(SynergyComponents.getResources().cssComponents().contextMenu());

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

            @Override
            public void onDeselection(SelectionEvent<ContextMenuItem> event) {
                //не имеет смысла в контекстном меню
            }
        };
        setBus(new SimpleEventBus());
    }

    @Override
    List<ContextMenuItem> getItems() {
        return items;
    }

    /**
     * Создает, добавляет и затем возвращает новый элемент меню с
     * указанными параметрами
     * @param text текст элемента
     * @param command комманда, которую надо выполнить
     */
    public ContextMenuItem addItem(String text, Command command) {
        ContextMenuItem item = new ContextMenuItem();
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
        ContextMenuItem item = new ContextMenuItem() {
            @Override
            public boolean shouldBeSkipped() {
                return true;
            }

            @Override
            protected String getMainStyle() {
                return SynergyComponents.getResources().cssComponents().menuSeparator();
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
        super.show();
        int lenX = popup.getOffsetWidth();
        int lenY = popup.getOffsetHeight();

        int x = posX;
        int y = posY;

        if (x + lenX > Window.getClientWidth()) {
            x -= lenX;
        }
        if (y + lenY > Window.getClientHeight()) {
            y -= lenY;
        }

        popup.setPopupPosition(x, y);
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
            items.get(index).focus();
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
            items.get(index).focus();
        }
    }

    @Override
    protected void keyLeft(Event.NativePreviewEvent event) {
        event.cancel();
        int index = getFirst();
        if (index != -1) {
            items.get(index).focus();
        }
    }

    @Override
    protected void keyRight(Event.NativePreviewEvent event) {
        event.cancel();
        int index = getLast();
        if (index != -1) {
            items.get(index).focus();
        }
    }

    /**
     * При нажатии "Enter" элемент меню выбирается
     */
    @Override
    protected void keyEnter(Event.NativePreviewEvent event) {
        event.cancel();
        if (focusedIndex != -1) {
            items.get(focusedIndex).select();
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

        public void setCommand(Command command) {
            this.command = command;
        }

        public Command getCommand() {
            return command;
        }

        @Override
        protected void focus() {
            noFocused();
            focusedIndex = items.indexOf(this);
            super.focus();
        }

        @Override
        protected void select() {
            hide();
            if (command != null) {
                command.execute();
            }
        }
    }

    public void setBus(EventBus bus) {
        this.bus = bus;
        SelectionEvent.register(bus, selectionHandler);
    }

    public EventBus getBus() {
        return bus;
    }
}
