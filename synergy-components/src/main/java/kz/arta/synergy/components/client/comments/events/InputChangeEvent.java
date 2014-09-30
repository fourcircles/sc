package kz.arta.synergy.components.client.comments.events;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * User: vsl
 * Date: 29.09.14
 * Time: 10:19
 *
 * Событие "input"
 */
public class InputChangeEvent extends GwtEvent<InputChangeEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onInputChange(this);
    }

    public static interface Handler extends EventHandler {
        void onInputChange(InputChangeEvent event);
    }

    private static EventBus inputBus = new SimpleEventBus();

    /**
     * Добавляет хэндлер на событие "input" элемента. Средствами GWT этого сделать нельзя.
     * @param element элемент
     * @param handler хэндлер
     */
    public static void addInputHandler(Element element, Handler handler) {
        addInputEvent(element);
        inputBus.addHandlerToSource(InputChangeEvent.TYPE, element, handler);
    }

    private static void fireInputEvent(Element element) {
        inputBus.fireEventFromSource(new InputChangeEvent(), element);
    }

    private static native void addInputEvent(Element element) /*-{
        element.addEventListener("input", function() {
            @kz.arta.synergy.components.client.comments.events.InputChangeEvent::fireInputEvent(Lcom/google/gwt/dom/client/Element;)(element);
        })
    }-*/;

}
