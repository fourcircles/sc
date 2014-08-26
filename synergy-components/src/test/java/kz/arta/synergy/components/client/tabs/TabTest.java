package kz.arta.synergy.components.client.tabs;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.ArtaFlowPanel;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 26.08.14
 * Time: 10:53
 */
@RunWith(GwtMockitoTestRunner.class)
public class TabTest {
    @Mock ComponentResources resources;
    @Mock CssComponents cssComponents;

    @GwtMock ArtaFlowPanel root;
    @GwtMock Image closeImage;

    @Before
    public void setUp() {
        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(cssComponents);
    }

    @Test
    public void testSelectionEvent() {
        ArgumentCaptor<ClickHandler> clickCaptor = ArgumentCaptor.forClass(ClickHandler.class);
        when(root.addClickHandler(clickCaptor.capture())).thenReturn(null);

        Tab tab = new Tab();
        TabSelectionEvent.Handler selectionHandler = mock(TabSelectionEvent.Handler.class);
        tab.addTabSelectionHandler(selectionHandler);

        //клик по вкладке
        clickCaptor.getValue().onClick(null);

        verify(selectionHandler).onTabSelection(any(TabSelectionEvent.class));
    }

    @Test
    public void testCloseEvent() {
        ArgumentCaptor<ClickHandler> clickCaptor = ArgumentCaptor.forClass(ClickHandler.class);
        when(closeImage.addClickHandler(clickCaptor.capture())).thenReturn(null);

        Tab tab = new Tab();
        TabCloseEvent.Handler closeHandler = mock(TabCloseEvent.Handler.class);
        tab.addTabCloseHandler(closeHandler);

        //клик по картинке
        clickCaptor.getValue().onClick(null);

        ArgumentCaptor<TabCloseEvent> captor = ArgumentCaptor.forClass(TabCloseEvent.class);
        verify(closeHandler).onTabClose(captor.capture());
        assertEquals(tab, captor.getValue().getTab());
    }

    @Test
    public void testSetActive() {
        Tab tab = new Tab();

        TabSelectionEvent.Handler selectionHandler = mock(TabSelectionEvent.Handler.class);
        tab.addTabSelectionHandler(selectionHandler);

        tab.setActive(false, false);
        tab.setActive(false, true);
        tab.setActive(true, false);
        //только последний вызов должен создавать событие
        tab.setActive(true, true);

        ArgumentCaptor<TabSelectionEvent> captor = ArgumentCaptor.forClass(TabSelectionEvent.class);
        verify(selectionHandler, times(1)).onTabSelection(captor.capture());
        assertEquals(tab, captor.getValue().getTab());
    }


}
