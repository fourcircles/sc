package kz.arta.synergy.components.client.dialog.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.dialog.DialogSimple;

/**
 * User: vsl
 * Date: 02.10.14
 * Time: 16:29
 *
 * Событие диалога
 */
public class DialogEvent extends GwtEvent<DialogEvent.Handler> {
    private static Type<Handler> TYPE;

    /**
     * Диалог события
     */
    private DialogSimple dialog;

    /**
     * Тип события
     */
    private EventType type;

    public DialogEvent(DialogSimple dialog, EventType type) {
        this.dialog = dialog;
        this.type = type;
    }

    public DialogSimple getDialog() {
        return dialog;
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
            case TEXT_CHANGE:
                handler.onTextChange(this);
                break;
            default:
        }
    }

    public static interface Handler extends EventHandler {
        void onClose(DialogEvent event);
        void onCollapse(DialogEvent event);
        void onTextChange(DialogEvent event);
        void onShow(DialogEvent event);
    }

    /**
     * Класс для удобства, когда надо изменить только некоторые методы
     */
    public abstract static class AbstractHandler implements Handler {
        @Override
        public void onClose(DialogEvent event) {
            //extend
        }

        @Override
        public void onCollapse(DialogEvent event) {
            //extend
        }

        @Override
        public void onTextChange(DialogEvent event) {
            //extend
        }

        @Override
        public void onShow(DialogEvent event) {
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
         * Изменение текста заголовка
         */
        TEXT_CHANGE,
        /**
         * Появление диалога
         */
        SHOW
    }
}
