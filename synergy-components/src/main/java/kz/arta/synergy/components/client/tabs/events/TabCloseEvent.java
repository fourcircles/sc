package kz.arta.synergy.components.client.tabs.events;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import kz.arta.synergy.components.client.tabs.Tab;

/**
 * User: vsl
 * Date: 08.08.14
 * Time: 14:20
 *
 * Событие закрытия вкладки
 */
public class TabCloseEvent extends GwtEvent<TabCloseEvent.Handler> {
    private static Type<Handler> TYPE;

    /**
     * Вкладка
     */
    private Tab tab;

    public TabCloseEvent(Tab tab) {
        this.tab = tab;
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    public Type<Handler> getAssociatedType() {
        return getType();
    }

    protected void dispatch(Handler handler) {
        handler.onTabClose(this);
    }

    public static interface Handler extends EventHandler {
        void onTabClose(TabCloseEvent event);
    }

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public static HandlerRegistration register(EventBus bus, Handler handler) {
        return bus.addHandler(getType(), handler);
    }
}
