package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: vsl
 * Date: 06.08.14
 * Time: 13:58
 */
public interface HasFilterUpdateHandlers {
    HandlerRegistration addFilterUpdateHandler(FilterUpdateEvent.Handler handler);
}
