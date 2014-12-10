package kz.arta.synergy.components.client.table;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.view.client.*;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.table.column.ArtaColumn;
import kz.arta.synergy.components.client.table.column.ArtaTextColumn;
import kz.arta.synergy.components.client.table.events.TableSortEvent;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kz.arta.synergy.components.client.table.TableTestUtils.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 22.09.14
 * Time: 16:05
 */
@RunWith(GwtMockitoTestRunner.class)
public class TableCoreTest {

    public static final int PAGE_SIZE = 20;
    private static ResettableEventBus bus;

    private TableCore<User> table;
    private ProvidesKey<User> keyProvider;

    @Mock private HTMLTable.RowFormatter rowFormatter;
    @Mock private FlexTable.FlexCellFormatter cellFormatter;
    @Mock private FlexTable htmlTable;
    @Mock private com.google.gwt.user.client.Element element;
    @Mock private Style style;

    private ListDataProvider<User> provider;
    private Pager pager;
    @GwtMock private ComponentResources resources;

    @BeforeClass
    public static void beforeClass() {
        bus = new ResettableEventBus(new SimpleEventBus());
    }

    @Before
    public void setUpTest() {
        CssComponents cssComponents = mock(CssComponents.class);

        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        bus.removeHandlers();
        keyProvider = new ProvidesKey<User>() {
            @Override
            public Object getKey(User item) {
                return item.getKey();
            }
        };
        table = new TableCore<User>(PAGE_SIZE, keyProvider, bus);
        table.table = htmlTable;
        when(htmlTable.getRowFormatter()).thenReturn(rowFormatter);
        when(htmlTable.getFlexCellFormatter()).thenReturn(cellFormatter);
        when(rowFormatter.getElement(anyInt())).thenReturn(element);
        when(cellFormatter.getElement(anyInt(), anyInt())).thenReturn(element);

        when(element.getStyle()).thenReturn(style);

        provider = new ListDataProvider<User>(keyProvider);
        provider.addDataDisplay(table);

        User.resetIdCount();
        provider.getList().addAll(createUserList(50, 0));
        provider.flush();
    }

    @Test
    public void testGetRowById() {
        assertEquals(15, table.getRowById(15));
        assertEquals(-1, table.getRowById(30));
        table.setVisibleRange(20, 20);
        assertEquals(0, table.getRowById(20));
    }

    @Test
    public void testSetVisibleRange() {
        RangeChangeEvent.Handler handler = mock(RangeChangeEvent.Handler.class);
        table.addRangeChangeHandler(handler);

        table.setVisibleRange(0, 5);
        assertEquals(new Range(0, 5), table.getVisibleRange());

        ArgumentCaptor<RangeChangeEvent> captor = ArgumentCaptor.forClass(RangeChangeEvent.class);
        verify(handler, times(1)).onRangeChange(captor.capture());
        assertEquals(new Range(0, 5), captor.getValue().getNewRange());
    }

    @Test
    public void testSetRowCount() {
        table.setRowCount(20);

        assertEquals(20, table.objects.size());
        assertEquals(20, table.getRowCount());

        table.setRowCount(70);

        assertEquals(70, table.objects.size());
        assertEquals(70, table.getRowCount());
    }

    @Test
    public void testChangeRowCountEvent() {
        RowCountChangeEvent.Handler handler = mock(RowCountChangeEvent.Handler.class);
        table.addRowCountChangeHandler(handler);

        table.setRowCount(50);
        table.setRowCount(20);

        ArgumentCaptor<RowCountChangeEvent> captor = ArgumentCaptor.forClass(RowCountChangeEvent.class);
        verify(handler, times(1)).onRowCountChange(captor.capture());
        assertEquals(20, captor.getValue().getNewRowCount());
    }

    @Test
    public void testRotate() {
        List<Integer> list = Arrays.asList(1, 2, 3);

        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(3, 1, 2);
        List<Integer> list3 = Arrays.asList(2, 3, 1);

        TableCore.rotate(list, true);
        assertEquals(list2, list);
        TableCore.rotate(list, true);
        assertEquals(list3, list);
        TableCore.rotate(list, true);
        assertEquals(list1, list);

        TableCore.rotate(list, false);
        assertEquals(list3, list);
        TableCore.rotate(list, false);
        assertEquals(list2, list);
        TableCore.rotate(list, false);
        assertEquals(list1, list);
    }

    /**
     * Подтверждает вызовы метода {@link TableCore#setRow(int, Object)}
     * @param start начальная строка (видимая) в таблице
     */
    private void verifySetRow(TableCore<User> table, int start, int length, List<User> users) {
        ArgumentCaptor<Integer> first = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<User> second = ArgumentCaptor.forClass(User.class);

        verify(table, times(length)).setRow(first.capture(), second.capture());

        for (int i = 0; i < length; i++) {
            assertEquals(Integer.valueOf(start + i), first.getAllValues().get(i));
            assertEquals(users.get(i), second.getAllValues().get(i));
        }
    }

