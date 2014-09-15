package kz.arta.synergy.components.client.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.tree.events.TreeOpenEvent;
import kz.arta.synergy.components.client.tree.events.TreeSelectionEvent;
import kz.arta.synergy.components.client.util.ArtaHasText;

import java.util.ArrayList;

//todo контекстное меню
/**
 * User: vsl
 * Date: 12.09.14
 * Time: 12:20
 *
 * Узел дерева
 */
public class TreeItem implements ArtaHasText, IsTreeItem, IsWidget, HasClickHandlers {
    /**
     * Длительность анимации
     */
    static final int ANIMATION_DURATION = 300;

    /**
     * Корневая панель
     */
    private ArtaFlowPanel root;

    /**
     * Узлы внутри данного узла
     */
    private ArrayList<TreeItem> children = new ArrayList<TreeItem>();

    /**
     * Текст узла
     */
    @UiField InlineLabel label;

    /**
     * Иконка
     */
    @UiField Image icon;

    /**
     * Индикатор открытия/закрытия
     */
    @UiField Image indicator;

    /**
     * Панель контента
     */
    @UiField FlowPanel content;

    /**
     * Выбран ли узел
     */
    private boolean isSelected = false;

    /**
     * Открыт ли узел
     */
    private boolean isOpen = false;

    private EventBus bus;

    /**
     * Некоторый объект этого узла
     */
    private Object userObject;

    /**
     * Изменения свойства display при закрытии производится с отложением
     */
    private final Timer closeTimer;

    /**
     * Родительский узел.
     * Необходим для корректного изменения высоты панели контента
     */
    private TreeItem parent;

    /**
     * Высота контента
     */
    private int contentHeight;

    /**
     * Анимация открытия-закрытия для IE9
     */
    private TreeAnimationIE9 animation;

    interface TreeItemUiBinder extends UiBinder<ArtaFlowPanel, TreeItem> {
    }

    private static TreeItemUiBinder ourUiBinder = GWT.create(TreeItemUiBinder.class);


    public TreeItem(String text, EventBus bus) {
        root = ourUiBinder.createAndBindUi(this);
        root.addStyleName(SynergyComponents.resources.cssComponents().treeItem());

        ClickHandler selectionHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setSelected(true, true);
            }
        };
        label.addClickHandler(selectionHandler);
        icon.addClickHandler(selectionHandler);

        this.bus = bus;
        label.setText(text);

        indicator.getElement().getStyle().setDisplay(Style.Display.NONE);

        content.setStyleName(SynergyComponents.resources.cssComponents().content());
        content.getElement().getStyle().setHeight(0, Style.Unit.PX);

        closeTimer = new Timer() {
            @Override
            public void run() {
                content.getElement().getStyle().setDisplay(Style.Display.NONE);
            }
        };
    }

    /**
     * Клик по индикатору открытия/закрытия
     */
    @UiHandler("indicator")
    void indicatorClick(ClickEvent event) {
        setOpen(!isOpen());
    }

    /**
     * Добавить узел
     */
    public void addTreeItem(TreeItem item) {
        if (children == null) {
            children = new ArrayList<TreeItem>();
        }
        children.add(item);
        item.setParent(this);

        content.add(item.asTreeItem());

        indicator.getElement().getStyle().clearDisplay();
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Открыть/закрыть узел
     * @param isOpen true - открыть, false - закрыть
     * @param fireEvents создавать ли события
     */
    public void setOpen(boolean isOpen, boolean fireEvents) {
        if (isOpen) {
            indicator.setResource(ImageResources.IMPL.nodeOpen());

            content.getElement().getStyle().clearDisplay();
            updateContentHeight(content.getElement().getScrollHeight());
        } else {
            indicator.setResource(ImageResources.IMPL.nodeClosed());
            updateContentHeight(-getContentHeight());
            closeTimer.schedule(ANIMATION_DURATION);
        }
        this.isOpen = isOpen;
        if (fireEvents) {
            bus.fireEvent(new TreeOpenEvent(this, isOpen));
        }
    }

    public void setOpen(boolean isOpen) {
        setOpen(isOpen, true);
    }

    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Выбрать/убрать выделение узел дерева
     * @param selected true - выделить, false - убрать выделение
     * @param fireEvents создавать ли события
     */
    public void setSelected(boolean selected, boolean fireEvents) {
        label.removeStyleName(getFontStyle());
        this.isSelected = selected;
        label.addStyleName(getFontStyle());

        if (selected) {
            root.addStyleName(SynergyComponents.resources.cssComponents().selected());
            if (fireEvents) {
                bus.fireEvent(new TreeSelectionEvent(this));
            }
        } else {
            root.removeStyleName(SynergyComponents.resources.cssComponents().selected());
        }
    }

    public void setSelected(boolean selected) {
        setSelected(selected, true);
    }

    @Override
    public String getFontStyle() {
        if (isSelected()) {
            return SynergyComponents.resources.cssComponents().mainTextBold();
        } else {
            return SynergyComponents.resources.cssComponents().mainText();
        }
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }

    @Override
    public TreeItem asTreeItem() {
        return this;
    }

    @Override
    public Widget asWidget() {
        return root;
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return root.addClickHandler(handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        root.fireEvent(event);
    }

    public Object getUserObject() {
        return userObject;
    }

    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    public TreeItem getParent() {
        return parent;
    }

    public void setParent(TreeItem parent) {
        this.parent = parent;
    }

    /**
     * Увеличивает/уменьшает высоту контента на заданную величину.
     * Рекурсивно вызывается для всех родителей.
     * @param deltaHeight величина, на которую изменяется высота
     */
    public void updateContentHeight(int deltaHeight) {
        setContentHeight(getContentHeight() + deltaHeight);
        if (parent != null) {
            parent.updateContentHeight(deltaHeight);
        }
    }

    public void setContentHeight(int height) {
        if (Window.Navigator.getAppVersion().contains("MSIE")) {
            if (animation == null) {
                animation = new TreeAnimationIE9(content.getElement());
            }
            animation.updateHeight(contentHeight, height);
        } else {
            content.getElement().getStyle().setHeight(height, Style.Unit.PX);
        }
        contentHeight = height;
    }

    public int getContentHeight() {
        return contentHeight;
    }
}
