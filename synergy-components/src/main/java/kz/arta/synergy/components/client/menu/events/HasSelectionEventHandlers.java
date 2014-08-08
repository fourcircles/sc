package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 17:57
 */
public interface HasSelectionEventHandlers<V> {
    HandlerRegistration addSelectionHandler(SelectionEvent.Handler<V> handler);
}
