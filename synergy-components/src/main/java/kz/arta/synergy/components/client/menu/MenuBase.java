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

import java.util.List;

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

    private int getNext(int start, int end) {
        List<? extends MenuItem> items = getItems();

        int i = start;
        if (i < 0) {
            return -1;
        }
        while (i < items.size() && i < end &&
                items.get(i).shouldBeSkipped()) {
            i++;
        }
        if (i >= start && i < end) {
            return i;
        } else {
            return -1;
        }
    }

    private int getNext(int start) {
        List<? extends MenuItem> items = getItems();
        int beforeEnd = getNext(start + 1, items.size());
        if (beforeEnd == -1) {
            return getNext(0, start + 1);
        } else {
            return beforeEnd;
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

    private int getPrevious(int start, int end) {
        List<? extends MenuItem> items = getItems();

        int i = start;
        if (i >= items.size()) {
            return -1;
        }
        while (i >= 0 && i > end && items.get(i).shouldBeSkipped()) {
            i--;
        }
        if (i <= start && i > end) {
            return i;
        } else {
            return -1;
        }
    }

    private int getPrevious(int start) {
        int beforeStart = getPrevious(start - 1, -1);
        if (beforeStart == -1) {
            return getPrevious(getItems().size() - 1, start - 1);
        } else {
            return beforeStart;
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
            popup.getElement().getStyle().setProperty("borderTopStyle", "none");
        } else {
            popup.getElement().getStyle().setProperty("borderTopStyle", "solid");
        }
    }

    public void addAutoHidePartner(Element e) {
        popup.addAutoHidePartner(e);
    }

    public void removeAutoHidePartner(Element e) {
        popup.removeAutoHidePartner(e);
    }

    public boolean hasRelativeWidget() {
        return relativeWidget != null && relativeWidget.isAttached();
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
        if (hasRelativeWidget()) {
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
                default:
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
    abstract List<? extends MenuItem> getItems();

    protected abstract void keyDown(Event.NativePreviewEvent event);
    protected abstract void keyUp(Event.NativePreviewEvent event);
    protected abstract void keyLeft(Event.NativePreviewEvent event);
    protected abstract void keyRight(Event.NativePreviewEvent event);
    protected abstract void keyEnter(Event.NativePreviewEvent event);
}
