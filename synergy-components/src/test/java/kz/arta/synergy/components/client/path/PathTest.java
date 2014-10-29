package kz.arta.synergy.components.client.path;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
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
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PathTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    private PathItem pathItem;
    private Path path;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        path = new Path();
        pathItem = new PathItem("hello");
    }

    @Test
    public void trivial() {
        assertEquals(0, path.root.getWidgetCount());
    }

    private List<PathItem> mockRoot(Path path, PathItem ... items) {
        final List<PathItem> rootItems = new ArrayList<PathItem>();
        path.root = mock(FlowPanel.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                PathItem item = (PathItem) invocationOnMock.getArguments()[0];
                int index = (Integer) invocationOnMock.getArguments()[1];
                rootItems.add(index, item);
                return null;
            }
        }).when(path.root).insert(any(Widget.class), anyInt());

        Answer removeAnswer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                PathItem item = (PathItem) invocationOnMock.getMock();
                rootItems.remove(item);
                return null;
            }
        };
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                // +1 потому что есть кнопка
                return rootItems.size() + 1;
            }
        }).when(path.root).getWidgetCount();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return rootItems.get((Integer) invocationOnMock.getArguments()[0]);
            }
        }).when(path.root).getWidget(anyInt());

        for (int i = 0; i < items.length; i++) {
            doAnswer(removeAnswer).when(items[i]).removeFromParent();
        }

        return rootItems;
    }

    @Test
    public void testSetPath() {
        PathItem item1 = spy(new PathItem(""));
        when(item1.getAbsoluteTop()).thenReturn(22);
        PathItem item2 = spy(new PathItem(""));
        when(item2.getAbsoluteTop()).thenReturn(22);
        PathItem item3 = spy(new PathItem(""));
        when(item3.getAbsoluteTop()).thenReturn(20);
        PathItem item4 = spy(new PathItem(""));
        when(item4.getAbsoluteTop()).thenReturn(20);
        PathItem item5 = spy(new PathItem(""));
        when(item5.getAbsoluteTop()).thenReturn(20);

        List<PathItem> rootItems = mockRoot(path, item1, item2, item3, item4, item5);

        path.setPath(Arrays.asList(item1, item2, item3, item4, item5));

        assertEquals(3, rootItems.size());
    }

    @Test
    public void testSetPathFull() {
        PathItem item1 = spy(new PathItem(""));
        when(item1.getAbsoluteTop()).thenReturn(22);
        PathItem item2 = spy(new PathItem(""));
        when(item2.getAbsoluteTop()).thenReturn(22);
        PathItem item3 = spy(new PathItem(""));
        when(item3.getAbsoluteTop()).thenReturn(22);

        List<PathItem> rootItems = mockRoot(path, item1, item2, item3);
        path.setPath(Arrays.asList(item1, item2, item3));

        assertEquals(3, rootItems.size());
    }

    @Test
    public void testClear() {
        PathItem item1 = spy(new PathItem(""));
        when(item1.getAbsoluteTop()).thenReturn(22);
        PathItem item2 = spy(new PathItem(""));
        when(item2.getAbsoluteTop()).thenReturn(22);
        PathItem item3 = spy(new PathItem(""));
        when(item3.getAbsoluteTop()).thenReturn(22);

        List<PathItem> rootItems = mockRoot(path, item1, item2, item3);
        path.setPath(Arrays.asList(item1, item2, item3));

        assertEquals(3, rootItems.size());

        path.clear();
        assertEquals(0, rootItems.size());
    }

    //test clear

}