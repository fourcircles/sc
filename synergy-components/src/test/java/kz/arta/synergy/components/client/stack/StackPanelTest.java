package kz.arta.synergy.components.client.stack;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 26.08.14
 * Time: 14:10
 */
@RunWith(GwtMockitoTestRunner.class)
public class StackPanelTest {
    @Mock ComponentResources resources;
    @Mock CssComponents cssComponents;

    @GwtMock InlineLabel label;

    private StackPanel createStackPanel(int height, String... titles) {
        SingleStack[] stacks = new SingleStack[titles.length];
        for (int i = 0; i < titles.length; i++) {
            stacks[i] = new SingleStack(titles[i]);
        }
        return new StackPanel(Arrays.asList(stacks), height);
    }

    @Before
    public void setUp() {
        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(cssComponents);
    }

    @Test
    public void testOpenEvent() {
        StackPanel panel = createStackPanel(300, "one", "two");

        StackOpenEvent.Handler handler = mock(StackOpenEvent.Handler.class);
        panel.addStackOpenHandler(handler);

        panel.openStack(0, false);
        //здесь события не будет потому что стек-панель уже выбрана
        panel.openStack(0, true);
        panel.openStack(1, true);

        ArgumentCaptor<StackOpenEvent> captor = ArgumentCaptor.forClass(StackOpenEvent.class);
        verify(handler, times(1)).onStackOpened(captor.capture());
        assertEquals(1, captor.getValue().getIndex());
    }

    @Test
    public void testOpenDisabled() {
        StackPanel panel = createStackPanel(300, "one", "two");

        StackOpenEvent.Handler handler = mock(StackOpenEvent.Handler.class);
        panel.addStackOpenHandler(handler);

        assertEquals(0, panel.getStacks().indexOf(panel.getOpenedStack()));

        panel.getStacks().get(1).setEnabled(false);
        panel.openStack(1, true);

        verify(handler, never()).onStackOpened(any(StackOpenEvent.class));
    }

    @Test
    public void testCloseOpened() {
        StackPanel panel = createStackPanel(300, "one", "two");

        assertTrue(panel.getStacks().get(0).isOpen());
        panel.openStack(1, false);
        assertFalse(panel.getStacks().get(0).isOpen());
        assertTrue(panel.getStacks().get(1).isOpen());
    }
}
