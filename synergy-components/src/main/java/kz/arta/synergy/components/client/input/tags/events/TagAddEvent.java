package kz.arta.synergy.components.client.input.tags.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.input.tags.Tag;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 16:34
 *
 * Событие для добавления тега
 */
public class TagAddEvent extends GwtEvent<TagAddEvent.TagAddEventHandler> {
    public static Type<TagAddEventHandler> TYPE = new Type<TagAddEventHandler>();

    public Type<TagAddEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(TagAddEventHandler handler) {
        handler.onTagAdd(this);
    }

    private Tag tag;

    public TagAddEvent(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public static interface TagAddEventHandler extends EventHandler {
        void onTagAdd(TagAddEvent event);
    }
}
