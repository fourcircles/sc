package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 17:54
 */
public interface SelectionEventHandler<V> extends EventHandler {
    void onSelection(SelectionEvent<V> event);
}
