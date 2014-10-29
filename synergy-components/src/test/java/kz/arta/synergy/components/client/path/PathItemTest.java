package kz.arta.synergy.components.client.path;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.stack.events.StackOpenEvent;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 28.10.14
 * Time: 13:37
 */
@RunWith(GwtMockitoTestRunner.class)
public class PathItemTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    private PathItem pathItem;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        pathItem = new PathItem("hello");
    }

    @Test
    public void testClick() {
        ClickHandler handler = mock(ClickHandler.class);
        pathItem.addClickHandler(handler);

        pathItem.fireEvent(TestUtils.createClickEvent(null));

        verify(handler, times(1)).onClick(any(ClickEvent.class));
    }
}
