package kz.arta.synergy.components.client.input.tags;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.InputWithEvents;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 15.08.14
 * Time: 9:44
 *
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
    }

    private MenuItem<Integer> mockItem(Integer value) {
        MenuItem<Integer> item = mock(MenuItem.class);
        when(item.getUserValue()).thenReturn(value);
        return item;
    }

    @Test
    public void testSelectingItemsFromList() {
        tagInput.addListItem(1, "");
        tagInput.addListItem(2, "");

        MenuItem<Tag<Integer>> item1 = tagInput.getListItem(1);
        MenuItem<Tag<Integer>> item2 = tagInput.getListItem(2);

        TagAddEvent.Handler addHandler = mock(TagAddEvent.Handler.class);
        tagInput.addTagAddHandler(addHandler);

        tagInput.onListSelection(item1, true);
        tagInput.onListSelection(item2, true);

        ArgumentCaptor<TagAddEvent> eventCaptor = ArgumentCaptor.forClass(TagAddEvent.class);

        verify(addHandler, times(2)).onTagAdd(eventCaptor.capture());

        assertEquals(1, eventCaptor.getAllValues().get(0).getTag().getValue());
        assertEquals(2, eventCaptor.getAllValues().get(1).getTag().getValue());

        assertEquals(2, tagInput.getTags().size());
    }

    @Test
    public void testDeselectingItemsFromList() {
        tagInput.addListItem(1, "");
        tagInput.addListItem(2, "");

        TagRemoveEvent.Handler removeHandler = mock(TagRemoveEvent.Handler.class);
        tagInput.addTagRemoveHandler(removeHandler);

        MenuItem<Tag<Integer>> item1 = tagInput.getListItem(1);
        MenuItem<Tag<Integer>> item2 = tagInput.getListItem(2);

        tagInput.onListSelection(item1, true);
        tagInput.onListSelection(item2, true);

        tagInput.onListSelection(item1, false);
        tagInput.onListSelection(item2, false);

        ArgumentCaptor<TagRemoveEvent> eventCaptor = ArgumentCaptor.forClass(TagRemoveEvent.class);

        verify(removeHandler, times(2)).onTagRemove(eventCaptor.capture());

        assertEquals(1, eventCaptor.getAllValues().get(0).getTag().getValue());
        assertEquals(2, eventCaptor.getAllValues().get(1).getTag().getValue());

        assertEquals(0, tagInput.getTags().size());
    }

    @Test
    public void testAddTag() {
        Tag<Integer> tag1 = new Tag<Integer>("tag1", 1);
        Tag<Integer> tag2 = new Tag<Integer>("tag2", 2);

        tagInput.addTag(tag1);
        tagInput.addTag(tag2);

        assertTrue(tagInput.getTags().contains(tag1));
        assertTrue(tagInput.getTags().contains(tag2));
        assertEquals(2, tagInput.getTags().size());
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

    /**
     * Проверяет правильное добавление тегов через клавишу 'Enter'.
     */
    @Test
    public void testEnterKey() {
        tagInput.setListEnabled(true);
        tagInput.input = mock(InputWithEvents.class);
        when(tagInput.getText()).thenReturn("hello");

        tagInput.keyEnter();

        assertEquals(0, tagInput.getTags().size());

        tagInput.setListEnabled(false);

        tagInput.keyEnter();

        assertEquals(1, tagInput.getTags().size());
        assertEquals("hello", tagInput.getTags().get(0).getText());
    }
}
