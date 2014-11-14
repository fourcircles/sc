package kz.arta.synergy.components.client.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.tree.events.TreeItemContextMenuEvent;
import kz.arta.synergy.components.client.tree.events.TreeOpenEvent;
import kz.arta.synergy.components.client.tree.events.TreeSelectionEvent;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.client.util.Utils;

import java.util.ArrayList;
import java.util.List;

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
    private List<TreeItem> items;

    /**
     * Текст узла
     */
    @UiField InlineLabel label;

    /**
     * Иконка
     */
    @UiField Image icon;

    /**
     * Картинка для иконки
     */
    private ImageResource iconImage;

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

    /**
     * Является ли узле избранным
     */
    private boolean favorite = false;

    private EventBus bus;

    /**
     * Некоторый объект этого узла
     */
    private Object userObject;

    /**
     * Изменения свойства display при закрытии производится с отложением
     */
    Timer closeTimer;

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

    TreeItem(String text, final EventBus bus) {
        root = ourUiBinder.createAndBindUi(this);
        root.addStyleName(SynergyComponents.getResources().cssComponents().treeItem());


        ClickHandler selectionHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setSelected(true, true);
            }
        };
        DoubleClickHandler doubleSelectHandler = new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                event.preventDefault();
                setOpen(!isOpen());
            }
        };

        label.addStyleName(SynergyComponents.getResources().cssComponents().unselectable());
        label.addClickHandler(selectionHandler);
        label.addDoubleClickHandler(doubleSelectHandler);
        icon.addClickHandler(selectionHandler);
        icon.addDoubleClickHandler(doubleSelectHandler);

        icon.getElement().getStyle().setDisplay(Style.Display.NONE);

        this.bus = bus;
        label.setText(text);

        indicator.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);

        content.setStyleName(SynergyComponents.getResources().cssComponents().content());
        content.getElement().getStyle().setHeight(0, Style.Unit.PX);
        content.getElement().getStyle().setDisplay(Style.Display.NONE);

        closeTimer = new Timer() {
            @Override
            public void run() {
                content.getElement().getStyle().setDisplay(Style.Display.NONE);
            }
        };
        root.addContextMenuHandler(new ContextMenuHandler() {
            @Override
            public void onContextMenu(ContextMenuEvent event) {
                if (bus != null) {
                    //при вызове контекстного меню создаем кастомное событие о контекстном меню узла
                    bus.fireEventFromSource(new TreeItemContextMenuEvent(TreeItem.this, event),
                            TreeItem.this);
                }
            }
        });
    }

    /**
     * Клик по индикатору открытия/закрытия
     */
    @UiHandler("indicator")
    void indicatorClick(ClickEvent event) {
        setOpen(!isOpen());
    }

    /**
     * Скрывает или показывает индикатор открытия в зависимости от наличия внутренниз узлов
     */
    private void updateIndicator() {
        if (items != null && !items.isEmpty()) {
            indicator.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            indicator.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
    }

    /**
     * Удаляет узел
     * @param item узел
     */
    public void removeTreeItem(TreeItem item) {
        if (items != null && items.contains(item)) {
            items.remove(item);
            item.setParent(null);

            content.remove(item.asTreeItem());

            updateIndicator();
        }
    }

    /**
     * Добавить узел.
     * Повторное добавление узла запрещено.
     */
    void addTreeItem(TreeItem item) {
        if (items == null) {
            items = new ArrayList<TreeItem>();
        }
        if (item == null || items.contains(item)) {
            return;
        }
        items.add(item);
        item.setParent(this);

        content.add(item.asTreeItem());

        updateIndicator();
        updateContentHeight(-getContentHeight());
        updateContentHeight(content.getElement().getScrollHeight());
    }

    /**
     * Изменить иконку узла
     * @param resource картинка
     */
    public void setIcon(ImageResource resource) {
        if (resource == null) {
            icon.getElement().getStyle().setDisplay(Style.Display.NONE);
        } else {
            icon.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        }
        icon.setResource(resource);
        iconImage = resource;
    }

    public ImageResource getIcon() {
        return iconImage;
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
            indicator.setResource(ImageResources.IMPL.nodeOpen16());

            content.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            if (parent != null && !parent.isOpen()) {
                parent.setOpen(true, fireEvents);
            }
            updateContentHeight(content.getElement().getScrollHeight());
        } else {
            indicator.setResource(ImageResources.IMPL.nodeClosed16());
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
     * Выбрать/убрать выделение узел дерева.
     * Событие создается для каждого вызова, где selected==true, даже если узел уже был выбран.
     * @param selected true - выделить, false - убрать выделение
     * @param fireEvents создавать ли события
     */
    public void setSelected(boolean selected, boolean fireEvents) {
        label.removeStyleName(getFontStyle());
        this.isSelected = selected;
        label.addStyleName(getFontStyle());

        if (selected) {
            root.addStyleName(SynergyComponents.getResources().cssComponents().selected());
            if (fireEvents) {
                bus.fireEvent(new TreeSelectionEvent(this));
            }
            TreeItem selectedParent = parent;
            while (selectedParent != null) {
                if (!selectedParent.isOpen()) {
                    selectedParent.setOpen(true);
                }
                selectedParent = selectedParent.getParent();
            }
        } else {
            root.removeStyleName(SynergyComponents.getResources().cssComponents().selected());
        }
    }

    public void setSelected(boolean selected) {
        setSelected(selected, true);
    }

    @Override
    public String getFontStyle() {
        if (isSelected()) {
            return SynergyComponents.getResources().cssComponents().mainTextBold();
        } else {
            return SynergyComponents.getResources().cssComponents().mainText();
        }
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public boolean contains(TreeItem item) {
        return items != null && items.contains(item);
    }

    public List<TreeItem> getItems() {
        return items;
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

    /**
     * Изменяет родительский узел.
     */
    public void setParent(TreeItem parent) {
        if (this.parent == null) {
            this.parent = parent;
        } else {
            this.parent.removeTreeItem(this);
            this.parent = parent;
        }
    }

    /**
     * Увеличивает/уменьшает высоту контента на заданную величину.
     * Рекурсивно вызывается для всех родителей.
     * @param deltaHeight величина, на которую изменяется высота
     */
    void updateContentHeight(int deltaHeight) {
        setContentHeight(getContentHeight() + deltaHeight);
        if (parent != null) {
            parent.updateContentHeight(deltaHeight);
        }
    }

    /**
     * Изменяет высоту контента
     * @param height новая высота
     */
    private void setContentHeight(int height) {
        if (Navigator.isIE()) {
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

    /**
     * Добавляет хендлер для элемента дерева.
     * Если хендлеры добавлены для элемента дерева и для дерева, то при обработке
     * события вызова контекстного меню для элемента дерева надо предотвращать bubbling.
     */
    public HandlerRegistration addTreeContextMenuHandler(TreeItemContextMenuEvent.Handler handler) {
        return bus.addHandlerToSource(TreeItemContextMenuEvent.getType(), this, handler);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
