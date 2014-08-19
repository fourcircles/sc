package kz.arta.synergy.components.client.tabs.events;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import kz.arta.synergy.components.client.tabs.Tab;

/**
 * User: vsl
 * Date: 08.08.14
 * Time: 13:55
 *
 * Событие выбора вкладки
 */
public class TabSelectionEvent extends GwtEvent<TabSelectionEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onTabSelection(this);
    }

    public static interface Handler extends EventHandler {
        void onTabSelection(TabSelectionEvent event);
    }

    /**
     * Вкладка
     */
    private Tab tab;

    /**
     * Открыть ли вкладку после добавления
     */
    private boolean openAfter;

    public TabSelectionEvent(Tab tab) {
        this(tab, true);
    }

    public TabSelectionEvent(Tab tab, boolean openAfter) {
        this.tab = tab;
        this.openAfter = openAfter;
    }

    public Tab getTab() {
        return tab;
    }

    public boolean isOpenAfter() {
        return openAfter;
    }

    public static HandlerRegistration register(EventBus bus, Handler handler) {
        return bus.addHandler(TYPE, handler);
    }
}
