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
    boolean mouseOver = false;

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
                i.setOver(false);
            }
        }
    }

    /**
     * Вызывается при наведении мыши на элемент или выборе клавиатурой.
     * @param item элемент списка
     * @param mouseSelected true - если выбран мышью, false - клавиатурой
     */
    protected void overItem(MenuItem item, boolean mouseSelected) {
        if (items.contains(item) && getSelectedItem() != item) {
            clearOverStyles();
            selectedIndex = items.indexOf(item);
            item.setOver(true);
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
     * @return новый элемент списка
     */
    protected MenuItem addItem(MenuItem item) {
        items.add(item);
        panel.add(item.asWidget());
        return item;
    }

    /**
     * Добавляет элемент с заданным текстом в конец списка
     * @param text текст
     * @return новый элемент списка
     */
    public MenuItem addItem(String text) {
        return addItem(new MenuItem(text));
    }

    /**
     * Добавляет элемент с заданным текстом и иконкой в конец списка
     * @param text текст
     * @param imageResource иконка
     * @return новый элемент списка
     */
    public MenuItem addItem(String text, ImageResource imageResource) {
        return addItem(new MenuItem(text, imageResource));
    }

    /**
     * Добавляет элемент с текстом и командой для выполнения при выборе в конец списка.
     * @param text текст
     * @param command команда
     * @return новый элемент списка
     */
    public MenuItem addItem(String text, Command command) {
        return addItem(new MenuItem(text, command));
    }

    public MenuItem addItem(String text, ImageResource icon, Command command) {
        return addItem(new MenuItem(text, icon, command));
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
     * Метод показывает может ли быть выбран элемент меню.
     * Участвует в расчетах номера следующего, предыдущего элементов.
     * Например, в контекстном меню переписывается запрещая выбирать разделители.
     * @param item элемент списка
     * @return true - может ли быть выбран, false - нет
     */
    protected boolean canBeChosen(MenuItem item) {
        return true;
    }

    private boolean canBeChosen(int index) {
        return canBeChosen(items.get(index));
    }

    /**
     * Возвращает индекс элемента следующего за выбранным, который может быть выбран.
     * Индекс должен находится в пределах списка или быть равным -1.
     * @return индекс следующего элемента, -1 если таких элементов нет.
     */
    protected int getNext() {
        return getNext(selectedIndex);
    }

    private int getNext(int start) {
        int i = start + 1;
        if (i < 0) {
            return -1;
        }
        while (i < items.size() && !canBeChosen(i)) {
            i++;
        }
        if (i == items.size()) {
            i = 0;
            while (i < start && !canBeChosen(i)) {
                i++;
            }
            return i >= start ? -1 : i;
        } else {
            return i;
        }
    }

    /**
     * Возвращает индекс элемента перед выбранным. Учитывает может ли элемент быть выбран.
     * Индекс должен находится в пределах списка или быть равным -1.
     * @return индекс элемента до выбранного, -1 если таких элементов нет
     */
    protected int getPrevious() {
        return getPrevious(selectedIndex);
    }

    private int getPrevious(int start) {
        if (start == -1) {
            return getLast();
        }
        int i = start - 1;
        if (i >= items.size()) {
            return -1;
        }
        while (i >= 0 && !canBeChosen(i)) {
            i--;
        }
        if (i == -1) {
            i = items.size() - 1;
            while (i > start && !canBeChosen(i)) {
                i--;
            }
            return i <= start ? -1 : i;

        } else {
            return i;
        }
    }

    /**
     * Возвращает индекс первого элемента
     * @return индекс первого элемента
     */
    protected int getFirst() {
        return getNext(-1);
    }

    /**
     * Возвращает индекс последнего элемента
     * @return индекс последнего элемента
     */
    protected int getLast() {
        return getPrevious(items.size());
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
         * Добавляет или удаляет стиль over для элемента списка
         * @param over
         */
        public void setOver(boolean over) {
            if (over) {
                itemPanel.addStyleName(SynergyComponents.resources.cssComponents().over());
            } else {
                itemPanel.removeStyleName(SynergyComponents.resources.cssComponents().over());
            }

        }

        /**
         * Создает и наполняет FlowPanel, который представляет этот пункт меню.
         *
         * @return панель пункта меню
         */
        @Override
        public Widget asWidget() {
            if (itemPanel == null) {
                itemPanel = GWT.create(FlowPanel.class);
                MouseOverHandler over = new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        overItem(MenuItem.this, true);
                    }
                };
                ClickHandler click = new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        itemSelected(MenuItem.this);
                    }
                };
                MouseMoveHandler move = new MouseMoveHandler() {
                    @Override
                    public void onMouseMove(MouseMoveEvent event) {
                        overItem(MenuItem.this, true);
                    }
                };
                itemPanel.addDomHandler(over, MouseOverEvent.getType());
                itemPanel.addDomHandler(click, ClickEvent.getType());
                itemPanel.addDomHandler(move, MouseMoveEvent.getType());

                Label label = GWT.create(Label.class);
                label.setText(text);
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

    protected void keyLeft(Event.NativePreviewEvent event) {
        event.cancel();
        overItem(getFirst(), false);
    }

    protected void keyRight(Event.NativePreviewEvent event) {
        event.cancel();
        overItem(getLast(), false);
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
                        keyLeft(event);
                        break;
                    case KeyCodes.KEY_RIGHT:
                        keyRight(event);
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
