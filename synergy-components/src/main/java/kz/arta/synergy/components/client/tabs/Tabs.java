package kz.arta.synergy.components.client.tabs;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.tabs.events.HasTabHandlers;
import kz.arta.synergy.components.client.tabs.events.TabCloseEvent;
import kz.arta.synergy.components.client.tabs.events.TabSelectionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 08.08.14
 * Time: 12:23
 *
 * Панель на которой расположены только вкладки (без содержимого).
 */
public class Tabs extends Composite implements HasTabHandlers {
    static final EventBus innerBus = new SimpleEventBus();

    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Добавленные вкладки
     */
    private List<Tab> tabs;

    /**
     * Выбранная вкладка. Если null - ничего не выбрано.
     */
    private Tab selectedTab;

    /**
     * Хэндлеры для закрытия-выбора вкладок одинаковы для всех вкладок.
     */
    private final TabCloseEvent.Handler closeHandler;
    private final TabSelectionEvent.Handler selectionHandler;

    /**
     * Если после закрытия вкладки остается одна вкладка - у нее убирается кнопка закрыть (если она есть)
     * Эта переменная используется для восстановления кнопки закрыть.
     */
    boolean restoreCloseButton = false;

    public Tabs() {
        root = new FlowPanel();
        initWidget(root);

        addStyleName(SynergyComponents.resources.cssComponents().tabs());

        tabs = new ArrayList<Tab>();

        selectionHandler = new TabSelectionEvent.Handler() {
            @Override
            public void onTabSelection(TabSelectionEvent event) {
                selectTab(event.getTab(), true);
            }
        };
        closeHandler = new TabCloseEvent.Handler() {
            @Override
            public void onTabClose(TabCloseEvent event) {
                closeTab(event.getTab(), true);
            }
        };
    }

    /**
     * Добавить вкладку с текстом и содержимым
     * @param text текст
     * @param content содержимое
     * @return объект новой вкладки
     */
    public Tab addTab(String text, IsWidget content) {
        Tab newTab = new Tab();
        newTab.setText(text);
        newTab.setContent(content);
        addTab(newTab);

        return newTab;
    }

    /**
     * Добавить вкладку
     * @param tab вкладка
     */
    public void addTab(Tab tab) {
        //перед добавлением первой вкладки надо убрать кнопку закрыть
        if (tabs.size() == 0 && tab.hasCloseButton()) {
            restoreCloseButton = true;
            tab.setHasCloseButton(false);
        }

        tab.addTabSelectionHandler(selectionHandler);
        tab.addTabCloseHandler(closeHandler);
        root.add(tab);
        tabs.add(tab);

        //после добавления второй вкладки надо восстановить кнопку закрыть
        if (tabs.size() == 2 && restoreCloseButton) {
            tabs.get(0).setHasCloseButton(true);
            restoreCloseButton = false;
        }
    }

    /**
     * Возвращает вкладку на заданной позиции
     * @param index позиция
     * @return вкладка
     */
    public Tab get(int index) {
        if (index >= 0 && index < tabs.size()) {
            return tabs.get(index);
        }
        return null;
    }

    /**
     * Определяет есть ли вкладка с заданным содержимым
     * @param content содержимое
     * @return вкладка
     */
    public boolean contains(Widget content) {
        return get(content) != null;
    }

    /**
     * Возвращает первую вкладку с заданным содержимым
     * @param content содержимое
     * @return вкладка
     */
    public Tab get(Widget content) {
        for (Tab tab : tabs) {
            //noinspection NonJREEmulationClassesInClientCode
            if (tab.getContent().equals(content)) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Выбрать владку (сделать ее активной).
     * Если события не создаются, то это может привести к расхождению между
     * отображенным содержимым вкладки и выбранной вкладкой.
     * @param tab вкладка
     * @param fireEvents создавать ли события
     */
    public void selectTab(Tab tab, boolean fireEvents) {
        if (selectedTab != null) {
            int formerSelectedIndex = tabs.indexOf(selectedTab);
            if (formerSelectedIndex > 0 ) {
                tabs.get(formerSelectedIndex - 1).getElement().getStyle().clearBorderStyle();
            }
            selectedTab.setActive(false, false);
        }
        selectedTab = tab;
        selectedTab.setActive(true, false);

        int selectedIndex = tabs.indexOf(selectedTab);
        if (selectedIndex > 0) {
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                tabs.get(selectedIndex - 1).getElement().getStyle().setProperty("borderLeftStyle", "hidden");
            } else {
                tabs.get(selectedIndex - 1).getElement().getStyle().setProperty("borderRightStyle", "hidden");
            }
        }

        if (fireEvents) {
            innerBus.fireEventFromSource(new TabSelectionEvent(tab), this);
        }
    }

    /**
     * Закрыть вкладку
     * @param tab вкладка
     * @param fireEvents создавать ли события об этом
     */
    public void closeTab(Tab tab, boolean fireEvents) {
        if (tab.isActive()) {
            int index = tabs.indexOf(tab);
            tab.setActive(false, false);
            selectedTab = null;

            if (index > 0) {
                tabs.get(index - 1).getElement().getStyle().clearBorderStyle();
            }

            if (index == tabs.size() - 1) {
                if (tabs.size() > 1) {
                    selectTab(tabs.get(index - 1), true);
                }
            } else {
                selectTab(tabs.get(index + 1), true);
            }
        }
        tabs.remove(tab);
        root.remove(tab);

        //после удаления второй вкладки надо убрать кнопку закрыть у оставшейся вкладки
        if (tabs.size() == 1 && tabs.get(0).hasCloseButton()) {
            restoreCloseButton = true;
            tabs.get(0).setHasCloseButton(false);
        }

        if (fireEvents) {
            innerBus.fireEventFromSource(new TabCloseEvent(tab), this);
        }
    }

    /**
     * Количество добавленных вкладок
     */
    public int size() {
        return tabs.size();
    }

    @Override
    public HandlerRegistration addTabSelectionHandler(TabSelectionEvent.Handler handler) {
        return innerBus.addHandlerToSource(TabSelectionEvent.TYPE, this, handler);
    }

    @Override
    public HandlerRegistration addTabCloseHandler(TabCloseEvent.Handler handler) {
        return innerBus.addHandlerToSource(TabCloseEvent.TYPE, this, handler);
    }
}
