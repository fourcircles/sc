package kz.arta.synergy.components.client.menu;

import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.TestUtils;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 22.10.14
 * Time: 14:37
 */
@RunWith(GwtMockitoTestRunner.class)
public class ContextMenuTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    private List<MenuItem<Command>> items;
    private ContextMenu menu;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        menu = new ContextMenu();
        items = new ArrayList<MenuItem<Command>>();

        for (int i = 0; i < 10; i++) {
            Command command = mock(Command.class);
            MenuItem<Command> item = new MenuItem<Command>(command, "");
            items.add(item);
            menu.add(item);
        }
    }

    @Test
    public void testSelection() {
        items.get(0).click();
        verify(items.get(0).getUserValue(), times(1)).execute();
    }

    @Test
    public void testSelectionNull() {
        MenuItem<Command> nullItem = new MenuItem<Command>(null, "null");
        items.add(nullItem);

        nullItem.fireEvent(TestUtils.createClickEvent(nullItem));
    }

    /**
     * В контекстном меню не может быть выбранных элементов.
     */
    @Test
    public void testNoSelected() {
        items.get(0).fireEvent(TestUtils.createClickEvent(null));

        for (MenuItem<Command> item : menu.getItems()) {
            assertFalse(item.isSelected());
        }
    }

    /**
     * После выбора элемента не может быть сфокусированных
     */
    @Test
    public void testNoFocusedAfterSelection() {
        menu.keyDown();
        menu.keyDown();
        assertEquals(items.get(1), menu.getFocusedItem());

        menu.keyEnter(null);
        verify(items.get(1).getUserValue(), times(1)).execute();
        assertNull(menu.getFocusedItem());
    }
}
