package kz.arta.synergy.components.client.table.column;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.table.events.CellEditEvent;
import kz.arta.synergy.components.client.table.events.TreeTableItemEvent;

/**
* User: vsl
* Date: 08.10.14
* Time: 11:02
 *
 * Виджет для ячейки отображающей ячейку дерева таблицы.
*/
public class TreeTableWidget<T extends TreeTableItem<T>> extends EditableText<T> {
    /**
     * Отступ для каждого уровня
     */
    private static final int OFFSET_PER_LEVEL = 24;

    /**
     * Отступ у корневых элементов
     */
    private static final int INITIAL_PADDING = 12;

    private static final String LOADER_ID = "circularG";

    /**
     * Треугольник
     */
    private Image image;

    /**
     * При смене объекта хендлер на прежний объект надо удалять,
     * для этого существует это поле
     */
    private HandlerRegistration handlerRegistration;
    private TreeTableItemEvent.Handler<T> handler;

    /**
     * Лоадер
     */
    private FlowPanel loader;

    private final FlowPanel widgets;

    /**
     * Можно ли редактировать ячейку
     */
    private boolean isEditable = false;

    public TreeTableWidget(T object, TreeColumn<T> column, boolean isEditable, EventBus bus) {
        super(column, object, bus);

        widgets = new FlowPanel();
        widgets.setStyleName(SynergyComponents.getResources().cssComponents().treeTableItem());

        this.isEditable = isEditable;

        image = GWT.create(Image.class);
        image.setResource(ImageResources.IMPL.nodeClosed16());

        /*обнуляем здесь, чтобы корректно был создан handler*/
        this.object = null;
        update(object);

        root.setWidget(widgets);
        widgets.add(image);
        widgets.add(label);

        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                    imageClick();
                }
                event.stopPropagation();
            }
        });
    }

    @Override
    public void onBrowserEvent(Event event) {
        int type = event.getTypeInt();
        int keyCode = event.getKeyCode();
        if (isEditable) {
            if (isEditing) {
                editEvent(event);
            } else if (type == Event.ONKEYDOWN && (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_F2)) {
                edit();
            }
        }
    }

    /**
     * Заменяет элемент ввода на текст
     */
    protected void unEdit() {
        isEditing = false;
        root.setWidget(widgets);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            getElement().getStyle().setPaddingRight(14, Style.Unit.PX);
        } else {
            getElement().getStyle().setPaddingLeft(14, Style.Unit.PX);
        }
        root.setFocus(true);
    }

    /**
     * Создает лоадер, который заменяет треугольник
     *
     * @return лоадер
     */
    private FlowPanel createLoader() {
        FlowPanel loader = new FlowPanel();
        loader.setStyleName(SynergyComponents.getResources().cssComponents().loader());

        FlowPanel first = new FlowPanel();
        first.getElement().setId(LOADER_ID);
        loader.add(first);

        for (int i = 1; i <= 8; i++) {
            FlowPanel dot = new FlowPanel();
            dot.setStyleName(SynergyComponents.getResources().cssComponents().circularG());
            dot.getElement().setId(LOADER_ID + "_" + i);
            loader.add(dot);
        }
        return loader;
    }

    /**
     * Показывает лоадер вместо треугольника
     */
    private void showLoader() {
        if (loader == null) {
            loader = createLoader();
        }
        image.removeFromParent();
        widgets.insert(loader, 0);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            loader.getElement().getStyle().setMarginRight(getMargin(object), Style.Unit.PX);
        } else {
            loader.getElement().getStyle().setMarginLeft(getMargin(object), Style.Unit.PX);
        }
    }

    /**
     * Скрывает лоадер
     */
    private void hideLoader() {
        loader.removeFromParent();
        widgets.insert(image, 0);
    }

    /**
     * Клик по треугольнику
     */
    private void imageClick() {
        if (object.isOpen()) {
            object.close();
        } else {
            showLoader();
            object.addTreeTableHandler(new TreeTableItemEvent.Handler<T>() {
                @Override
                public void onClose(TreeTableItemEvent<T> event) {
                    // do nothing
                }

                @Override
                public void onOpen(TreeTableItemEvent<T> event) {
                    hideLoader();
                }
            });
            object.open();
        }
    }

    /**
     * Находит глубину элемента
     * @param item элемент
     * @return его глубина
     */
    private int getDepth(TreeTableItem item) {
        TreeTableItem cur = item;
        int depth = 0;
        while (cur.getParent() != null) {
            cur = cur.getParent();
            depth++;
        }
        return depth;
    }

    /**
     * Находит отступ для объекта в зависимости от его глубины
     * @param item объект
     * @return его отступ
     */
    private int getMargin(TreeTableItem item) {
        return INITIAL_PADDING + getDepth(item) * OFFSET_PER_LEVEL;
    }

    /**
     * Обновляет вид виджета до состояния объекта.
     * Обычно вызывается при смене объекта или при его изменении.
     */
    private void update() {
        String itemText = column.getValue(object);
        String newText = itemText == null ? "" : itemText;

        if (!newText.equals(label.getText())) {
            label.setText(newText);
        }

        if (object.hasChildren()) {
            label.removeStyleName(SynergyComponents.getResources().cssComponents().mainText());
            label.setStyleName(SynergyComponents.getResources().cssComponents().mainTextBold());
            image.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            image.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            if (object.isOpen()) {
                image.setResource(ImageResources.IMPL.nodeOpen16());
            } else {
                image.setResource(ImageResources.IMPL.nodeClosed16());
            }
        } else {
            label.removeStyleName(SynergyComponents.getResources().cssComponents().mainTextBold());
            label.setStyleName(SynergyComponents.getResources().cssComponents().mainText());
            image.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
            image.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }

        int margin = getMargin(object);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            image.getElement().getStyle().setMarginRight(margin, Style.Unit.PX);
        } else {
            image.getElement().getStyle().setMarginLeft(margin, Style.Unit.PX);
        }
    }

    /**
     * Возвращает хендлер клика
     */
    private TreeTableItemEvent.Handler<T> getHandler() {
        if (handler == null) {
            handler = new TreeTableItemEvent.Handler<T>() {
                @Override
                public void onClose(TreeTableItemEvent<T> event) {
                    image.setResource(ImageResources.IMPL.nodeClosed16());
                }
                @Override
                public void onOpen(TreeTableItemEvent<T> event) {
                    image.setResource(ImageResources.IMPL.nodeOpen16());
                }
            };
        }
        return handler;
    }

    /**
     * Изменяет отображаемый объект и вид виджета.
     * @param item новый объект
     */
    public void update(T item) {
        if (this.object != item) {
            this.object = item;
            if (handlerRegistration != null) {
                handlerRegistration.removeHandler();
            }
            handlerRegistration = item.addTreeTableHandler(getHandler());
        }
        update();
    }
}
