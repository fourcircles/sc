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
 */
//TODO document
//TODO move smartshow to contextmenu
public abstract class MenuBase extends PopupPanel{
    protected FlowPanel panel;

    protected ArrayList<MenuItem> items;

    protected Widget relativeWidget;

    private boolean mouseOver = false;

    protected int selectedIndex = -1;

    private ResizeHandler resizeHandler;
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

    protected void selectItem(int index) {
        if (index < 0 || index >= items.size()) {
            return;
        }
        MenuItem item = items.get(index);
        for (MenuItem i: items) {
            i.asWidget().removeStyleName(SynergyComponents.resources.cssComponents().over());
        }
        selectedIndex = index;
        item.asWidget().addStyleName(SynergyComponents.resources.cssComponents().over());
    }


    public MenuBase(Widget relativeWidget) {
        this();
        this.relativeWidget = relativeWidget;
    }

    private void addItem(MenuItem item) {
        items.add(item);
        panel.add(item.asWidget());
    }

    public void addItem(String text) {
        addItem(new MenuItem(text));
    }

    public void addItem(String text, ImageResource imageResource) {
        addItem(new MenuItem(text, imageResource));
    }

    public void addItem(String text, Command command) {
        addItem(new MenuItem(text, command));
    }

    public void addItem(String text, ImageResource icon, Command command) {
        addItem(new MenuItem(text, icon, command));
    }

    public void removeItem(int index) {
        items.remove(index);
        panel.remove(index);
    }

    public String getText(int index) {
        return items.get(index).getText();
    }

    public void setItemText(int index, String text) {
        items.get(index).setText(text);
    }

    protected int getNext() {
        if (items.isEmpty()) {
            return -1;
        }
        return (selectedIndex + 1) % items.size();
    }

    protected int getPrevious() {
        if (items.isEmpty()) {
            return -1;
        }
        return (selectedIndex - 1) % items.size();
    }

    protected int getFirst() {
        if (items.isEmpty()) {
            return -1;
        }
        return 0;
    }

    protected int getLast() {
        if (items.isEmpty()) {
            return -1;
        }
        return items.size();
    }

    public void clearItems() {
        items.clear();
        panel.clear();
    }

    public void setRelativeWidget(Widget widget) {
        if (relativeWidget != null) {
            removeAutoHidePartner(relativeWidget.getElement());
        }
        relativeWidget = widget;
        addAutoHidePartner(relativeWidget.getElement());
    }

    @Override
    public void show() {
        super.show();
        if (resizeRegistration == null) {
            resizeRegistration = Window.addResizeHandler(resizeHandler);
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (resizeRegistration != null) {
            resizeRegistration.removeHandler();
            resizeRegistration = null;
        }
    }

    /**
     * Выравнивает меню так, чтобы его верхний левый угол имел заданные координаты. Если при этом
     * меню выходит за пределы окна браузера, например за правую границу, заданные координаты будет
     * иметь верхний правый угол и т. д.
     * @param posX координата X
     * @param posY координата Y
     */
    public void smartShow(int posX, int posY) {
        show();
        int lenX = getOffsetWidth();
        int lenY = getOffsetHeight();

        if (posX + lenX > Window.getClientWidth()) {
            posX -= lenX;
        }
        if (posY + lenY > Window.getClientHeight()) {
            posY -= lenY;
        }

        setPopupPosition(posX, posY);
    }

    /**
     * Показывает меню под родителем.
     */
    public void showUnderParent() {
        if (relativeWidget != null && relativeWidget.isAttached()) {
            getElement().getStyle().setProperty("minWidth", relativeWidget.getOffsetWidth() - 8 + "px");

            int x = relativeWidget.getAbsoluteLeft() + 4;
            int y = relativeWidget.getAbsoluteTop() + relativeWidget.getOffsetHeight() + 1;
            setPopupPosition(x, y);
            show();
        }
    }

    /**
     * Пункт меню
     */
    protected static class MenuItem implements IsWidget {
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

        public void setText(String text) {
            this.text = text;
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
                        itemPanel.addStyleName(SynergyComponents.resources.cssComponents().over());
                    }
                };
                MouseOutHandler out = new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        itemPanel.removeStyleName(SynergyComponents.resources.cssComponents().over());
                    }
                };
                itemPanel.addDomHandler(over, MouseOverEvent.getType());
                itemPanel.addDomHandler(out, MouseOutEvent.getType());

                Label label = new Label(text);
                Selection.disableTextSelectInternal(label.getElement());
                label.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
                label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

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
                        selectItem(getNext());
                        break;
                    case KeyCodes.KEY_UP:
                        event.cancel();
                        selectItem(getPrevious());
                        break;
                    case KeyCodes.KEY_LEFT:
                        event.cancel();
                        selectItem(getFirst());
                        break;
                    case KeyCodes.KEY_RIGHT:
                        event.cancel();
                        selectItem(getLast());
                        break;
                    case KeyCodes.KEY_ESCAPE:
                        event.cancel();
                        hide();
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
