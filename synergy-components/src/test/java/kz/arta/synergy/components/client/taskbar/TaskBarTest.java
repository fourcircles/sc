package kz.arta.synergy.components.client.taskbar;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: vsl
 * Date: 07.10.14
 * Time: 14:09
 */
@RunWith(GwtMockitoTestRunner.class)
public class TaskBarTest {

    private Map<TaskBarItemTest, TaskBarItemUI> uis;
    private TaskBar taskBar;

    @GwtMock Utils utils;
    @GwtMock private ComponentResources resources;

    @Before
    public void setUp() {
        CssComponents cssComponents = mock(CssComponents.class);
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        uis = new HashMap<TaskBarItemTest, TaskBarItemUI>();
        taskBar = new TaskBar() {
            @Override
            TaskBarItemUI createItemUI(TaskBarItem item) {
                TaskBarItemUI ui = mock(TaskBarItemUI.class);
                uis.put((TaskBarItemTest) item, ui);
                return ui;
            }
        };
    }

    @Test
    public void testAdd() {
        TaskBarItemTest item = new TaskBarItemTest("hi");
        taskBar.addItem(item);
        taskBar.addItem(null);
        assertEquals(1, taskBar.size());
        assertTrue(taskBar.contains(item));
    }

    @Test
    public void testRemoveMissing() {
        taskBar.removeItem(-3);
        taskBar.removeItem(10);
        taskBar.removeItem(null);
        taskBar.removeItem(new TaskBarItemTest("missing"));

        assertEquals(0, taskBar.size());
    }

    @Test
    public void testRemoveNormal() {
        TaskBarItem item1 = new TaskBarItemTest("");
        TaskBarItem item2 = new TaskBarItemTest("");

        taskBar.addItem(item1);
        taskBar.addItem(item2);
        assertEquals(2, taskBar.size());

        taskBar.removeItem(0);
        assertFalse(taskBar.contains(item1));
        assertEquals(1, taskBar.size());

        taskBar.removeItem(item2);
        assertFalse(taskBar.contains(item2));
        assertEquals(0, taskBar.size());
    }

    @Test
    public void testClosingItem() {
        TaskBarItemTest item1 = new TaskBarItemTest("");

        taskBar.addItem(item1);
        item1.close();

        assertFalse(taskBar.contains(item1));
    }

    @Test
    public void testFitNormally() {
        List<TaskBarItemTest> items = Arrays.asList(
                new TaskBarItemTest(""),
                new TaskBarItemTest(""),
                new TaskBarItemTest(""),
                new TaskBarItemTest("")
        );

        for (TaskBarItem item : items) {
            taskBar.addItem(item);
        }

        for (TaskBarItemUI ui : uis.values()) {
            when(ui.getNormalWidth()).thenReturn(38.0);
        }

        int width = 4 * 40 + Constants.TASKBAR_ITEM_PADDING * 3;

        taskBar.width = width;
        assertTrue(taskBar.fitNormally());

        taskBar.width = width - 0.5;
        assertFalse(taskBar.fitNormally());
    }
}
