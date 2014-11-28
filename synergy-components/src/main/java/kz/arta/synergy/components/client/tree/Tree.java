package kz.arta.synergy.components.client.tree;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.theme.ColorType;
import kz.arta.synergy.components.client.tree.events.TreeOpenEvent;
import kz.arta.synergy.components.client.tree.events.TreeSelectionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 12.09.14
 * Time: 14:07
 *
 * Дерево
 */
public class Tree extends Composite implements HasContextMenuHandlers {

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
    List<TreeItem> items;

    /**
     * Цвет дерева
     */
    private final boolean white;

    /**
     * Создаем дерево (по умолчанию со скроллом и белое)
     */
    public Tree() {
        this(true, true);
    }

    /**
     * Создаем дерево с указанием необходимости скролла
     * @param withScroll true - нужен скролл, false - не нужен. Если false - тогда нужно поместить во внешний скролл
     * @param white true - дерево белое, false - черное
     */
    @UiConstructor
    public Tree(boolean withScroll, boolean white) {
        this.white = white;
        if (withScroll) {
            ArtaScrollPanel scroll = new ArtaScrollPanel(white ? ColorType.WHITE : ColorType.BLACK);
            initWidget(scroll);
            scroll.addStyleName(SynergyComponents.getResources().cssComponents().tree());
            if (!white) {
                scroll.addStyleName(SynergyComponents.getResources().cssComponents().dark());
            }

            root = new FlowPanel();
            scroll.setWidget(root);
        } else {
            root = new FlowPanel();
            initWidget(root);
            root.setStyleName(SynergyComponents.getResources().cssComponents().tree());
            root.addStyleName(SynergyComponents.getResources().cssComponents().dark());
        }

        bus = new SimpleEventBus();
        items = new ArrayList<TreeItem>();

        bus.addHandler(TreeSelectionEvent.getType(), new TreeSelectionEvent.Handler() {
            @Override
            public void onTreeSelection(TreeSelectionEvent event) {
                if (event.getTreeItem() != selectedItem) {
                    if (selectedItem != null) {
                        selectedItem.setSelected(false, false);
                    }
                    selectedItem = event.getTreeItem();
                }
                if (selectedItem.indicator != null) {
                    selectedItem.indicator.getElement().scrollIntoView();
                } else if (selectedItem.icon != null) {
                    selectedItem.icon.getElement().scrollIntoView();
                } else {
                    selectedItem.label.getElement().scrollIntoView();
                }
            }
        });
    }

    /**
     * Возвращает все добавленные корневые узлы дерева
     */
    public List<TreeItem> getItems() {
        return items;
    }

    /**
     * Добавить узел в корень дерева
     * @param text текст нового узла
     * @return новый узел дерева
     */
    public TreeItem addItem(String text) {
        TreeItem item = new TreeItem(text, bus, white);


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
        TreeItem item = new TreeItem(text, bus, white);
        parentItem.addTreeItem(item);
        return item;
    }

    /**
     * {@link #addItem(TreeItem, String)}
     * @param icon иконка у нового элемента дерева
     */
    public TreeItem addItem(TreeItem parentItem, String text, ImageResource icon) {
        TreeItem item = addItem(parentItem, text);
        item.setIcon(icon);
        return item;
    }

    @SuppressWarnings("UnusedDeclaration")
    public HandlerRegistration addTreeOpenHandler(TreeOpenEvent.Handler handler) {
        return bus.addHandler(TreeOpenEvent.getType(), handler);
    }

    public HandlerRegistration addTreeSelectionEvent(TreeSelectionEvent.Handler handler) {
        return bus.addHandler(TreeSelectionEvent.getType(), handler);
    }

    @Override
    public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
        return root.addDomHandler(handler, ContextMenuEvent.getType());
    }

    /**
     * @return выбранный элемент дерева
     */
    public TreeItem getSelectedItem() {
        return selectedItem;
    }
}
