package kz.arta.synergy.components.client.stack.events;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: vsl
 * Date: 20.08.14
 * Time: 16:21
 */
public interface HasStackOpenHandlers {
    HandlerRegistration addStackOpenHandler(StackOpenEvent.Handler handler);
}
