package kz.arta.synergy.components.client.taskbar;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.List;

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
    List<TaskBarItem> items;

    /**
     * Ширина (без границ)
     */
    double width;

    /**
     * Индикатор скрытых элементов
     */
    private TaskBarItem indicator;

    private PopupPanel popup;
    /**
     * Корневая панель для скрытых элементов
     */
    private FlowPanel indicatorRoot;

    public TaskBar() {
        root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.resources.cssComponents().taskBar());

        items = new ArrayList<TaskBarItem>();

        indicator = new TaskBarItem();
        indicator.addStyleName(SynergyComponents.resources.cssComponents().indicator());

        popup = new PopupPanel(true);
        popup.setStyleName(SynergyComponents.resources.cssComponents().taskBarIndicator());
        indicatorRoot = new FlowPanel();
        indicatorRoot.setStyleName(SynergyComponents.resources.cssComponents().content());
        popup.setWidget(indicatorRoot);
        popup.addAutoHidePartner(indicator.getElement());

        indicator.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
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
        });

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                width = Utils.getPreciseWidth(TaskBar.this.getElement()) - Constants.BORDER_WIDTH * 2 - Constants.TASKBAR_IMAGE_MARGIN;
                updateWidths();
            }
        });
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
     * Добавляет новый элемент с заданным текстом
     * @param text текст для элемента
     * @return новый элемент панели задач
     */
    public TaskBarItem addItem(String text) {
        TaskBarItem item = new TaskBarItem();
        item.setText(text);

        items.add(item);
        root.add(item);

        updateWidths();
        return item;
    }

    /**
     * Убрать элемент на заданной позиции
     * @param index позиция
     */
    public void removeItem(int index) {
        if (index < 0 && index >= items.size()) {
            return;
        }
        TaskBarItem item = items.get(index);
        items.remove(index);
        root.remove(item);
        updateWidths();
    }

    /**
     * {@link #removeItem(int)}
     */
    public void removeItem(TaskBarItem item) {
        if (items.contains(item)) {
            removeItem(items.indexOf(item));
        }
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
            totalWidth += item.getNormalWidth() + Constants.BORDER_WIDTH * 2;
        }
        if (width - (items.size() - 1) * Constants.TASKBAR_ITEM_PADDING >= totalWidth) {
            for (TaskBarItem item : items) {
                item.setWidth(item.getNormalWidth());
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
        indicator.setText("+" + num);
        return indicator.getNormalWidth() + Constants.BORDER_WIDTH * 2 - Constants.TASKBAR_ITEM_PADDING;
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
            TaskBarItem item = items.get(i);
            double itemWidth = item.getNormalWidth() + Constants.BORDER_WIDTH * 2;
            item.setWidth(itemWidth - (itemWidth - MIN_ITEM_WIDTH) * reduce - Constants.BORDER_WIDTH * 2);
        }

        if (hasIndicator) {
            for (int i = visibleItems; i < items.size(); i++) {
                root.remove(items.get(i));
            }
            if (!indicator.isAttached()) {
                root.add(indicator);
            }
            int hidden = items.size() - visibleItems;
            indicator.setText(hidden + "+");
            indicator.setWidth(getIndicatorWidth(hidden));

            for (int i = visibleItems; i < items.size(); i++) {
                TaskBarItem item = items.get(i);
                item.setWidth(item.getNormalWidth() + Constants.BORDER_WIDTH * 2);

                indicatorRoot.add(item);
            }
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
            double itemWidth = items.get(i).getNormalWidth() + Constants.BORDER_WIDTH * 2;
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
