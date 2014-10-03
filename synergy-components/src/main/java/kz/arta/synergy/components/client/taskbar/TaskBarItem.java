package kz.arta.synergy.components.client.taskbar;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasText;
import kz.arta.synergy.components.client.taskbar.events.TaskBarEvent;
import kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent;

/**
 * User: vsl
 * Date: 03.10.14
 * Time: 9:49
 *
 * Представляет элемент панели задач.
 * Все компоненты, которые надо отображать в панели задач должны наследовать от него.
 *
 * Состояние определяется иконкой и текстом. При изменении состояния необходимо создавать
 * событие {@link kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent}.
 *
 * Методы {@link #collapse()}, {@link #open()}, {@link #close()} используются таскбаром для
 * управления объектом. Например, при клике по элементу таскбара вызывается {@link #open()}
 *
 * При закрытии, сворачивании и открытии объекта (например, при закрытии диалога кликом) надо создавать
 * событие {@link kz.arta.synergy.components.client.taskbar.events.TaskBarEvent} правильного типа.
 */
public interface TaskBarItem extends HasText {

    /**
     * Иконка. Если возвращает null - используется стандартная иконка.
     */
    ImageResource getTaskBarIcon();
    void setTaskBarIcon(ImageResource image);

    /**
     * Добавляет хендлер на изменение состояния
     */
    HandlerRegistration addModelChangeHandler(ModelChangeEvent.Handler handler);

    HandlerRegistration addTaskBarHandler(TaskBarEvent.Handler handler);

    void close();
    void open();
    void collapse();
}
