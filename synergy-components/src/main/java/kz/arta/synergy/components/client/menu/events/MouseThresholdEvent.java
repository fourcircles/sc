package kz.arta.synergy.components.client.menu.events;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: vsl
 * Date: 29.10.14
 * Time: 12:22
 */
public class MouseThresholdEvent extends GwtEvent<MouseThresholdEvent.Handler> {
    public static final Type<Handler> TYPE = new Type<Handler>();

    static Map<Object, Config> objects = new HashMap<Object, Config>();

    private static MouseMoveHandler mouseMoveHandler = new MouseMoveHandler() {
        @Override
        public void onMouseMove(MouseMoveEvent event) {
            Object source = event.getSource();
            Config config = objects.get(source);
            if (config != null && config.update(event.getClientX(), event.getClientY())) {
                MouseThresholdEvent thresholdEvent = new MouseThresholdEvent();
                for (EventBus bus : config.buses) {
                    bus.fireEventFromSource(thresholdEvent, source);
                }
            }
        }
    };

    public MouseThresholdEvent() {
    }

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        handler.onMouseThreshold(this);
    }

    public static interface Handler extends EventHandler {
        void onMouseThreshold(MouseThresholdEvent event);
    }

    public static HandlerRegistration register(EventBus bus, final Widget source, Handler handler) {
        if (!objects.containsKey(source)) {
            HandlerRegistration dom = source.addDomHandler(mouseMoveHandler, MouseMoveEvent.getType());
            Config config = new Config(dom);
            objects.put(source, config);
        }

        final HandlerRegistration eventRegistration = bus.addHandlerToSource(TYPE, source, handler);
        final Config config = objects.get(source);
        config.attachedHandlers++;
        config.addBus(bus);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                eventRegistration.removeHandler();
                config.attachedHandlers--;
                if (config.attachedHandlers == 0) {
                    config.domRegistration.removeHandler();
                    objects.remove(source);
                }
            }
        };
    }


    static class Config {
        private static final int DEFAULT_THRESHOLD = 10;
        private boolean started;
        private int x;
        private int y;

        private int attachedHandlers = 0;
        private HandlerRegistration domRegistration;

        private Set<EventBus> buses = new HashSet<EventBus>();

        public Config(HandlerRegistration domRegistration) {
            this.domRegistration = domRegistration;
            this.started = false;

        }

        public void addBus(EventBus bus) {
            buses.add(bus);
        }

        public boolean update(int x, int y) {
            boolean result = false;
            if (!started) {
                started = true;
            } else {
                int distance = Math.abs(this.x - x) + Math.abs(this.y - y);
                if (distance > DEFAULT_THRESHOLD) {
                    result = true;
                }
            }
            this.x = x;
            this.y = y;

            return result;
        }
    }
}
