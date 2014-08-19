package kz.arta.synergy.components.client.tabs.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: vsl
 * Date: 11.08.14
 * Time: 15:51
 */
public interface HasTabHandlers {
    HandlerRegistration addTabSelectionHandler(TabSelectionEvent.Handler handler);
    HandlerRegistration addTabCloseHandler(TabCloseEvent.Handler handler);
}
