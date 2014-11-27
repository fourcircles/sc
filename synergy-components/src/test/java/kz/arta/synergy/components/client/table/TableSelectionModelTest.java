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

import static org.junit.Assert.*;
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

        User user = TableTestUtils.createUser(0);
        model.setSelected(user, true);
        verify(handler, times(1)).onSelectionChange(any(SelectionChangeEvent.class));
        assertEquals(1, model.getSelectedObjects().size());
        assertEquals(1, model.getSelectedColumns(user).size());
        assertTrue(model.isSelected(user, null));
    }

    @Test
    public void testDuplicateSelect() {
        SelectionChangeEvent.Handler handler = mock(SelectionChangeEvent.Handler.class);
        model.addSelectionChangeHandler(handler);

        User user = TableTestUtils.createUser(0);
        model.setSelected(user, null, true, false); //выделение без события
        model.setSelected(user, null, true, true); //повторное выделение - события нет

        verify(handler, times(0)).onSelectionChange(any(SelectionChangeEvent.class));
        assertTrue(model.isSelected(user, null));
    }

    @Test
    public void testDeselection() {
        SelectionChangeEvent.Handler handler = mock(SelectionChangeEvent.Handler.class);
        model.addSelectionChangeHandler(handler);

        User user = TableTestUtils.createUser(0);
        model.setSelected(user, null, true, false); //выделение без события
        model.setSelected(user, null, false, true); //снятие выделения

        verify(handler, times(1)).onSelectionChange(any(SelectionChangeEvent.class));

        assertTrue(model.getSelectedObjects().isEmpty());
        assertFalse(model.isSelected(user, null));
    }

    @Test
    public void testSelectCell() {
        SelectionChangeEvent.Handler handler = mock(SelectionChangeEvent.Handler.class);
        model.addSelectionChangeHandler(handler);

        User user = TableTestUtils.createUser(0);
        ArtaColumn column1 = mock(ArtaColumn.class);
        ArtaColumn column2 = mock(ArtaColumn.class);

        model.setSelected(user, column1, true, false);
        assertEquals(1, model.getSelectedColumns(user).size());
        assertTrue(model.getSelectedColumns(user).contains(column1));

        model.setSelected(user, column2, true, true);
        assertEquals(2, model.getSelectedColumns(user).size());
        assertTrue(model.getSelectedColumns(user).contains(column1));

        verify(handler, times(1)).onSelectionChange(any(SelectionChangeEvent.class));
    }

    @Test
    public void testMultipleSelection() {
        User user1 = TableTestUtils.createUser(0);
        User user2 = TableTestUtils.createUser(0);

        ArtaColumn column1 = mock(ArtaColumn.class);
        ArtaColumn column2 = mock(ArtaColumn.class);

        model.setSelected(user1, null, true, false);
        model.setSelected(user2, column1, true, false);
        model.setSelected(user2, column2, true, false);

        assertTrue(model.isSelected(user1, null));

        //строка не выделена
        assertFalse(model.isSelected(user2, null));
        assertTrue(model.isSelected(user2, column1));
        assertTrue(model.isSelected(user2, column2));
    }

    @Test
    public void testClear() {
        User user1 = TableTestUtils.createUser(0);
        User user2 = TableTestUtils.createUser(0);

        ArtaColumn column1 = mock(ArtaColumn.class);
        ArtaColumn column2 = mock(ArtaColumn.class);

        model.setSelected(user1, null, true, false);
        model.setSelected(user2, column1, true, false);
        model.setSelected(user2, column2, true, false);

        model.clear();

        assertTrue(model.getSelectedObjects().isEmpty());
        assertFalse(model.isSelected(user1));
        assertFalse(model.isSelected(user2, column1));
        assertFalse(model.isSelected(user2, column2));
    }
}
