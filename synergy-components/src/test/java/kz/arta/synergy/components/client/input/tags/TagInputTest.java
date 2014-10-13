package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.InputWithEvents;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.DropDownListMulti;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 15.08.14
 * Time: 9:44
 */
@RunWith(GwtMockitoTestRunner.class)
public class TagInputTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    TagInput<Integer> tagInput;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        tagInput = new TagInput<Integer>(true);
        DropDownListMulti<Integer> list = new DropDownListMulti<Integer>(mock(Widget.class), tagInput.innerBus);
        tagInput.setDropDownList(list);
    }

    private DropDownListMulti<Integer>.Item mockItem(Integer value) {
        DropDownListMulti<Integer>.Item item = mock(DropDownListMulti.Item.class);
        when(item.getValue()).thenReturn(value);
        return item;
    }

    @Test
    public void testSelectingItemsFromList() {
        DropDownList<Integer>.Item item1 = mockItem(1);
        DropDownList<Integer>.Item item2 = mockItem(2);

        TagAddEvent.Handler addHandler = mock(TagAddEvent.Handler.class);
        tagInput.addTagAddHandler(addHandler);

        tagInput.innerBus.fireEvent(new ListSelectionEvent<Integer>(item1));
        tagInput.innerBus.fireEvent(new ListSelectionEvent<Integer>(item2));

        ArgumentCaptor<TagAddEvent> eventCaptor = ArgumentCaptor.forClass(TagAddEvent.class);

        verify(addHandler, times(2)).onTagAdd(eventCaptor.capture());

        assertEquals(1, eventCaptor.getAllValues().get(0).getTag().getValue());
        assertEquals(2, eventCaptor.getAllValues().get(1).getTag().getValue());
    }

    @Test
    public void testDeselectingItemsFromList() {
        DropDownList<Integer>.Item item1 = mockItem(1);
        DropDownList<Integer>.Item item2 = mockItem(2);

        TagRemoveEvent.Handler removeHandler = mock(TagRemoveEvent.Handler.class);
        tagInput.addTagRemoveHandler(removeHandler);

        tagInput.innerBus.fireEvent(new ListSelectionEvent<Integer>(item1));
        tagInput.innerBus.fireEvent(new ListSelectionEvent<Integer>(item2));

        tagInput.innerBus.fireEvent(new ListSelectionEvent<Integer>(item1, ListSelectionEvent.ActionType.DESELECT));
        tagInput.innerBus.fireEvent(new ListSelectionEvent<Integer>(item2, ListSelectionEvent.ActionType.DESELECT));

        ArgumentCaptor<TagRemoveEvent> eventCaptor = ArgumentCaptor.forClass(TagRemoveEvent.class);

        verify(removeHandler, times(2)).onTagRemove(eventCaptor.capture());

        assertEquals(1, eventCaptor.getAllValues().get(0).getTag().getValue());
        assertEquals(2, eventCaptor.getAllValues().get(1).getTag().getValue());
    }

    @Test
    public void testAddTag() {
        Tag<Integer> tag1 = new Tag<Integer>("tag1", 1);
        Tag<Integer> tag2 = new Tag<Integer>("tag2", 2);

        tagInput.addTag(tag1);
        tagInput.addTag(tag2);

        assertTrue(tagInput.getTags().contains(tag1));
        assertTrue(tagInput.getTags().contains(tag2));
    }

    /**
     * Проверяет удаление тегов добавленных через события, а не через список
     */
    @Test
    public void testRemoveTags() {
        Tag<Integer> tag1 = new Tag<Integer>("tag1", 1);
        Tag<Integer> tag2 = new Tag<Integer>("tag2", 2);

        tagInput.addTag(tag1);
        tagInput.addTag(tag2);

        tagInput.removeTag(tag1);
        tagInput.removeTag(tag2);

        assertEquals(0, tagInput.getTags().size());
    }

    @Test
    public void testDummy() {
        tagInput.input = mock(InputWithEvents.class);
        when(tagInput.getText()).thenReturn("hello");
        tagInput.keyEnter();

        List<Tag<Integer>> tags = tagInput.getTags();
        assertEquals(1, tags.size());
        assertEquals("hello", tags.get(0).getText());
    }
}
