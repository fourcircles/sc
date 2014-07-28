package kz.arta.synergy.components.client.input.tags.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 17:12
 */
public interface HasTagRemoveEventHandler extends HasHandlers {
    public HandlerRegistration addTagRemoveHandler(TagRemoveEvent.TagRemoveEventHandler handler);
}
