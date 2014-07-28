package kz.arta.synergy.components.client.input.tags.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import kz.arta.synergy.components.client.input.tags.Tag;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 16:39
 *
 * Событие для удаления тега
 */
public class TagRemoveEvent extends GwtEvent<TagRemoveEvent.TagRemoveEventHandler> {
    public static Type<TagRemoveEventHandler> TYPE = new Type<TagRemoveEventHandler>();

    public Type<TagRemoveEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(TagRemoveEventHandler handler) {
        handler.onTagRemove(this);
    }

    private Tag tag;

    public TagRemoveEvent(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public static interface TagRemoveEventHandler extends EventHandler {
        void onTagRemove(TagRemoveEvent event);
    }
}
