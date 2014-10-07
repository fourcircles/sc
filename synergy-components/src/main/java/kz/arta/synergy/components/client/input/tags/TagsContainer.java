package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 24.09.14
 * Time: 14:45
 */
public abstract class TagsContainer<V> extends Composite {
    protected EventBus innerBus;

    protected TagsContainer() {
        innerBus = new SimpleEventBus();
    }

    protected TagsContainer(EventBus bus) {
        innerBus = bus;
    }

    public abstract List<Tag<V>> getTags();

    public Tag<V> getTag(V value) {
        for (Tag<V> tag : getTags()) {
            //noinspection NonJREEmulationClassesInClientCode
            if (tag.getValue().equals(value)) {
                return tag;
            }
        }
        return null;
    }

    public void removeTag(Tag<V> tag) {
        innerBus.fireEventFromSource(new TagRemoveEvent<V>(tag), this);
    }

    public void addTag(Tag<V> tag) {
        innerBus.fireEventFromSource(new TagAddEvent<V>(tag), this);
    }

    public boolean contains(V value) {
        return getTag(value) != null;
    }

    public void clear() {
        //список будет изменяться, поэтому надо сделать его копию
        List<Tag<V>> newTags = new ArrayList<Tag<V>>(getTags());
        for (Tag<V> tag : newTags) {
            removeTag(tag);
        }
    }
}
