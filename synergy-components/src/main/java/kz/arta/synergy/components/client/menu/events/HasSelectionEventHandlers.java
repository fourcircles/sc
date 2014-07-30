package kz.arta.synergy.components.client.menu.events;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 17:57
 */
public interface HasSelectionEventHandlers<V> {
    void addSelectionHandler(SelectionEvent.Handler<V> handler);
}
