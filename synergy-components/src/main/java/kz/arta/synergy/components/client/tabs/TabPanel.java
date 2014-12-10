package kz.arta.synergy.components.client.tabs;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.tabs.events.HasTabHandlers;
import kz.arta.synergy.components.client.tabs.events.TabCloseEvent;
import kz.arta.synergy.components.client.tabs.events.TabSelectionEvent;

/**
 * User: vsl
 * Date: 08.08.14
 * Time: 11:46
 * Панель вкладок
 */
public class TabPanel extends Composite implements HasTabHandlers {
    static final EventBus INNER_BUS = new SimpleEventBus();

    /**
     * Панель для содержимого
     */
    private FlowPanel contentPanel;

    /**
     * Панель с вкладками
     */
    private Tabs tabs;

    public TabPanel() {
        FlowPanel root = new FlowPanel();
        initWidget(root);

        tabs = new Tabs();
        root.add(tabs);

        contentPanel = new FlowPanel();
        contentPanel.addStyleName(SynergyComponents.getResources().cssComponents().tabContent());

        root.add(contentPanel);

        tabs.addTabCloseHandler(new TabCloseEvent.Handler() {
            @Override
            public void onTabClose(TabCloseEvent event) {
                INNER_BUS.fireEventFromSource(event, TabPanel.this);
            }
        });
        tabs.addTabSelectionHandler(new TabSelectionEvent.Handler() {
            @Override
            public void onTabSelection(TabSelectionEvent event) {
                setContent(event.getTab());
                INNER_BUS.fireEventFromSource(event, TabPanel.this);
            }
        });
    }

    /**
     * Содержит ли панель вкладку с заданным содержимым
     * @param content содержимое
     */
    public boolean contains(Widget content) {
        return tabs.contains(content);
    }

    /**
     * Добавить вкладку с заданным текстом и содержимым
     * @param text текст
     * @param content содержимое
     */
    public void addTab(String text, IsWidget content) {
        tabs.addTab(text, content);
    }

    /**
     * {@link #addTab(String, com.google.gwt.user.client.ui.IsWidget)}
     * @param closable может ли пользователь закрыть эту вкладку
     */
    public void addTab(String text, IsWidget content, boolean closable) {
        Tab tab = tabs.addTab(text, content);
        tab.setHasCloseButton(closable);
    }

    /**
     * Выбрать вкладку с заданным содержимым
     * @param content содержимое
     */
    public void selectTab(Widget content) {
        selectTab(tabs.get(content));
    }

    /**
     * Выбрать вкладку на заданной позиции
     * @param index позиция
     */
    public void selectTab(int index) {
        selectTab(tabs.get(index));
    }

    private void selectTab(Tab tab) {
        if (tab != null) {
            tab.setActive(true, true);
            setContent(tab);
        }
    }

    /**
     * Задать отображаемое содержимое как содержимое заданной вкладки
     * @param tab вкладка с содержимым
     */
    private void setContent(Tab tab) {
        contentPanel.clear();
        contentPanel.add(tab.getContent());
    }

    @Override
    public HandlerRegistration addTabSelectionHandler(TabSelectionEvent.Handler handler) {
        return INNER_BUS.addHandlerToSource(TabSelectionEvent.getType(), this, handler);
    }

    @Override
    public HandlerRegistration addTabCloseHandler(TabCloseEvent.Handler handler) {
        return INNER_BUS.addHandlerToSource(TabCloseEvent.getType(), this, handler);
    }

    public FlowPanel getContentPanel() {
        return contentPanel;
    }
}
