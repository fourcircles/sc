package kz.arta.synergy.components.client.tabs;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
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
    private static final int SCROLL_DURATION = 500;

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
    private ScrollAnimation scrollAnimation = new ScrollAnimation();

    /**
     * Текущий оффсет для табов
     */
    private int leftOffset;
    private boolean hasScrollButtons = false;

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

        backButton = createBackButton();
        forwardButton = createForwardButton();
    }

    /**
     * Кнопка "назад"
     */
    ImageButton createBackButton() {
        final ImageButton button = new ImageButton(ImageResources.IMPL.tabsLeft());
        button.getElement().getStyle().setLeft(0, Style.Unit.PX);
        button.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                button.setIcon(ImageResources.IMPL.tabsLeftOver());
            }
        });
        button.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                button.setIcon(ImageResources.IMPL.tabsLeft());
            }
        });
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (LocaleInfo.getCurrentLocale().isRTL()) {
                    offsetTo(getNext(getLastVisible()), false);
                } else {
                    offsetTo(getPrevious(getFirstVisible()), false);
                }
            }
        });
        return button;
    }

    /**
     * Кнопка "вперед"
     */
    ImageButton createForwardButton() {
        final ImageButton button = new ImageButton(ImageResources.IMPL.tabsRight());
        button.getElement().getStyle().setRight(0, Style.Unit.PX);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (LocaleInfo.getCurrentLocale().isRTL()) {
                    offsetTo(getPrevious(getFirstVisible()), true);
                } else {
                    offsetTo(getNext(getLastVisible()), true);
                }
            }
        });
        button.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                button.setIcon(ImageResources.IMPL.tabsRightOver());
            }
        });
        button.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                button.setIcon(ImageResources.IMPL.tabsRight());
            }
        });
        return button;
    }

    /**
     * Видна ли вкладка
     * @param tab вкладка
     */
    private boolean isVisible(Tab tab) {
        int left = tab.getAbsoluteLeft();
        int right = left + tab.getOffsetWidth();

        return left >= root.getAbsoluteLeft() && right <= root.getAbsoluteLeft() + root.getOffsetWidth();
    }

    /**
     * Возвращает первую видимую вкладку
     */
    private Tab getFirstVisible() {
        for (Tab tab : tabs) {
            if (isVisible(tab)) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Возвращает последнюю видимую вкладку
     */
    private Tab getLastVisible() {
        for (int i = tabs.size() - 1; i >= 0; i--) {
            if (isVisible(tabs.get(i))) {
                return tabs.get(i);
            }
        }
        return null;
    }

    /**
     * Возвращает вкладку, следующую за заданной, если такой нет - последнюю.
     */
    private Tab getNext(Tab tab) {
        int index = tabs.indexOf(tab);
        if (index != -1) {
            if (index < tabs.size() - 1) {
                return tabs.get(index + 1);
            } else {
                return tab;
            }
        }
        return null;
    }

    /**
     * Возвращает вкладку перед заданной, если такой нет - первую.
     */
    private Tab getPrevious(Tab tab) {
        int index = tabs.indexOf(tab);
        if (index != -1) {
            if (index > 0) {
                return tabs.get(index - 1);
            } else {
                return tab;
            }
        }
        return null;
    }

    /**
     * Смещает все вкладки на заданную величину
     * @param left величина смещения
     */
    private void offsetBy(int left) {
        if (Window.Navigator.getAppVersion().contains("MSIE")) {
            scrollAnimation.scrollTo(left);
        } else {
            for (Tab tab : tabs) {
                if (LocaleInfo.getCurrentLocale().isRTL()) {
                    tab.getElement().getStyle().setRight(-left, Style.Unit.PX);
                } else {
                    tab.getElement().getStyle().setLeft(-left, Style.Unit.PX);
                }
            }
        }
        leftOffset = left;
    }

    /**
     * Скроллит панель к данной вкладке двумя разными способами
     * @param targetTab вкладка
     * @param direction true - правая граница вкладки у правого края панели,
     *                  false - левая граница вкладки у левого края панели
     */
    private void offsetTo(Tab targetTab, boolean direction) {
        int beforeTabsWidth = 0;
        for (Tab tab : tabs) {
            if (tab == targetTab) {
                break;
            }
            beforeTabsWidth += tab.getOffsetWidth();
        }
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            if (!direction) {
                beforeTabsWidth = beforeTabsWidth + targetTab.getOffsetWidth() - root.getOffsetWidth();
            }
        } else {
            if (direction) {
                beforeTabsWidth = beforeTabsWidth + targetTab.getOffsetWidth() - root.getOffsetWidth();
            }
        }

        offsetBy(beforeTabsWidth);
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

        tab.getElement().getStyle().setLeft(-leftOffset, Style.Unit.PX);
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

    /**
     * Добавляет кнопки скрола вкладок если они нужны
     */
    private void checkScrollButtons() {
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            root.getElement().getStyle().clearRight();
        } else {
            root.getElement().getStyle().clearLeft();
        }
        root.getElement().getStyle().clearWidth();

        if (root.getElement().getScrollWidth() > root.getOffsetWidth()) {
            hasScrollButtons = true;
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                root.getElement().getStyle().setRight(20, Style.Unit.PX);
            } else {
                root.getElement().getStyle().setLeft(20, Style.Unit.PX);
            }
            root.setWidth(rootContainer.getOffsetWidth() - 20 - 19 + "px");
            rootContainer.insert(backButton, 0);
            rootContainer.add(forwardButton);
        } else {
            hasScrollButtons = false;
            rootContainer.remove(backButton);
            rootContainer.remove(forwardButton);
            offsetBy(0);
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

        if (selectedTab.getAbsoluteLeft() < root.getAbsoluteLeft()) {
            //выделяем вкладку, ее левая часть скрыта
            offsetTo(selectedTab, false);
        } else if (selectedTab.getAbsoluteLeft() + selectedTab.getOffsetWidth() >
                root.getAbsoluteLeft() + root.getOffsetWidth()) {
            //правая часть скрыта
            offsetTo(selectedTab, true);
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

        checkScrollButtons();

        if (hasScrollButtons) {
            Tab lastTab = tabs.get(tabs.size() - 1);
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                if (lastTab.getAbsoluteLeft() > root.getAbsoluteLeft()) {
                    offsetTo(lastTab, false);
                }
            } else if (lastTab.getAbsoluteLeft() + lastTab.getOffsetWidth() <
                    root.getAbsoluteLeft() + root.getOffsetWidth()) {
                offsetTo(lastTab, true);
            }
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
            double change = (newLeft - oldLeft) * progress;

            for (Tab tab : tabs) {
                tab.getElement().getStyle().setLeft(oldLeft + change, Style.Unit.PX);
            }
        }

        /**
         * Скроллит панель табов к заданному значению
         * @param left значение scrollLeft к которому скроллит
         */
        public void scrollTo(int left) {
            this.oldLeft = -leftOffset;
            this.newLeft = -left;
            run(SCROLL_DURATION);
        }
    }
}
