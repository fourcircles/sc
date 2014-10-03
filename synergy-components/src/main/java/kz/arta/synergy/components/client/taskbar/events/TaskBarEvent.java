package kz.arta.synergy.components.client.taskbar.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * User: vsl
 * Date: 02.10.14
 * Time: 16:29
 *
 * Событие диалога
 */
public class TaskBarEvent extends GwtEvent<TaskBarEvent.Handler> {
    private static Type<Handler> TYPE;

    /**
     * Тип события
     */
    private EventType type;

    public TaskBarEvent(EventType type) {
        this.type = type;
    }

    public static Type<Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<Handler>();
        }
        return TYPE;
    }

    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(Handler handler) {
        switch (type) {
            case CLOSE:
                handler.onClose(this);
                break;
            case COLLAPSE:
                handler.onCollapse(this);
                break;
            case SHOW:
                handler.onShow(this);
                break;
            default:
        }
    }

    public static interface Handler extends EventHandler {
        void onClose(TaskBarEvent event);
        void onCollapse(TaskBarEvent event);
        void onShow(TaskBarEvent event);
    }

    /**
     * Класс для удобства, когда надо изменить только некоторые методы
     */
    public abstract static class AbstractHandler implements Handler {
        @Override
        public void onClose(TaskBarEvent event) {
            //extend
        }

        @Override
        public void onCollapse(TaskBarEvent event) {
            //extend
        }

        @Override
        public void onShow(TaskBarEvent event) {
            //extend
        }
    }

    /**
     * Тип события диалога
     */
    public enum EventType {
        /**
         * Закрытие
         */
        CLOSE,
        /**
         * Сворачивание
         */
        COLLAPSE,
        /**
         * Появление диалога
         */
        SHOW
    }
}
