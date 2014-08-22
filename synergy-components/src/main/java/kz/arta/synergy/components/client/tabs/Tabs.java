package kz.arta.synergy.components.client.tabs;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.resources.ImageResources;
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
    /**
     * Продолжительность анимации
     */
    private static final int SCROLL_DURATION = 200;

    static final EventBus innerBus = new SimpleEventBus();

    /**
     * Панель в которую добавляются кнопки для скролла
     */
    private FlowPanel rootContainer;

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

    /**
     * Кнопки для скроллинга табов, когда табов слишком много
     */
    private ImageButton backButton;
    private ImageButton forwardButton;

    /**
     * Анимация для скролла
     */
    private ScrollAnimation scroll;

    public Tabs() {
        rootContainer = new FlowPanel();
        initWidget(rootContainer);
        root = new FlowPanel();
        rootContainer.add(root);

        root.addStyleName(SynergyComponents.resources.cssComponents().tabs());
        rootContainer.addStyleName(SynergyComponents.resources.cssComponents().tabsContainer());

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

        backButton = new ImageButton(ImageResources.IMPL.tabsLeft());
        forwardButton = new ImageButton(ImageResources.IMPL.tabsRight());

        backButton.getElement().getStyle().setLeft(0, Style.Unit.PX);
        backButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                scrollTo(getPreviousHiddenTab(), false);
            }
        });
        backButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                backButton.setIcon(ImageResources.IMPL.tabsLeftOver());
            }
        });
        backButton.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                backButton.setIcon(ImageResources.IMPL.tabsLeft());
            }
        });

        forwardButton.getElement().getStyle().setRight(0, Style.Unit.PX);
        forwardButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                scrollTo(getNextHiddenTab(), true);
            }
        });
        forwardButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                forwardButton.setIcon(ImageResources.IMPL.tabsRightOver());
            }
        });
        forwardButton.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                forwardButton.setIcon(ImageResources.IMPL.tabsRight());
            }
        });

    }

    /**
     * Возвращает первый таб, часть которого скрыта за правым краем панели табов.
     * Если такого нет - возвращает последний.
     */
    private Tab getNextHiddenTab() {
        int scollStart = root.getAbsoluteLeft();
        int scrollEnd = scollStart + root.getOffsetWidth();

        for (Tab tab : tabs) {
            if (tab.getAbsoluteLeft() + tab.getOffsetWidth() > scrollEnd) {
                return tab;
            }
        }
        return tabs.get(tabs.size() - 1);
    }

    /**
     * Возвращает последний там, часть которого скрыта за левым краем панели табов.
     * Если такого нет - возвращает первый.
     */
    private Tab getPreviousHiddenTab() {
        Tab previous = tabs.get(0);
        for (Tab tab : tabs) {
            if (tab.getAbsoluteLeft() >= root.getAbsoluteLeft()) {
                return previous;
            }
            previous = tab;
        }
        return tabs.get(0);
    }

    private void scrollTo(int left) {
        if (scroll == null) {
            scroll = new ScrollAnimation();
        }
        scroll.scrollTo(left);
    }

    private void scrollTo(Tab targetTab, boolean direction) {
        int leftOffset = 0;
        for (Tab tab : tabs) {
            if (tab == targetTab) {
                break;
            }
            leftOffset += tab.getOffsetWidth();
        }

        if (direction) {
            //правая граница таба - правая граница таб панели
            scrollTo(leftOffset + targetTab.getOffsetWidth() - root.getOffsetWidth());
        } else {
            //левая граница таба - левая граница таб панели
            scrollTo(leftOffset);
        }
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

        checkScrollButtons();
    }

    private void checkScrollButtons() {
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            root.getElement().getStyle().clearRight();
        } else {
            root.getElement().getStyle().clearLeft();
        }
        root.getElement().getStyle().clearWidth();

        if (root.getElement().getScrollWidth() > root.getOffsetWidth()) {
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                root.getElement().getStyle().setRight(20, Style.Unit.PX);
            } else {
                root.getElement().getStyle().setLeft(20, Style.Unit.PX);
            }
            root.setWidth(rootContainer.getOffsetWidth() - 20 - 19 + "px");
            rootContainer.insert(backButton, 0);
            rootContainer.add(forwardButton);
        } else {
            rootContainer.remove(backButton);
            rootContainer.remove(forwardButton);
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

        selectedTab.getElement().scrollIntoView();

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

        checkScrollButtons();
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

    /**
     * Анимация для скролла табов
     */
    private class ScrollAnimation extends Animation {
        /**
         * Старое значение scrollLeft
         */
        private int oldLeft;

        /**
         * Значение scrollLeft к которому скроллится
         */
        private int newLeft;

        @Override
        protected void onUpdate(double progress) {
            int change = (int) ((newLeft - oldLeft) * progress);
            root.getElement().setScrollLeft(oldLeft + change);
        }

        /**
         * Скроллит панель табов к заданному значению
         * @param left значение scrollLeft к которому скроллит
         */
        public void scrollTo(int left) {
            this.oldLeft = root.getElement().getScrollLeft();
            this.newLeft = left;
            run(SCROLL_DURATION);
        }
    }
}
