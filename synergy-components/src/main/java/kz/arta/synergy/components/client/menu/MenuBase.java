package kz.arta.synergy.components.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.Selection;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 15.07.14
 * Time: 10:48
 *
 * Базовый класс для выпадающего списка и контекстного меню
 */
public abstract class MenuBase extends PopupPanel{
    /**
     * Основная панель
     */
    protected FlowPanel panel;

    /**
     * Логический контейнер элементов списка
     */
    protected ArrayList<MenuItem> items;

    /**
     * Родительский компонент
     */
    protected Widget relativeWidget;

    /**
     * Показывает находится ли мышь над списком, в этом случае колесо мыши
     * не закрывает список
     */
    private boolean mouseOver = false;

    /**
     * Индекс выбранного элемента, если -1 -- не выбран.
     */
    protected int selectedIndex = -1;

    /**
     * При изменении размера окна список закрывается
     */
    private ResizeHandler resizeHandler;

    /**
     * Наблюдение за событиями изменения размера окна ведется только при
     * открытом списке. HandlerRegistration хранится для прекращения наблюдения
     * за этим событием.
     */
    private HandlerRegistration resizeRegistration;

    public MenuBase() {
        super(true);
        panel = GWT.create(FlowPanel.class);
        setStyleName(getMainStyle());
        items = new ArrayList<MenuItem>();

        setWidget(panel);

        MouseOverHandler over = new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                mouseOver = true;
            }
        };
        MouseOutHandler out = new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                mouseOver = false;
            }
        };

        addDomHandler(over, MouseOverEvent.getType());
        addDomHandler(out, MouseOutEvent.getType());

        resizeHandler = new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                hide();
            }
        };
    }

    public MenuBase(Widget relativeWidget) {
        this();
        this.relativeWidget = relativeWidget;
    }

    /**
     * Удаляет класс over у всег элементов списка.
     */
    private void clearOverStyles() {
        for (MenuItem i: items) {
            if (i != null) {
                i.asWidget().removeStyleName(SynergyComponents.resources.cssComponents().over());
            }
        }
    }

    /**
     * Вызывается при наведении мыши на элемент или выборе клавиатурой.
     * @param item элемент списка
     * @param mouseSelected true - если выбран мышью, false - клавиатурой
     */
    protected void overItem(MenuItem item, boolean mouseSelected) {
        if (items.contains(item)) {
            clearOverStyles();
            selectedIndex = items.indexOf(item);
            item.asWidget().addStyleName(SynergyComponents.resources.cssComponents().over());
        }
    }

    /**
     * Вызывается при наведении мыши на элемент или выборе клавиатурой.
     * @param index индекс элемента списка
     * @param mouseSelected true - если выбран мышью, false - клавиатурой
     */
    protected void overItem(int index, boolean mouseSelected) {
        if (index < 0 || index >= items.size()) {
            return;
        }
        overItem(items.get(index), mouseSelected);
    }

    /**
     * Добавляет элемент в конец списка
     * @param item элемент списка
     */
    private void addItem(MenuItem item) {
        items.add(item);
        panel.add(item.asWidget());
    }

    /**
     * Добавляет элемент с заданным текстом в конец списка
     * @param text текст
     */
    public void addItem(String text) {
        addItem(new MenuItem(text));
    }

    /**
     * Добавляет элемент с заданным текстом и иконкой в конец списка
     * @param text текст
     * @param imageResource иконка
     */
    public void addItem(String text, ImageResource imageResource) {
        addItem(new MenuItem(text, imageResource));
    }

    /**
     * Добавляет элемент с текстом и командой для выполнения при выборе в конец списка.
     * @param text текст
     * @param command команда
     */
    public void addItem(String text, Command command) {
        addItem(new MenuItem(text, command));
    }

    public void addItem(String text, ImageResource icon, Command command) {
        addItem(new MenuItem(text, icon, command));
    }

    /**
     * Удаляет элемент списка с заданным индексом
     * @param index
     */
    public void removeItem(int index) {
        items.remove(index);
        panel.remove(index);
    }

    /**
     * Возвращает текущий выбранный элемент
     * @return выбранный элемент, null - если ничего не выбрано
     */
    public MenuItem getSelectedItem() {
        return selectedIndex == -1 ? null : items.get(selectedIndex);
    }

    /**
     * Возвращает индекс элемента следующего за выбранным.
     * @return индекс следующего элемента, -1 если список пуст
     */
    protected int getNext() {
        if (items.isEmpty()) {
            return -1;
        }
        return (selectedIndex + 1) % items.size();
    }

    /**
     * Возвращает индекс элемент идущего до выбранного.
     * @return индекс элемента до выбранного, -1 если список пуст
     */
    protected int getPrevious() {
        if (items.isEmpty()) {
            return -1;
        }
        if (selectedIndex == -1) {
            return getLast();
        }
        return (selectedIndex - 1) % items.size();
    }

    /**
     * Возвращает индекс первого элемента
     * @return индекс первого элемента
     */
    protected int getFirst() {
        if (items.isEmpty()) {
            return -1;
        }
        return 0;
    }

    /**
     * Возвращает индекс последнего элемента
     * @return индекс последнего элемента
     */
    protected int getLast() {
        if (items.isEmpty()) {
            return -1;
        }
        return items.size() - 1;
    }

    /**
     * Удаляет все элементы списка
     */
    public void clearItems() {
        items.clear();
        panel.clear();
    }

    /**
     * Устанавливает виджет относительно которого список будет отображаться
     * @param widget родительский виджет
     */
    public void setRelativeWidget(Widget widget) {
        if (relativeWidget != null) {
            removeAutoHidePartner(relativeWidget.getElement());
        }
        relativeWidget = widget;
        addAutoHidePartner(relativeWidget.getElement());
        getElement().getStyle().setProperty("borderTop", "0px");
    }

    /**
     * При показе списка начинаем следить за изменением размера окна браузера.
     */
    @Override
    public void show() {
        super.show();
        if (resizeRegistration == null) {
            resizeRegistration = Window.addResizeHandler(resizeHandler);
        }
    }

    /**
     * При скрытии списка перестаем следить за изменением размера окна браузера.
     * Также удаляем over стили для всех элементов списка.
     * @param autoClosed
     */
    @Override
    public void hide(boolean autoClosed) {
        super.hide(autoClosed);
        clearOverStyles();
        if (resizeRegistration != null) {
            resizeRegistration.removeHandler();
            resizeRegistration = null;
        }
        selectedIndex = -1;
    }

    /**
     * Показывает меню под родителем.
     */
    public void showUnderParent() {
        if (relativeWidget != null && relativeWidget.isAttached()) {
            getElement().getStyle().setProperty("minWidth", relativeWidget.getOffsetWidth() - 8 + "px");

            setPopupPositionAndShow(new PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    int x = relativeWidget.getAbsoluteLeft() + 4;
                    int y = relativeWidget.getAbsoluteTop() + relativeWidget.getOffsetHeight() + 1;
                    setPopupPosition(x, y);
                }
            });
        }
    }

    /**
     * Вызывается при выборе пункта меню (клавиша enter, клик мыши)
     */
    protected void itemSelected(MenuItem item) {
        hide();
    }

    /**
     * Пункт меню
     */
    public class MenuItem implements IsWidget {
        /**
         * Текст пункта меню
         */
        protected String text;

        /**
         * Иконка пункта меню
         */
        protected ImageResource icon;

        /**
         * Действие пункта меню
         */
        protected Command command;

        /**
         * Основная панель элемента списка
         */
        protected FlowPanel itemPanel;

        public MenuItem(String text) {
            this(text, null, null);
        }

        public MenuItem(String text, ImageResource icon) {
            this(text, icon, null);
        }

        public MenuItem(String text, Command command) {
            this(text, null, command);
        }

        public MenuItem(String text, ImageResource icon, Command command) {
            this.text = text;
            this.icon = icon;
            this.command = command;
        }

        public String getText() {
            return text;
        }

        public ImageResource getIcon() {
            return icon;
        }

        /**
         * Создает и наполняет FlowPanel, который представляет этот пункт меню.
         *
         * @return панель пункта меню
         */
        @Override
        public Widget asWidget() {
            if (itemPanel == null) {
                itemPanel = new FlowPanel();
                MouseOverHandler over = new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        overItem(MenuItem.this, true);
                        itemPanel.addStyleName(SynergyComponents.resources.cssComponents().over());
                    }
                };
                ClickHandler click = new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        itemSelected(MenuItem.this);
                    }
                };
                itemPanel.addDomHandler(over, MouseOverEvent.getType());
                itemPanel.addDomHandler(click, ClickEvent.getType());

                Label label = new Label(text);
                Selection.disableTextSelectInternal(label.getElement());
                label.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
                label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
                label.getElement().getStyle().setProperty("wordBreak", "break-all");

                if (icon != null) {
                    Image image = GWT.create(Image.class);
                    image.setResource(icon);
                    image.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
                    itemPanel.add(image);
                    image.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
                }

                itemPanel.add(label);
                itemPanel.addStyleName(SynergyComponents.resources.cssComponents().contextMenuItem());
            }

            return itemPanel;
        }

        /**
         * Название стиля для пункта меню
         * @return название стиля
         */
        protected String getMainStyle() {
            return SynergyComponents.resources.cssComponents().contextMenuItem();
        }
    }


    /**
     * Механизм предпросмотра событий PopupPanel.
     * Обрабатываются события колеса мыши и навигационных кнопок клавиатуры.
     * @param event событие
     */
    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        Event nativeEvent = Event.as(event.getNativeEvent());

        switch (nativeEvent.getTypeInt()) {
            case Event.ONMOUSEWHEEL:
                if (!mouseOver) {
                    hide();
                }
                break;
            case Event.ONKEYDOWN:
                switch (nativeEvent.getKeyCode()) {
                    case KeyCodes.KEY_DOWN:
                        event.cancel();
                        overItem(getNext(), false);
                        break;
                    case KeyCodes.KEY_UP:
                        event.cancel();
                        overItem(getPrevious(), false);
                        break;
                    case KeyCodes.KEY_LEFT:
                        event.cancel();
                        overItem(getFirst(), false);
                        break;
                    case KeyCodes.KEY_RIGHT:
                        event.cancel();
                        overItem(getLast(), false);
                        break;
                    case KeyCodes.KEY_ESCAPE:
                        event.cancel();
                        hide();
                        break;
                    case KeyCodes.KEY_ENTER:
                        event.cancel();
                        itemSelected(getSelectedItem());
                        break;
                    default:
                }
        }
        super.onPreviewNativeEvent(event);
    }

    /**
     * Возвращает главный стиль компонента
     * @return название стиля
     */
    abstract protected String getMainStyle();
}
