package kz.arta.synergy.components.client.tree;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.tree.events.TreeOpenEvent;
import kz.arta.synergy.components.client.tree.events.TreeSelectionEvent;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 22.09.14
 * Time: 12:33
 */
@RunWith(GwtMockitoTestRunner.class)
public class TreeItemTest {
    private static final String SELECTED = "selected";

    @GwtMock ArtaFlowPanel panel;
    @GwtMock InlineLabel label;

    private TreeItem treeItem;
    private static ResettableEventBus bus;

    @BeforeClass
    public static void beforeClass() {
        ComponentResources resources = mock(ComponentResources.class);
        CssComponents cssComponents = mock(CssComponents.class);

        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(cssComponents);
        when(cssComponents.selected()).thenReturn(SELECTED);

        bus = new ResettableEventBus(new SimpleEventBus());
    }

    @Before
    public void setUp() {
        bus.removeHandlers();
        treeItem = new TreeItem("treeItem", bus);
    }

    @Test
    public void testSelect() {
        TreeSelectionEvent.Handler handler = mock(TreeSelectionEvent.Handler.class);

        bus.addHandler(TreeSelectionEvent.getType(), handler);

        treeItem.setSelected(true, false);
        assertTrue(treeItem.isSelected());

        treeItem.setSelected(true, true); //событие
        assertTrue(treeItem.isSelected());

        treeItem.setSelected(false, true);
        assertFalse(treeItem.isSelected());

        treeItem.setSelected(true, true); //событие
        assertTrue(treeItem.isSelected());

        ArgumentCaptor<TreeSelectionEvent> captor = ArgumentCaptor.forClass(TreeSelectionEvent.class);
        verify(handler, times(2)).onTreeSelection(captor.capture());
        assertEquals(treeItem, captor.getValue().getTreeItem());
    }

    @Test
    public void testUpdateHeight() {
        TreeItem spy = spy(new TreeItem("item", bus));
        TreeItem parent = mock(TreeItem.class);

        spy.setParent(parent);
        spy.updateContentHeight(20);

        verify(parent, times(1)).updateContentHeight(20);
    }

    @Test
    public void testSetOpen() {
        assertFalse(treeItem.isOpen()); //default

        FlowPanel content = mock(FlowPanel.class);
        Element contentElement = mock(Element.class);
        Style elementStyle = mock(Style.class);
        when(contentElement.getStyle()).thenReturn(elementStyle);
        when(content.getElement()).thenReturn(contentElement);
        when(contentElement.getScrollHeight()).thenReturn(20);

        treeItem.content = content;

        treeItem.setOpen(true, false);

        verify(elementStyle, times(1)).setDisplay(Style.Display.BLOCK);
        verify(elementStyle, times(1)).setHeight(20, Style.Unit.PX);

        treeItem.closeTimer = mock(Timer.class);
        treeItem.setOpen(false, false);

        verify(treeItem.closeTimer, times(1)).schedule(anyInt());
    }

    @Test
    public void testSetOpenEvents() {
        TreeOpenEvent.Handler handler = mock(TreeOpenEvent.Handler.class);
        bus.addHandler(TreeOpenEvent.getType(), handler);

        treeItem.setOpen(true);

        ArgumentCaptor<TreeOpenEvent> captor = ArgumentCaptor.forClass(TreeOpenEvent.class);
        verify(handler, times(1)).onTreeOpen(captor.capture());
        assertTrue(captor.getValue().isOpen());
        assertEquals(treeItem, captor.getValue().getItem());
        assertTrue(treeItem.isOpen());
    }

    @Test
    public void testSetCloseEvents() {
        TreeOpenEvent.Handler handler = mock(TreeOpenEvent.Handler.class);
        bus.addHandler(TreeOpenEvent.getType(), handler);

        treeItem.setOpen(true, false);
        treeItem.setOpen(false);

        ArgumentCaptor<TreeOpenEvent> captor = ArgumentCaptor.forClass(TreeOpenEvent.class);
        verify(handler, times(1)).onTreeOpen(captor.capture());
        assertFalse(captor.getValue().isOpen());
        assertEquals(treeItem, captor.getValue().getItem());
        assertFalse(treeItem.isOpen());
    }

    @Test
    public void testAddStrangeItem() {
        TreeItem newItem = new TreeItem("new", bus);

        treeItem.addTreeItem(newItem);
        treeItem.addTreeItem(newItem); //повторное добавление
        treeItem.addTreeItem(null);

        assertEquals(treeItem, newItem.getParent());
        assertEquals(1, treeItem.getItems().size());
        assertEquals(newItem, treeItem.getItems().get(0));
    }

    @Test
    public void testAddItem() {
        TreeItem newItem = new TreeItem("new", bus);
        assertNull(treeItem.getItems());

        treeItem.addTreeItem(newItem);
        assertEquals(1, treeItem.getItems().size());
        assertEquals(treeItem, newItem.getParent());
    }

    @Test
    public void testRemove() {
        TreeItem newItem = new TreeItem("new", bus);
        treeItem.addTreeItem(newItem);

        assertEquals(treeItem, newItem.getParent());

        treeItem.removeTreeItem(newItem);
        assertNull(newItem.getParent());
        assertEquals(0, treeItem.getItems().size());
    }

    @Test
    public void testRemoveStrangeItem() {
        TreeItem newItem = new TreeItem("new", bus);
        TreeItem anotherItem = new TreeItem("another", bus);

        treeItem.addTreeItem(newItem);

        treeItem.removeTreeItem(null);
        treeItem.removeTreeItem(anotherItem);

        assertEquals(1, treeItem.getItems().size());

        treeItem.removeTreeItem(newItem);
        assertEquals(0, treeItem.getItems().size());

        assertNull(newItem.getParent());
    }
}
