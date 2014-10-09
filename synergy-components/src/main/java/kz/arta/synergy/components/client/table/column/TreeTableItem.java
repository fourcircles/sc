package kz.arta.synergy.components.client.table.column;

import com.google.gwt.event.shared.HandlerRegistration;
import kz.arta.synergy.components.client.table.events.TreeTableItemEvent;

import java.util.List;

/**
 * User: vsl
 * Date: 07.10.14
 * Time: 17:52
 *
 * Интерфейс, который нужно использовать всем классам, которые надо отображать в дереве таблице.
 *
 * При открытии и закрытии объекта надо создавать события {@link kz.arta.synergy.components.client.table.events.TreeTableItemEvent}
 * соответствующего типа.
 */
public interface TreeTableItem<T extends TreeTableItem<T>> {
    T getParent();

    List<T> getChildren();

    boolean hasChildren();

    /**
     * @return открыт ли объект
     */
    boolean isOpen();

    /**
     * Открыть объект.
     */
    void open();

    /**
     * Закрыть объект.
     */
    void close();

    HandlerRegistration addTreeTableHandler(TreeTableItemEvent.Handler<T> handler);
}
