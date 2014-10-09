package kz.arta.synergy.components.client.table;

import com.google.gwt.view.client.ListDataProvider;
import kz.arta.synergy.components.client.table.column.TreeTableItem;
import kz.arta.synergy.components.client.table.events.TreeTableItemEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * User: vsl
 * Date: 08.10.14
 * Time: 11:44
 *
 * Предоставляет данные для дерева-таблицы.
 * При закрытии-открытии объекта добавляет/удаляет объекты внутри него в таблицу.
 */
public class TreeTableProvider<T extends TreeTableItem<T>> extends ListDataProvider<T> {

    /**
     * Хендлер для закрытия-открытия объектов.
     */
    private TreeTableItemEvent.Handler<T> handler;

    /**
     * Добавленные объекты
     */
    private Set<TreeTableItem> items = new HashSet<TreeTableItem>();

    public TreeTableProvider() {
        this.handler = new TreeTableItemEvent.Handler<T>() {
            @Override
            public void onClose(TreeTableItemEvent<T> event) {
                for (T child : event.getItem().getChildren()) {
                    removeItemInner(child);
                }
                flush();
            }
            @Override
            public void onOpen(TreeTableItemEvent<T> event) {
                int index = getList().indexOf(event.getItem());
                for (T child : event.getItem().getChildren()) {
                    addItemInner(index + 1, child);
                }
                flush();
            }
        };
    }

    /**
     * Возвращает корневой элемент объекта
     * @param item элемент
     * @return его корень
     */
    private T getRoot(T item) {
        T root = item;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    /**
     * Добавляет элемент.
     * Если элемент не является корневым, добавляет его корень.
     * @param item элемент
     */
    public void addItem(T item) {
        final T root = getRoot(item);
        addItemInner(getList().size(), root);
    }

    /**
     * Удаляет элемент. Если он открыт - удаляет детей и т.д.
     * @param item элемент
     */
    private void removeItemInner(T item) {
        if (item.getChildren() == null) {
            return;
        }

        for (T child : item.getChildren()) {
            removeItemInner(child);
        }

        getList().remove(item);
    }

    /**
     * Добавляет элемент на заданную позицию.
     * Если он открыт - добавляет его детей и т.д.
     * @param index позиция
     * @param item элемент
     * @return конечная позиция добавленных элементов
     */
    private int addItemInner(int index, T item) {
        int curIndex = index;

        getList().add(curIndex++, item);
        if (!items.contains(item)) {
            item.addTreeTableHandler(handler);
            items.add(item);
        }

        if (item.isOpen() && item.getChildren() != null) {
            for (T child : item.getChildren()) {
                curIndex = addItemInner(curIndex, child);
            }
        }

        return curIndex;
    }
}
