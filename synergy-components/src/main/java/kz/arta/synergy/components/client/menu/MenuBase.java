package kz.arta.synergy.components.client.menu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 28.07.14
 * Time: 11:16
 *
 * Базовый класс для выпадающего списка и меню
 */
public abstract class MenuBase {

    /**
     * Индекс выбранного элемента, если -1 -- не выбран.
     */
    protected int focusedIndex = -1;

    /**
     * Попап в котором находится основная панель
     */
    protected PopupPanel popup;

    /**
     * Основная панель
     */
    protected ArtaFlowPanel root;

    /**
     * Показывает находится ли мышь над списком, в этом случае колесо мыши
     * не закрывает список
     */
    protected boolean mouseOver = false;

    /**
     * Родительский компонент
     */
    protected Widget relativeWidget;

    /**
     * Наблюдение за событиями изменения размера окна ведется только при
     * открытом списке. HandlerRegistration хранится для прекращения наблюдения
     * за этим событием.
     */
    private HandlerRegistration resizeRegistration;

    /**
     * При изменении размера окна список закрывается
     */
    private ResizeHandler resizeHandler;

    protected MenuBase() {
        root = new ArtaFlowPanel();


        popup = new PopupPanel(true) {
            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                MenuBase.this.onPreview(event);
            }
            @Override
            public void hide(boolean auto) {
//                noFocused();
                if (resizeRegistration != null) {
                    resizeRegistration.removeHandler();
                    resizeRegistration = null;
                }
                mouseOver = false;
                super.hide(auto);
            }
        };
        popup.setWidget(root);

        root.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                mouseOver = true;
            }
        });
        root.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                mouseOver = false;
                noFocused();
                focusedIndex = -1;
            }
        });
        resizeHandler = new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                hide();
            }
        };

    }

    /**
     * Удаляет все элементы списка
     */
    public void clear() {
        root.clear();
        getItems().clear();
        focusedIndex = -1;
    }

    /**
     * Удаляет класс over у всех элементов списка.
     */
    public void noFocused() {
        for (MenuItem item : getItems()) {
            item.blurItem();
        }
        focusedIndex = -1;
    }

    /**
     * Добавляет элемент в конец списка
     * @param item элемент
     */
    protected void addItem(MenuItem item) {
        root.add(item);
    }

    /**
     * Удаляет элемент
     * @param item элемент
     */
    protected void removeItem(MenuItem item) {
        root.remove(item);
    }

    /**
     * Возвращает индекс элемента следующего за выбранным, который может быть выбран.
     * Индекс должен находится в пределах списка или быть равным -1.
     * @return индекс следующего элемента, -1 если таких элементов нет.
     */
    protected int getNext() {
        return getNext(focusedIndex);
    }

    private int getNext(int start) {
        ArrayList<? extends MenuItem> items = getItems();

        int i = start + 1;
        if (i < 0) {
            return -1;
        }
        while (i < items.size() && items.get(i).shouldBeSkipped()) {
            i++;
        }
        if (i == items.size()) {
            i = 0;
            while (i < start && items.get(i).shouldBeSkipped()) {
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
        return getPrevious(focusedIndex);
    }

    private int getPrevious(int start) {
        ArrayList<? extends MenuItem> items = getItems();
        if (start == -1) {
            return getLast();
        }
        int i = start - 1;
        if (i >= items.size()) {
            return -1;
        }
        while (i >= 0 && items.get(i).shouldBeSkipped()) {
            i--;
        }
        if (i == -1) {
            i = items.size() - 1;
            while (i > start && items.get(i).shouldBeSkipped()) {
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
        return getPrevious(getItems().size());
    }

    /**
     * Фокусирует элемент находящийся на заданной позиции.
     * @param index позиция
     */
    protected void focus(int index) {
        focusedIndex = index;
        if (index != -1) {
            getItems().get(index).focus();
        }
    }

    public MenuItem getFocused() {
        if (focusedIndex != -1) {
            return getItems().get(focusedIndex);
        } else {
            return null;
        }
    }

    /**
     * Устанавливает виджет относительно которого список будет отображаться
     * @param widget родительский виджет
     */
    public void setRelativeWidget(Widget widget) {
        if (relativeWidget != null) {
            popup.removeAutoHidePartner(widget.getElement());
        }
        relativeWidget = widget;
        if (widget != null) {
            popup.addAutoHidePartner(widget.getElement());
            popup.getElement().getStyle().setProperty("borderTop", "0px");
        } else {
            popup.getElement().getStyle().setProperty("borderTop", "1px");
        }
    }

    public void addAutoHidePartner(Element e) {
        popup.addAutoHidePartner(e);
    }

    public void removeAutoHidePartner(Element e) {
        popup.removeAutoHidePartner(e);
    }

    public Widget getRelativeWidget() {
        return relativeWidget;
    }

    /**
     * Выполняет операции перед тем как показать список
     */
    private void beforeShow() {
        if (resizeRegistration == null) {
            resizeRegistration = Window.addResizeHandler(resizeHandler);
        }
    }

    /**
     * Показывает меню под родителем.
     */
    protected void showUnderParent() {
        if (relativeWidget != null && relativeWidget.isAttached()) {
            beforeShow();
            popup.getElement().getStyle().setProperty("minWidth", relativeWidget.getOffsetWidth() - Constants.BORDER_RADIUS * 2 + "px");
            root.getElement().getStyle().setProperty("minWidth", relativeWidget.getOffsetWidth() - Constants.BORDER_RADIUS * 2 + "px");

            popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    int x = relativeWidget.getAbsoluteLeft() + Constants.BORDER_RADIUS;
                    int y = relativeWidget.getAbsoluteTop() + relativeWidget.getOffsetHeight() + 1;
                    popup.setPopupPosition(x, y);
                }
            });
        }
    }


    /**
     * Показан ли список
     */
    public boolean isShowing() {
        return popup.isShowing();
    }

    /**
     * При показе списка начинаем следить за изменением размера окна браузера.
     */
    public void show() {
        beforeShow();
        popup.show();
    }

    /**
     * Скрывает список
     */
    public void hide() {
        popup.hide();
    }


    /**
     * Предпросмотр событий при открытом меню. Обрабатываются события колеса мыши и
     * навигация клавиатурой.
     * @param event событие
     */
    protected void onPreview(Event.NativePreviewEvent event) {
        Event nativeEvent = Event.as(event.getNativeEvent());

        if (nativeEvent.getTypeInt() == Event.ONMOUSEWHEEL) {
            if (!mouseOver) {
                popup.hide();
            }
        } else if (nativeEvent.getTypeInt() == Event.ONKEYDOWN) {
            switch (nativeEvent.getKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    keyDown(event);
                    break;
                case KeyCodes.KEY_UP:
                    keyUp(event);
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
            }
        }
    }

    /**
     * Добавляет стиль к списку
     * @param style стиль
     */
    public void addStyleName(String style) {
        popup.addStyleName(style);
    }

    /**
     * Возвращает логическое представление списка
     */
    abstract ArrayList<? extends MenuItem> getItems();

    protected abstract void keyDown(Event.NativePreviewEvent event);
    protected abstract void keyUp(Event.NativePreviewEvent event);
    protected abstract void keyLeft(Event.NativePreviewEvent event);
    protected abstract void keyRight(Event.NativePreviewEvent event);
    protected abstract void keyEnter(Event.NativePreviewEvent event);
}
