package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.FilterUpdateEvent;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.menu.filters.ListFilter;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 13.08.14
 * Time: 14:04
 */
@RunWith(GwtMockitoTestRunner.class)
public class DropDownListTest {

    @Mock ComponentResources resources;
    @Mock CssComponents cssComponents;

    @Mock private Widget relativeWidget;

    private ResettableEventBus bus;

    private DropDownList<Integer> intList;
    private ArrayList<DropDownList<Integer>.Item> intItems;

    @Before
    public void setUp() {
        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(cssComponents);
        when(cssComponents.selected()).thenReturn("selected");

        bus = new ResettableEventBus(new SimpleEventBus());

        intList = new DropDownList<Integer>(relativeWidget, bus);
        intItems = new ArrayList<DropDownList<java.lang.Integer>.Item>();
        intItems.add(intList.addItem("zero", 0));
        intItems.add(intList.addItem("one", 1));
        intItems.add(intList.addItem("two", 2));
        intItems.add(intList.addItem("three", 3));
    }

    @Test
    public void testFocusValue() {
        bus.removeHandlers();

        intItems.get(0).focus();
        assertEquals(intItems.get(0), intList.getFocusedItem());

        intList.focusValue(3);
        assertEquals(intItems.get(3), intList.getFocusedItem());

        intList.focusValue(42);
        assertEquals(intItems.get(3), intList.getFocusedItem());
    }

    @Test
    public void testAddDuplicateValue() {
        DropDownList<Integer> list = new DropDownList<Integer>();
        DropDownList.Item two = list.addItem("2", 2);
        assertTrue(list.contains(2));
        assertTrue(list.contains(two));

        DropDownList.Item anotherTwo = intList.addItem("another 2", 2);

        assertFalse(intList.contains(two));
        assertTrue(intList.contains(anotherTwo));
        assertTrue(intList.contains(2));
    }

    @Test
    public void testAdd() {
        DropDownList<String> stringList = new DropDownList<String>();
        stringList.addItem("one", "one");
        stringList.addItem("two", "two");

        assertTrue(stringList.contains("one"));
        assertTrue(stringList.contains("two"));
    }

    @Test
    public void testAddNull() {
        DropDownList<String> stringList = new DropDownList<String>();
        stringList.addItem("null1", null);

        assertTrue(stringList.contains((String) null));
        assertFalse(stringList.contains("notnull"));

        DropDownList.Item notNull = stringList.addItem("notnull", "notnull");
        assertTrue(stringList.contains("notnull"));

        assertEquals(notNull, stringList.get("notnull"));

        stringList.remove("notnull");

        assertFalse(stringList.contains("notnull"));
    }

    @Test
    public void testSelect() {
        ListSelectionEvent.Handler<Integer> handler = mock(ListSelectionEvent.Handler.class);
        bus.addHandlerToSource(ListSelectionEvent.TYPE, intList, handler);

        intList.selectValue(2);
        intList.selectValue(42);

        verify(handler, times(1)).onSelection(any(ListSelectionEvent.class));
    }

    @Test
    public void testRemove() {
        assertTrue(intList.contains(2));

        intList.remove(Integer.valueOf(2));
        intList.remove(Integer.valueOf(42));

        assertFalse(intList.contains(2));
    }

    @Test
    public void testSelectedValue() {
        DropDownList<String> mockList = new DropDownList<String>();
        DropDownList.Item item1 = mock(DropDownList.Item.class);
        when(item1.getValue()).thenReturn("2");
        DropDownList.Item item2 = mock(DropDownList.Item.class);
        when(item2.getValue()).thenReturn("3");

        mockList.items.add(item1);
        mockList.items.add(item2);

        mockList.setSelectedValue("2");

        mockList.setSelectedValue("3");

        InOrder order = inOrder(item1, item2);
        order.verify(item1).addStyleName(SynergyComponents.resources.cssComponents().selected());
        order.verify(item1).removeStyleName(SynergyComponents.resources.cssComponents().selected());
        order.verify(item2).addStyleName(SynergyComponents.resources.cssComponents().selected());
    }

    @Test
    public void testNullValue() {
        DropDownList<Integer>.Item firstNull = intList.addItem("first null", null);
        assertTrue(intList.contains(firstNull));
        assertEquals(firstNull, intList.get(null));

        //при добавлении второго null первый удаляется
        DropDownList<Integer>.Item secondNull = intList.addItem("second null", null);
        assertFalse(intList.contains(firstNull));
        assertTrue(intList.contains(secondNull));

        assertEquals(secondNull, intList.get(null));
    }
}
