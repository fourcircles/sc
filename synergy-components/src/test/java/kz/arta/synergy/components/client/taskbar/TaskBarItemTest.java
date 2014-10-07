package kz.arta.synergy.components.client.taskbar;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent;
import kz.arta.synergy.components.client.taskbar.events.TaskBarEvent;

/**
* User: vsl
* Date: 06.10.14
* Time: 17:50
*/
class TaskBarItemTest implements TaskBarItem{
    private static EventBus bus = new SimpleEventBus();
    private boolean isOpen = false;

    private String text;

    TaskBarItemTest(String text) {
        this.text = text;
    }

    @Override
    public ImageResource getTaskBarIcon() {
        return null;
    }

    @Override
    public HandlerRegistration addModelChangeHandler(ModelChangeEvent.Handler handler) {
        return bus.addHandlerToSource(ModelChangeEvent.getType(), this, handler);
    }

    @Override
    public HandlerRegistration addTaskBarHandler(TaskBarEvent.Handler handler) {
        return bus.addHandlerToSource(TaskBarEvent.getType(), this, handler);
    }

    @Override
    public void close() {
        isOpen = false;
        bus.fireEventFromSource(new TaskBarEvent(TaskBarEvent.EventType.CLOSE), this);
    }

    @Override
    public void open() {
        isOpen = true;
        bus.fireEventFromSource(new TaskBarEvent(TaskBarEvent.EventType.SHOW), this);
    }

    @Override
    public void collapse() {
        isOpen = false;
        bus.fireEventFromSource(new TaskBarEvent(TaskBarEvent.EventType.COLLAPSE), this);
    }

    @Override
    public String getText() {
        return text;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        bus.fireEventFromSource(new ModelChangeEvent(), this);
    }

    public void modelChanged() {
        bus.fireEventFromSource(new ModelChangeEvent(), this);
    }

    public void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
    }
}
