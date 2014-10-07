package kz.arta.synergy.components.client.taskbar;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.taskbar.events.TaskBarEvent;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: vsl
 * Date: 01.10.14
 * Time: 14:04
 *
 * Панель задач
 */
public class TaskBar extends Composite {
    /**
     * Минимальная ширина элемента
     */
    private static final int MIN_ITEM_WIDTH = 50;

    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Добавленные элементы
     */
    List<TaskBarItem> items = new ArrayList<TaskBarItem>();

    /**
     * Ширина (без границ)
     */
    double width;

    /**
     * Индикатор скрытых элементов
     */
    private FlowPanel indicator;
    /**
     * Текст индикатора
     */
    private final Label indicatorLabel;

    private PopupPanel popup;
    /**
     * Корневая панель для скрытых элементов
     */
    private FlowPanel indicatorRoot;

    /**
     * Соответствие между добавленными элементами и их видами
     */
    private Map<TaskBarItem, TaskBarItemUI> uiMap = new HashMap<TaskBarItem, TaskBarItemUI>();

    public TaskBar() {
        root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.resources.cssComponents().taskBar());

        indicatorLabel = new Label();
        indicator = createIndicator(indicatorLabel);

        popup = new PopupPanel(true);
        popup.setStyleName(SynergyComponents.resources.cssComponents().taskBarIndicator());
        indicatorRoot = new FlowPanel();
        indicatorRoot.setStyleName(SynergyComponents.resources.cssComponents().content());
        popup.setWidget(indicatorRoot);
        popup.addAutoHidePartner(indicator.getElement());

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                width = Utils.getPreciseWidth(TaskBar.this.getElement()) - Constants.BORDER_WIDTH * 2 - Constants.TASKBAR_IMAGE_MARGIN;
                updateWidths();
            }
        });
    }

    /**
     * Создает индикатор
     */
    private FlowPanel createIndicator(Label label) {
        FlowPanel indicator = new FlowPanel();
        indicator.sinkEvents(Event.ONCLICK);
        indicator.setStyleName(SynergyComponents.resources.cssComponents().item());
        indicator.addStyleName(SynergyComponents.resources.cssComponents().indicator());
        Image indicatorImage = new Image(ImageResources.IMPL.calendarIcon());

        indicator.add(indicatorImage);
        indicator.add(label);

        indicator.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                indicatorClick();
            }
        }, ClickEvent.getType());

        return indicator;
    }

    /**
     * Клик по индикатору
     */
    private void indicatorClick() {
        if (popup.isShowing()) {
            popup.hide();
        } else {
            popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    int popupHeight = popup.getOffsetHeight();
                    int left;
                    if (LocaleInfo.getCurrentLocale().isRTL()) {
                        left = indicator.getAbsoluteLeft();
                    } else {
                        left = indicator.getAbsoluteLeft() + indicator.getOffsetWidth() - popup.getOffsetWidth();
                    }
                    popup.setPopupPosition(left, indicator.getAbsoluteTop() - popupHeight - Constants.TASKBAR_IMAGE_MARGIN);
                }
            });
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                width = Utils.getPreciseWidth(TaskBar.this.getElement()) - Constants.BORDER_WIDTH * 2 - Constants.TASKBAR_IMAGE_MARGIN;
                updateWidths();
            }
        });
    }

    /**
     * Создает новый вид для элемента панели задач
     * @param item элемент панели задач
     * @return его вид
     */
    private TaskBarItemUI createItemUI(final TaskBarItem item) {
        final TaskBarItemUI itemUI = new TaskBarItemUI(item);
        itemUI.addModelChangeHandler(new ModelChangeEvent.Handler() {
            @Override
            public void onModelChange(ModelChangeEvent event) {
                updateWidths();
            }
        });
        return itemUI;
    }

    /**
     * Добавляет элемент
     */
    public void addItem(final TaskBarItem item) {
        if (!items.contains(item)) {
            items.add(item);

            TaskBarItemUI ui = createItemUI(item);
            uiMap.put(item, ui);
            root.add(ui);
            updateWidths();

            item.addTaskBarHandler(new TaskBarEvent.AbstractHandler() {
                @Override
                public void onClose(TaskBarEvent event) {
                    removeItem(item);
                }
            });

            if (item.isOpen()) {
                item.open();
            }
        }
    }

    /**
     * Удаляет элемент
     */
    public void removeItem(TaskBarItem item) {
        if (items.contains(item)) {
            items.remove(item);
            root.remove(uiMap.get(item));
            uiMap.remove(item);

            updateWidths();
        }
    }

    /**
     * Убрать элемент на заданной позиции
     * @param index позиция
     */
    public void removeItem(int index) {
        if (index < 0 && index >= items.size()) {
            return;
        }
        removeItem(items.get(index));
    }

    public int size() {
        return items.size();
    }

    /**
     * Пробует разместить все элементы без сжатия
     * @return true если получилось
     */
    private boolean fitNormally() {
        double totalWidth = 0;
        for (TaskBarItem item : items) {
            totalWidth += uiMap.get(item).getNormalWidth() + Constants.BORDER_WIDTH * 2;
        }
        if (width - (items.size() - 1) * Constants.TASKBAR_ITEM_PADDING >= totalWidth) {
            for (TaskBarItem item : items) {
                TaskBarItemUI ui = uiMap.get(item);
                ui.setWidth(ui.getNormalWidth());
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Возвращает ширину индикатора при отображении количества скрытых элементов
     * @param num количество скрытых элементов
     * @return ширина индикатора
     */
    private double getIndicatorWidth(int num) {
        double textWidth = Utils.getPreciseTextWidth("+" + num, SynergyComponents.resources.cssComponents().mainText());
        return textWidth + Constants.STD_ICON_WIDTH + Constants.TASKBAR_IMAGE_MARGIN
                + Constants.TASKBAR_ITEM_PADDING * 2 + Constants.BORDER_WIDTH * 2;
    }

    private boolean fitReduced() {
        int i = items.size();
        while (i > 0 && i * MIN_ITEM_WIDTH > width - (i - 1) * Constants.TASKBAR_ITEM_PADDING - getIndicatorWidth(items.size() - i)) {
            i--;
        }
        fit(i);
        return true;
    }

    /**
     * Размещает первые visibleItems элементов на панели задач (с возможным сжатием),
     * остальные - в индикаторе скрытых элементов.
     * @param visibleItems количество видимых элементов
     */
    private void fit(int visibleItems) {
        if (!isAttached()) {
            return;
        }

        boolean hasIndicator = visibleItems < items.size();

        double totalItemWidth = getItemsWidth(visibleItems);
        double delta = totalItemWidth - MIN_ITEM_WIDTH * visibleItems;

        double widthNoMargins = width - (visibleItems - 1) * Constants.TASKBAR_ITEM_PADDING;
        if (hasIndicator) {
            widthNoMargins -= 5 + getIndicatorWidth(items.size() - visibleItems);
        }

        double reduce = (totalItemWidth - widthNoMargins) / delta;
        for (int i = 0; i < visibleItems; i++) {
            TaskBarItemUI item = uiMap.get(items.get(i));
            double itemWidth = item.getNormalWidth() + Constants.BORDER_WIDTH * 2;
            item.setWidth(itemWidth - (itemWidth - MIN_ITEM_WIDTH) * reduce - Constants.BORDER_WIDTH * 2);
        }

        if (hasIndicator) {
            for (int i = 0; i < visibleItems; i++) {
                root.add(uiMap.get(items.get(i)));
            }
            for (int i = visibleItems; i < items.size(); i++) {
                TaskBarItemUI item = uiMap.get(items.get(i));
                item.setWidth(item.getNormalWidth() + Constants.BORDER_WIDTH * 2);

                indicatorRoot.add(item);
            }
            root.add(indicator);

            int hidden = items.size() - visibleItems;

            indicatorLabel.setText(hidden + "+");
            indicator.getElement().getStyle().setWidth(getIndicatorWidth(hidden) - Constants.BORDER_WIDTH * 2, Style.Unit.PX);

        } else {
            root.remove(indicator);
        }
    }

    /**
     * Возвращает суммарную ширину первых visibleItems элементов
     */
    private double getItemsWidth(int visibleItems) {
        double totalItemWidth = 0;
        for (int i = 0; i < visibleItems; i++) {
            double itemWidth = uiMap.get(items.get(i)).getNormalWidth() + Constants.BORDER_WIDTH * 2;
            totalItemWidth += itemWidth;
        }
        return totalItemWidth;
    }

    /**
     * Размещает все добавленые элементы на панели задач
     */
    public void updateWidths() {
        if (!isAttached()) {
            return;
        }
        if (!fitNormally()) {
            fitReduced();
        }
    }
}
