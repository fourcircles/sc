package kz.arta.synergy.components.client.table.column;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.table.events.TreeTableItemEvent;

/**
* User: vsl
* Date: 08.10.14
* Time: 11:02
 *
 * Виджет для ячейки отображающей ячейку дерева таблицы.
*/
public class TreeTableWidget<T extends TreeTableItem<T>> extends Composite {
    /**
     * Отступ для каждого уровня
     */
    private static final int OFFSET_PER_LEVEL = 24;

    /**
     * Отступ у корневых элементов
     */
    private static final int INITIAL_PADDING = 12;

    /**
     * Треугольник
     */
    private Image image;

    /**
     * Элемент текста
     */
    private InlineLabel label;

    /**
     * Объект, который отображается виджетом
     */
    private T item;

    /**
     * При смене объекта хендлер на прежний объект надо удалять,
     * для этого существует это поле
     */
    private HandlerRegistration handlerRegistration;
    private TreeTableItemEvent.Handler<T> handler;

    /**
     * Столбец
     */
    private TreeColumn<T> column;

    public TreeTableWidget(T item, TreeColumn<T> column) {
        FlowPanel root = new FlowPanel();
        initWidget(root);

        root.setStyleName(SynergyComponents.getResources().cssComponents().treeTableItem());

        this.column = column;

        image = GWT.create(Image.class);
        image.setResource(ImageResources.IMPL.nodeClosed16());

        label = GWT.create(InlineLabel.class);
        label.setStyleName(SynergyComponents.getResources().cssComponents().mainText());
        label.setText("");

        update(item);

        root.add(image);
        root.add(label);

        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                imageClick();
                event.stopPropagation();
            }
        });
    }

    /**
     * Клик по треугольнику
     */
    private void imageClick() {
        if (item.isOpen()) {
            item.close();
        } else {
            item.open();
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
        String itemText = column.getText(item);
        String newText = itemText == null ? "" : itemText;

        if (!newText.equals(label.getText())) {
            label.setText(newText);
        }

        if (item.hasChildren()) {
            label.removeStyleName(SynergyComponents.getResources().cssComponents().mainText());
            label.setStyleName(SynergyComponents.getResources().cssComponents().mainTextBold());
            image.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            image.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            if (item.isOpen()) {
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

        int margin = getMargin(item);
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
        if (this.item != item) {
            this.item = item;
            if (handlerRegistration != null) {
                handlerRegistration.removeHandler();
            }
            handlerRegistration = item.addTreeTableHandler(getHandler());
        }
        update();
    }
}
