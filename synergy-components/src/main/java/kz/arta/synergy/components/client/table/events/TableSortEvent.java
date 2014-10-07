package kz.arta.synergy.components.client.table.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.table.column.ArtaColumn;

import java.util.*;

/**
 * User: vsl
 * Date: 04.09.14
 * Time: 15:35
 *
 * Событие сортировки таблицы.
 *
 * Возможно улучшение до содержания нескольких столбцов как здесь:
 * {@link com.google.gwt.user.cellview.client.ColumnSortList}
 */
public class TableSortEvent<T> extends GwtEvent<TableSortEvent.Handler<T>> {
    public final static Type<Handler<?>> TYPE = new Type<Handler<?>>();

    private ArtaColumn<T> column;
    private boolean isAscending;

    public TableSortEvent(ArtaColumn<T> column, boolean isAscending) {
        this.column = column;
        this.isAscending = isAscending;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Type<Handler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    protected void dispatch(Handler<T> handler) {
        handler.onSort(this);
    }

    public static interface Handler<V> extends EventHandler {
        void onSort(TableSortEvent<V> event);
    }

    public static class ListHandler<V> implements Handler<V> {
        private List<V> list;
        private Map<ArtaColumn<V>, Comparator<V>> comparators =
                new HashMap<ArtaColumn<V>, Comparator<V>>();

        public ListHandler(List<V> list) {
            this.list = list;
        }

        @Override
        public void onSort(TableSortEvent<V> event) {
            ArtaColumn<V> column = event.getColumn();
            if (column == null) {
                return;
            }
            final Comparator<V> comparator = comparators.get(column);
            if (comparator == null) {
                return;
            }
            if (event.isAscending()) {
                Collections.sort(list, comparator);
            } else {
                Collections.sort(list, new Comparator<V>() {
                    @Override
                    public int compare(V o1, V o2) {
                        return -comparator.compare(o1, o2);
                    }
                });
            }
        }

        public void setComparator(ArtaColumn<V> column, Comparator<V> comparator) {
            comparators.put(column, comparator);
        }

        public void setList(List<V> list) {
            assert list != null;
            this.list = list;
        }
    }

    public ArtaColumn<T> getColumn() {
        return column;
    }

    public boolean isAscending() {
        return isAscending;
    }
}
