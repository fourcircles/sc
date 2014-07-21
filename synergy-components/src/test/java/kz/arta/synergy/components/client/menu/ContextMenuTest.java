package kz.arta.synergy.components.client.menu;

import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * User: vsl
 * Date: 18.07.14
 * Time: 11:28
 */
@RunWith(GwtMockitoTestRunner.class)
public class ContextMenuTest {
    @Mock
    ComponentResources resources;
    @Mock
    CssComponents css;


    ContextMenu menu;

    @Before
    public void setUp() {
        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(css);
        when(css.contextMenuItem()).thenReturn("contextMenuItem");

        menu = new ContextMenu() {
            @Override
            protected String getMainStyle() {
                return "style";
            }
        };
    }

    private void fillMenu(String... items) {
        menu.clearItems();
        for (String item: items) {
            if (item == null) {
                menu.addSeparator();
            } else {
                menu.addItem(item);
            }
        }
    }

    @Test
    public void testNext() {
        fillMenu(null, "1", "2", null);

        menu.selectedIndex = 1;
        assertEquals(2, menu.getNext());

        menu.selectedIndex = 2;
        assertEquals(1, menu.getNext());

        fillMenu(null, null, "5", null, null);
        menu.selectedIndex = 2;
        assertEquals(-1, menu.getNext());

        fillMenu(null, "3", "5", null, "6");
        menu.selectedIndex = 2;
        assertEquals(4, menu.getNext());
    }

    @Test
    public void testPrevious() {
        fillMenu(null, "1", "2", null);

        menu.selectedIndex = 1;
        assertEquals(2, menu.getPrevious());

        menu.selectedIndex = 2;
        assertEquals(1, menu.getPrevious());

        fillMenu(null, null, "5", null, null);
        menu.selectedIndex = 2;
        assertEquals(-1, menu.getPrevious());

        fillMenu("3", null, "5", null, null);
        menu.selectedIndex = 2;
        assertEquals(0, menu.getPrevious());

    }

}
