package kz.arta.synergy.components.client.dagger;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.dagger.events.DaggerItemSelectionEvent;
import kz.arta.synergy.components.client.menu.events.FilterUpdateEvent;
import kz.arta.synergy.components.client.menu.filters.ListFilter;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 20.10.14
 * Time: 11:34
 *
 * Выпадающий список
 */
public class DaggerDropDownList<V> extends DaggerMenu<V> {
    private DaggerItem<V> selectedItem;
    protected ValueChangeHandler<Boolean> selectionHandler;

    private ListFilter filter;
    private HandlerRegistration filterRegistration;
    private final ArtaScrollPanel scroll;

    public DaggerDropDownList() {
        super();

        scroll = new ArtaScrollPanel();
        scroll.setWidget(root);
        popup.setWidget(scroll);

        popup.setStyleName(SynergyComponents.getResources().cssComponents().contextMenu());
    }

    @Override
    protected ValueChangeHandler<Boolean> getSelectionHandler(DaggerItem<V> newItem) {
        if (selectionHandler == null) {
            selectionHandler = new ValueChangeHandler<Boolean>() {
                @Override
                @SuppressWarnings({"unchecked"})
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    DaggerItem<V> item = (DaggerItem) event.getSource();
                    hide();
                    item.setValue(true, false);

                    if (item != selectedItem) {
                        if (selectedItem != null) {
                            selectedItem.setValue(false, false);
                        }
                        selectedItem = item;
                        fireEvent(new DaggerItemSelectionEvent<V>(selectedItem, event.getValue()));
                    }
                }
            };
        }
        return selectionHandler;
    }

    /**
     * Задать фильтр для списка
     * @param filter фильтр
     */
    public void setFilter(ListFilter filter) {
        this.filter = filter;
        if (filterRegistration != null) {
            filterRegistration.removeHandler();
        }
        filterRegistration = filter.addFilterUpdateHandler(new FilterUpdateEvent.Handler() {
            @Override
            public void onFilterUpdate(FilterUpdateEvent event) {
                applyFilter();
            }
        });
        applyFilter();
    }

    /**
     * Определяет высоту из количества добавленых элементов
     * @return высота
     */
    protected int getHeight() {
        int cnt = root.getWidgetCount();
        return Math.min(cnt * 32 + Math.max(cnt - 1, 0) * 2, Constants.LIST_MAX_HEIGHT);
    }

    /**
     * Применяет заданный фильтр
     */
    private void applyFilter() {
        if (filter != null) {
            for (DaggerItem<V> item : items) {
                if (filter.include(item)) {
                    root.add(item);
                } else {
                    root.remove(item);
                }
            }
        } else {
            for (DaggerItem<V> item : items) {
                root.add(item);
            }
        }
        // сфокусированный элемент должен быть виден всегда
        scrollToFocused();

        // высота может измениться
        popup.getElement().getStyle().setHeight(getHeight(), Style.Unit.PX);
    }

    /**
     * Удалить текущий фильтр
     */
    public void clearFilter() {
        if (filterRegistration != null) {
            filterRegistration.removeHandler();
            filterRegistration = null;
        }
        this.filter = null;
        applyFilter();
    }

    @Override
    protected void setPopupBeforeShow(Widget relativeWidget) {
        super.setPopupBeforeShow(relativeWidget);
        root.getElement().getStyle().setProperty("maxWidth", relativeWidget.getOffsetWidth() - Constants.BORDER_RADIUS * 2 + "px");
        scroll.getElement().getStyle().setProperty("maxWidth", relativeWidget.getOffsetWidth() - Constants.BORDER_RADIUS * 2 + "px");

        scroll.getElement().getStyle().setHeight(getHeight(), Style.Unit.PX);

        if (selectedItem != null) {
            selectedItem.getElement().scrollIntoView();
        }
    }

    /**
     * Возвращает следующий элемент в отфильтрованном списке.
     * Поиск начинается с позиции start + 1 и заканчивается на start.
     */
    private DaggerItem<V> nextIncluded(int start) {
        int index = start + 1;
        while (index < items.size() && !isIncluded(index)) {
            index++;
        }

        if (index < items.size()) {
            return items.get(index);
        } else {
            index = 0;
            while (index <= start && index < items.size() && !isIncluded(index)) {
                index++;
            }
            if (index <= start && index < items.size()) {
                return items.get(index);
            } else {
                return null;
            }
        }
    }

    /**
     * Возвращает предыдущий элемент в отфильтрованном списке.
     * Поиск начинается с позиции start - 1 и заканчивается на start.
     */
    private DaggerItem<V> previousIncluded(int start) {
        int index = start - 1;
        while (index >= 0 && !isIncluded(index)) {
            index--;
        }
        if (index >= 0) {
            return items.get(index);
        } else {
            index = items.size() - 1;
            while (index >= start && index >= 0 && !isIncluded(index)) {
                index--;
            }
            if (index >= start && index >= 0) {
                return items.get(index);
            } else {
                return null;
            }
        }
    }

    private boolean isIncluded(int index) {
        return filter.include(items.get(index));
    }

    @Override
    protected void focusNext() {
        DaggerItem<V> nextItem;
        if (focusedIndex == -1) {
            if (selectedItem != null) {
                nextItem = nextIncluded(items.indexOf(selectedItem));
            } else {
                nextItem = nextIncluded(-1);
            }
        } else {
            nextItem = nextIncluded(focusedIndex);
        }
        nextItem.setFocused(true, true);
        scrollToFocused();
    }

    @Override
    protected void focusPrevious() {
        DaggerItem<V> previousItem;
        if (focusedIndex == -1) {
            if (selectedItem != null) {
                previousItem = previousIncluded(items.indexOf(selectedItem));
            } else {
                previousItem = previousIncluded(items.size());
            }
        } else {
            previousItem = previousIncluded(focusedIndex);
        }
        previousItem.setFocused(true, true);
        scrollToFocused();
    }

    @Override
    protected void focusFirst() {
        DaggerItem<V> first = nextIncluded(-1);
        if (first != null) {
            first.setFocused(true, true);
        }
        scrollToFocused();
    }

    @Override
    protected void focusLast() {
        DaggerItem<V> last = previousIncluded(items.size());
        if (last != null) {
            last.setFocused(true, true);
        }
        scrollToFocused();
    }

    private void scrollToFocused() {
        if (focusedIndex != -1) {
            items.get(focusedIndex).getElement().scrollIntoView();
        }
    }

}
