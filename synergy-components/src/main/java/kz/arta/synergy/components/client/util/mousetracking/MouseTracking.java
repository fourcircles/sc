package kz.arta.synergy.components.client.util.mousetracking;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;

/**
 * User: vsl
 * Date: 29.01.15
 * Time: 14:24
 */
public class MouseTracking {
    private static final int STEP_DURATION = 3000;
    private static final int IDLE_TIME = 60 * 1000;
    
    private static HandlerRegistration registration;
    private static EventBus bus = new SimpleEventBus();

    private static boolean active;

    private static final Timer STEP_TIMER = new Timer() {
        @Override
        public void run() {
            active = false;
            enable();
        }
    };
    
    private static final Timer IDLE_TIMER = new Timer() {
        @Override
        public void run() {
            bus.fireEvent(new IdleEvent());
        }
    };
    
    private static Event.NativePreviewHandler movePreviewHandler = new Event.NativePreviewHandler() {
        @Override
        public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
            if (event.getTypeInt() == Event.ONMOUSEMOVE) {
                mouseMove();
            }
        }
    };

    public static void enable() {
        if (registration == null) {
            registration = Event.addNativePreviewHandler(movePreviewHandler);
        }
    }

    public static void disable() {
        registration.removeHandler();
        registration = null;
    }

    private static void mouseMove() {
        System.out.println("Mouse move");
        
        activate();
        registration.removeHandler();
        registration = null;
        
        STEP_TIMER.cancel();
        STEP_TIMER.schedule(STEP_DURATION);
    }
    
    private static void activate() {
        active = true;
        IDLE_TIMER.cancel();
        IDLE_TIMER.schedule(IDLE_TIME);
    }
    
    public static boolean isActive() {
        return active;
    }
    
    public static HandlerRegistration addIdleHandler(IdleEvent.Handler handler) {
        return bus.addHandler(IdleEvent.TYPE, handler);
    }
}