    @Test
    public void testSetRowDataLeftBorder() {
        table.setVisibleRange(5, 20);
        TableCore<User> spy = spy(table);
        doNothing().when(spy).setRow(anyInt(), any(User.class));

        List<User> newUsers = createUserList(10, 500);
        spy.setRowData(0, newUsers);

        assertEquals(spy.objects.subList(0, 10), newUsers);
        verifySetRow(spy, 0, 5, newUsers.subList(5, 10));
    }

    @Test
    public void testSetRowDataRightBorder() {
        TableCore<User> spy = spy(table);
        doNothing().when(spy).setRow(anyInt(), any(User.class));

        List<User> newUsers = createUserList(10, 500);
        spy.setRowData(15, newUsers);

        assertEquals(spy.objects.subList(15, 25), newUsers);
        verifySetRow(spy, 15, 5, newUsers);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRowDataIntersect() {
        List<User> newUsers = createUserList(10, 500);
        table.setRowData(45, newUsers);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRowDataNoIntersect() {
        List<User> newUsers = createUserList(10, 500);
        table.setRowData(100, newUsers);
    }

    @Test
    public void testSort() {
        ArtaColumn<User> firstName = new ArtaTextColumn<User>("1") {
            @Override
            public String getValue(User object) {
                return object.getFirstName();
            }
        };
        ArtaColumn<User> spy = spy(firstName);

        Header header = mock(Header.class);
        when(header.isAscending()).thenReturn(Header.DEFAULT_IS_ASCENDING);

        when(spy.getHeader()).thenReturn(header);

        table.addColumn(spy);

        TableSortEvent.Handler<User> handler = mock(TableSortEvent.Handler.class);
        table.addSortHandler(handler);

        table.sort(spy);

        ArgumentCaptor<TableSortEvent> captor = ArgumentCaptor.forClass(TableSortEvent.class);
        verify(handler, times(1)).onSort(captor.capture());
        assertEquals(spy, captor.getValue().getColumn());
        assertEquals(Header.DEFAULT_IS_ASCENDING, captor.getValue().isAscending());
    }

    @Test
    public void testSetColumnWidth() {
        ArtaColumn<User> firstName = new ArtaTextColumn<User>("1") {
            @Override
            public String getValue(User object) {
                return object.getFirstName();
            }
        };
        table.addColumn(firstName);

        assertFalse(table.widths.containsKey(firstName));
        table.setColumnWidth(firstName, 80);
        assertEquals(Integer.valueOf(80), table.widths.get(firstName));
    }

    @Test
    public void testClearColumnWidth() {
        ArtaColumn<User> firstName = new ArtaTextColumn<User>("1") {
            @Override
            public String getValue(User object) {
                return object.getFirstName();
            }
        };
        table.addColumn(firstName);

        assertFalse(table.widths.containsKey(firstName));
        table.setColumnWidth(firstName, -1);
        assertEquals(Integer.valueOf(-1), table.widths.get(firstName));
    }

    private ArtaColumn<User> createEditableColumn(boolean isEditable) {
        ArtaColumn<User> column = mock(ArtaColumn.class);
        when(column.isEditable()).thenReturn(isEditable);
        return column;
    }

    @Test
    public void testNextEditableColumn() {
        List<ArtaColumn<User>> columns = new ArrayList<ArtaColumn<User>>();
        columns.add(createEditableColumn(false));
        columns.add(createEditableColumn(true));
        columns.add(createEditableColumn(false));
        columns.add(createEditableColumn(true));
        columns.add(createEditableColumn(false));

        for (ArtaColumn<User> column : columns) {
            table.addColumn(column);
        }
        assertEquals(3, table.getNextEditableColumn(1));
        assertEquals(-1, table.getNextEditableColumn(3));
        assertEquals(1, table.getNextEditableColumn(0));
        assertEquals(1, table.getNextEditableColumn(-1));
        assertEquals(-1, table.getNextEditableColumn(-2));
        assertEquals(-1, table.getNextEditableColumn(100));
    }

    @Test
    public void testPreviousEditableColumn() {
        ArtaColumn[] columns = new ArtaColumn[] {
                createEditableColumn(false),
                createEditableColumn(true),
                createEditableColumn(false),
                createEditableColumn(true),
                createEditableColumn(false)
        };
        for (ArtaColumn column : columns) {
            table.addColumn(column);
        }
        assertEquals(-1, table.getPreviousEditableColumn(1));
        assertEquals(1, table.getPreviousEditableColumn(3));
        assertEquals(3, table.getPreviousEditableColumn(4));
        assertEquals(-1, table.getPreviousEditableColumn(-1));
        assertEquals(-1, table.getPreviousEditableColumn(100));
    }

    @Test
    public void testNextVisibleObject() {
        table.setVisibleRange(10, PAGE_SIZE);
        User first = provider.getList().get(10);
        User last = provider.getList().get(10 + PAGE_SIZE - 1);
        assertEquals(first, table.getNextVisibleObject(last, true));
        assertEquals(last, table.getNextVisibleObject(first, false));

        assertEquals(provider.getList().get(11), table.getNextVisibleObject(first, true));
        assertEquals(provider.getList().get(10 + PAGE_SIZE - 2), table.getNextVisibleObject(last, false));
    }

    @Test
    public void testPositiveMod() {
        assertEquals(9, Utils.positiveMod(20, 11));
        assertEquals(1, Utils.positiveMod(-5, 2));
    }
}
