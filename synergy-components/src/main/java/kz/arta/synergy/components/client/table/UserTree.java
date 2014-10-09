package kz.arta.synergy.components.client.table;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.synergy.components.client.table.column.TreeTableItem;
import kz.arta.synergy.components.client.table.events.TreeTableItemEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 08.10.14
 * Time: 10:40
 *
 * Пример объекта для дерева-таблицы
 */
public class UserTree extends User implements TreeTableItem<UserTree> {
    private UserTree parent;
    private List<UserTree> children;
    private boolean isOpen = false;

    private EventBus bus;

    public UserTree(UserTree parent, String firstName,
                    String lastName, String address) {
        super(firstName, lastName, address);

        setParent(parent);

        children = new ArrayList<UserTree>();
        bus = new SimpleEventBus();
    }

    public void setParent(UserTree parent) {
        if (parent != null) {
            this.parent = parent;
            parent.addChild(this);
        }
    }

    @Override
    public UserTree getParent() {
        return parent;
    }

    public void addChild(UserTree child) {
        children.add(child);
    }

    @Override
    public List<UserTree> getChildren() {
        return children;
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void open() {
        this.isOpen = true;
        bus.fireEventFromSource(new TreeTableItemEvent<UserTree>(this, TreeTableItemEvent.EventType.OPEN), this);
    }

    @Override
    public void close() {
        this.isOpen = false;
        bus.fireEventFromSource(new TreeTableItemEvent<UserTree>(this, TreeTableItemEvent.EventType.CLOSE), this);
    }

    @Override
    public HandlerRegistration addTreeTableHandler(TreeTableItemEvent.Handler<UserTree> handler) {
        return bus.addHandlerToSource(TreeTableItemEvent.TYPE, this, handler);
    }
}
