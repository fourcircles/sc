package kz.arta.synergy.components.client.menu.filters;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.events.FilterUpdateEvent;

/**
 * User: vsl
 * Date: 06.08.14
 * Time: 14:17
 *
 * Текстовый фильтр для списка.
 * Содержит префиксный фильтр и фильтр который проверяет на содержание текста.
 */
abstract public class ListTextFilter implements ListFilter {
    /**
     * Текст
     */
    protected String text;
    private EventBus bus;

    /**
     * Создает фильтр, который проверяет начинается ли текст элемента списка с заданного текста
     */
    public static ListTextFilter createPrefixFilter() {
        return new ListTextFilter() {
            @Override
            public boolean include(DropDownList.Item item) {
                if (this.text == null || this.text.isEmpty()) {
                    return true;
                }
                String itemText = item.getText();
                if (itemText == null || this.text.length() > itemText.length()) {
                    return false;
                }
                return itemText.toLowerCase().substring(0, this.text.length()).equals(this.text.toLowerCase());
            }
        };
    }

    /**
     * Создает фильтр, который проверяет содержит ли элемент списка текст
     */
    public static ListTextFilter createContainsFilter() {
        return new ListTextFilter() {
            @Override
            public boolean include(DropDownList.Item item) {
                if (this.text == null || this.text.isEmpty()) {
                    return true;
                }
                if (item.getText() == null) {
                    return false;
                }
                return item.getText().toLowerCase().contains(this.text.toLowerCase());
            }
        };
    }
    
    public ListTextFilter() {
        bus = new SimpleEventBus();
    }

    public ListTextFilter(String text) {
        this();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldText = this.text;
        this.text = text;
        if (oldText != null && !oldText.equals(text)) {
            bus.fireEventFromSource(new FilterUpdateEvent(), this);
        }
    }

    @Override
    public HandlerRegistration addFilterUpdateHandler(FilterUpdateEvent.Handler handler) {
        return bus.addHandlerToSource(FilterUpdateEvent.TYPE, this, handler);
    }
}
