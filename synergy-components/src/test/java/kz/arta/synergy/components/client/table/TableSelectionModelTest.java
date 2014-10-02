package kz.arta.synergy.components.client.table;

import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.table.column.ArtaColumn;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 23.09.14
 * Time: 15:01
 */
@RunWith(GwtMockitoTestRunner.class)
public class TableSelectionModelTest {

    private TableSelectionModel<User> model;
    private static ResettableEventBus bus;
    private static ProvidesKey<User> keyProvider;

    @BeforeClass
    public static void setUp() {
        bus = new ResettableEventBus(new SimpleEventBus());
        keyProvider = new ProvidesKey<User>() {
            @Override
            public Object getKey(User item) {
                return item.getKey();
            }
        };
    }

    @Before
    public void setUpTest() {
        model = new TableSelectionModel<User>(bus, keyProvider);
    }

    @Test
    public void testFirstSelect() {
        SelectionChangeEvent.Handler handler = mock(SelectionChangeEvent.Handler.class);
        model.addSelectionChangeHandler(handler);

        User user = TestUtils.createUser(0);
        model.setSelected(user, true);
        verify(handler, times(1)).onSelectionChange(any(SelectionChangeEvent.class));
        assertEquals(user, model.getSelectedObject());
    }

    @Test
    public void testDuplicateSelect() {
        SelectionChangeEvent.Handler handler = mock(SelectionChangeEvent.Handler.class);
        model.addSelectionChangeHandler(handler);

        User user = TestUtils.createUser(0);
        model.setSelected(user, null, true, false); //выделение без события
        model.setSelected(user, null, true, true); //повторное выделение - события нет

        verify(handler, times(0)).onSelectionChange(any(SelectionChangeEvent.class));
        assertEquals(user, model.getSelectedObject());
    }

    @Test
    public void testDeselection() {
        SelectionChangeEvent.Handler handler = mock(SelectionChangeEvent.Handler.class);
        model.addSelectionChangeHandler(handler);

        User user = TestUtils.createUser(0);
        model.setSelected(user, null, true, false); //выделение без события
        model.setSelected(user, null, false, true); //снятие выделения

        verify(handler, times(1)).onSelectionChange(any(SelectionChangeEvent.class));
        assertNull(model.getSelectedObject());
    }

    @Test
    public void testSelectCell() {
        SelectionChangeEvent.Handler handler = mock(SelectionChangeEvent.Handler.class);
        model.addSelectionChangeHandler(handler);

        User user = TestUtils.createUser(0);
        ArtaColumn column1 = mock(ArtaColumn.class);
        ArtaColumn column2 = mock(ArtaColumn.class);

        model.setSelected(user, column1, true, false);
        assertEquals(column1, model.getSelectedColumn());

        model.setSelected(user, column2, true, true);
        assertEquals(column2, model.getSelectedColumn());

        verify(handler, times(1)).onSelectionChange(any(SelectionChangeEvent.class));
    }

    //выделена ячейка, снимаем выделение с ряда
    @Test
    public void testDeselectRow() {
        SelectionChangeEvent.Handler handler = mock(SelectionChangeEvent.Handler.class);
        model.addSelectionChangeHandler(handler);

        User user = TestUtils.createUser(0);
        ArtaColumn column = mock(ArtaColumn.class);

        model.setSelected(user, column, true, false);
        model.setSelected(user, false);

        assertNull(model.getSelectedColumn());
        assertNull(model.getSelectedObject());

        verify(handler, times(1)).onSelectionChange(any(SelectionChangeEvent.class));
    }
}
