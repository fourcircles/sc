package kz.arta.synergy.components.client.taskbar;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 06.10.14
 * Time: 17:24
 */
@RunWith(GwtMockitoTestRunner.class)
public class TaskBarItemUITest {
    private static final String OPEN = "open";

    private TaskBarItemTest item;
    private TaskBarItemUI ui;

    @GwtMock private InlineLabel label;
    private String labelText = "";

    @BeforeClass
    public static void beforeClass() {
        ComponentResources resources = mock(ComponentResources.class);
        CssComponents cssComponents = mock(CssComponents.class);

        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(cssComponents);

        when(cssComponents.open()).thenReturn(OPEN);
    }

    @Before
    public void setUp() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                labelText = (String) invocationOnMock.getArguments()[0];
                return null;
            }
        }).when(label).setText(anyString());
        when(label.getText()).thenReturn(labelText);

        item = new TaskBarItemTest("test");
        ui = new TaskBarItemUI(item);
    }

    @Test
    public void testChangeModelText() {
        item.setText("different");
        verify(label).setText("different");
    }

    @Test
    public void testChangeModelEvent() {
        ModelChangeEvent.Handler handler = mock(ModelChangeEvent.Handler.class);
        ui.addModelChangeHandler(handler);

        item.fireEvent(new ModelChangeEvent());

        verify(handler, times(1)).onModelChange(any(ModelChangeEvent.class));
    }

    @Test
    public void testOpenClose() {
        final Set<String> styles = new HashSet<String>();

        ui = new TaskBarItemUI(item) {
            @Override
            public void addStyleName(String style) {
                styles.add(style);
            }

            @Override
            public void removeStyleName(String style) {
                styles.remove(style);
            }
        };

        item.open();
        assertTrue(styles.contains(OPEN));

        item.close();
        assertFalse(styles.contains(OPEN));
    }

    @Test
    public void testClickOpen() {
        item.setOpen(false);
        ui.click();
        assertTrue(item.isOpen());
    }

    @Test
    public void testClickClose() {
        item.setOpen(true);
        ui.click();
        assertFalse(item.isOpen());
    }
}
