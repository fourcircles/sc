package kz.arta.synergy.components.client.input;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import kz.arta.synergy.components.client.input.events.TextChangedEvent;

/**
 * User: vsl
 * Date: 29.07.14
 * Time: 18:07
 *
 * Класс публикует событие TextChangedEvent при изменении текста
 */
public class InputWithEvents extends TextInput {
    /**
     * EventBus на который публикуется событие
     */
    private EventBus bus;

    /**
     * Старое значения текста
     */
    private String oldText = "";

    public InputWithEvents(final EventBus bus) {
        super(true);
        this.bus = bus;

        addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                textMaybeChanged(getText());
            }
        });
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONPASTE) {
            textMaybeChanged(getText());
        }
    }

    /**
     * Проверяет изменился ли текст, если изменился, то
     * создает соответствующее событие
     * @param newText новый текст
     */
    private void textMaybeChanged(String newText) {
        if (!oldText.equals(newText)) {
            bus.fireEventFromSource(new TextChangedEvent(oldText, newText), this);
            oldText = newText;
        }
    }

    @Override
    public void setText(String text) {
        setText(text, true);
    }

    public void setText(String text, boolean fireEvents) {
        super.setText(text);
        if (fireEvents) {
            textMaybeChanged(getText());
        }
    }

    public EventBus getBus() {
        return bus;
    }

    public void setBus(EventBus bus) {
        this.bus = bus;
    }

}
