package kz.arta.synergy.components.client.tabs;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.tabs.events.TabCloseEvent;
import kz.arta.synergy.components.client.tabs.events.TabSelectionEvent;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 26.08.14
 * Time: 11:21
 */
@RunWith(GwtMockitoTestRunner.class)
public class TabsTest {
    @Mock ComponentResources resources;
    @Mock CssComponents cssComponents;
    @Mock Element element;
    @Mock Style style;

    @GwtMock FlowPanel root;

    private Tabs tabs;
    @Before
    public void setUp() {
        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(cssComponents);

        when(root.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);
    }


    private void testVisibleTab(int panelLeft, int panelWidth,
                                int tabLeft, int tabWidth,
                                boolean expected) {
        Tab tab = mock(Tab.class);
        when(tab.getAbsoluteLeft()).thenReturn(tabLeft);
        when(tab.getOffsetWidth()).thenReturn(tabWidth);
        when(root.getAbsoluteLeft()).thenReturn(panelLeft);
        when(root.getOffsetWidth()).thenReturn(panelWidth);

        assertEquals(expected, tabs.isTabVisible(tab));
    }
    @Test
    public void testIsVisible() {
        tabs = new Tabs();
        testVisibleTab(0, 20, 0, 20, true);
        testVisibleTab(0, 20, 0, 30, false);
        testVisibleTab(0, 20, 20, 1, false);
        testVisibleTab(10, 20, 9, 2, false);
    }

    @Test
    public void testNoCloseButtonOnFirstAddedTab() {
        tabs = new Tabs();
        Tab tab = new Tab(true);
        tabs.addTab(tab);

        assertFalse(tab.hasCloseButton());
    }

    @Test
    public void testNoCloseButtonOnLastTab() {
        tabs = new Tabs();
        Tab tab1 = new Tab(true);
        Tab tab2 = new Tab(true);

        tabs.addTab(tab1);
        tabs.addTab(tab2);

        assertTrue(tab1.hasCloseButton());
        assertTrue(tab2.hasCloseButton());

        tabs.closeTab(tab1, false);
        assertFalse(tab2.hasCloseButton());
    }

    @Test
    public void testClosingActiveTab() {
        tabs = new Tabs();

        Tab[] tabsArray = new Tab[]{new Tab(), new Tab(), new Tab(), new Tab()};
        for (Tab tab : tabsArray) {
            tabs.addTab(tab);
        }

        tabsArray[2].setActive(true, true);
        assertTrue(tabsArray[2].isActive());

        tabsArray[2].close();
        assertTrue(tabsArray[3].isActive());

        tabsArray[3].close();
        assertTrue(tabsArray[1].isActive());
    }

    @Test
    public void testClosingTabEvent() {
        tabs = new Tabs();
        TabCloseEvent.Handler closingHandler = mock(TabCloseEvent.Handler.class);

        tabs.addTabCloseHandler(closingHandler);

        Tab tab = new Tab(true);
        tabs.addTab(tab);
        tab.close();

        ArgumentCaptor<TabCloseEvent> captor = ArgumentCaptor.forClass(TabCloseEvent.class);
        verify(closingHandler, times(1)).onTabClose(captor.capture());
        assertEquals(tab, captor.getValue().getTab());
    }

    @Test
    public void testSelectingTabEvent() {
        tabs = new Tabs();
        TabSelectionEvent.Handler selectionHandler = mock(TabSelectionEvent.Handler.class);

        tabs.addTabSelectionHandler(selectionHandler);

        Tab tab = new Tab(true);
        tabs.addTab(tab);
        tabs.selectTab(tab, true);
        tab.setActive(false, true);
        tab.setActive(true, true);

        ArgumentCaptor<TabSelectionEvent> captor = ArgumentCaptor.forClass(TabSelectionEvent.class);
        verify(selectionHandler, times(2)).onTabSelection(captor.capture());
        for (TabSelectionEvent event : captor.getAllValues()) {
            assertEquals(tab, event.getTab());
        }
    }

    //тривиальный тест на добавление кнопок скролла
    @Test
    public void testAddingScrollButtons() {
        when(root.getOffsetWidth()).thenReturn(200);
        when(element.getScrollWidth()).thenReturn(300);

        Tabs tabs = new Tabs();
        assertFalse(tabs.hasScrollButtons);

        tabs.addTab(new Tab());
        assertTrue(tabs.hasScrollButtons);
    }
}
