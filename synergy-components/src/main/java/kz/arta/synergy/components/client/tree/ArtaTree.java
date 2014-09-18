package kz.arta.synergy.components.client.tree;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.tree.events.TreeOpenEvent;
import kz.arta.synergy.components.client.tree.events.TreeSelectionEvent;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 12.09.14
 * Time: 14:07
 *
 * Дерево
 */
public class ArtaTree extends Composite implements HasContextMenuHandlers {

    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Выбранный элемент
     */
    private TreeItem selectedItem = null;
    private EventBus bus;

    /**
     * Внутренние пункты дерева
     */
    ArrayList<TreeItem> items;

    public ArtaTree() {
        ArtaScrollPanel scroll = new ArtaScrollPanel();
        initWidget(scroll);
        scroll.addStyleName(SynergyComponents.resources.cssComponents().tree());

        root = new FlowPanel();
        scroll.setWidget(root);


        bus = new SimpleEventBus();
        items = new ArrayList<TreeItem>();

        bus.addHandler(TreeSelectionEvent.TYPE, new TreeSelectionEvent.Handler() {
            @Override
            public void onTreeSelection(TreeSelectionEvent event) {
                if (event.getTreeItem() == selectedItem) {
                    return;
                }
                if (selectedItem != null) {
                    selectedItem.setSelected(false, false);
                }
                selectedItem = event.getTreeItem();
                selectedItem.asWidget().getElement().scrollIntoView();
            }
        });
    }

    /**
     * Возвращает все добавленные корневые узлы дерева
     */
    public ArrayList<TreeItem> getItems() {
        return items;
    }

    /**
     * Добавить узел в корень дерева
     * @param text текст нового узла
     * @return новый узел дерева
     */
    public TreeItem addItem(String text) {
        TreeItem item = new TreeItem(text, bus);

        items.add(item);
        root.add(item.asTreeItem());

        return item;
    }

    /**
     * Добавить новый узел дерева в заданный узел
     * @param parentItem родительский узел
     * @param text текст нового узла
     * @return новый узел
     */
    public TreeItem addItem(TreeItem parentItem, String text) {
        TreeItem item = new TreeItem(text, bus);
        parentItem.addTreeItem(item);
        return item;
    }

    public HandlerRegistration addTreeOpenHandler(TreeOpenEvent.Handler handler) {
        return bus.addHandler(TreeOpenEvent.TYPE, handler);
    }

    public HandlerRegistration addTreeSelectionEvent(TreeSelectionEvent.Handler handler) {
        return bus.addHandler(TreeSelectionEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
        return root.addDomHandler(handler, ContextMenuEvent.getType());
    }
}
