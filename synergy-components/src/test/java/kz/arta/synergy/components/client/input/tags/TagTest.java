package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Image;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 15.08.14
 * Time: 11:31
 */
@RunWith(GwtMockitoTestRunner.class)
public class TagTest {
    @GwtMock Image image;

    @Mock ComponentResources resources;
    @Mock CssComponents cssComponents;

    @Before
    public void setUp() {
        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(cssComponents);
    }

    @Test
    public void testClosingEvent() {
        ArgumentCaptor<ClickHandler> clickHandlerCaptor = ArgumentCaptor.forClass(ClickHandler.class);

        when(image.addClickHandler(clickHandlerCaptor.capture())).thenReturn(null);
        Tag<String> tag = new Tag<String>("tag text", "value");

        EventBus bus = new SimpleEventBus();
        tag.setBus(bus);

        TagRemoveEvent.Handler removeHandler = mock(TagRemoveEvent.Handler.class);
        TagRemoveEvent.register(bus, removeHandler);

        //клик по кресту
        clickHandlerCaptor.getValue().onClick(null);
        tag.setEnabled(false);
        //клик по кресту
        clickHandlerCaptor.getValue().onClick(null);


        ArgumentCaptor<TagRemoveEvent> captor = ArgumentCaptor.forClass(TagRemoveEvent.class);
        //только один раз
        verify(removeHandler, times(1)).onTagRemove(captor.capture());
        //правильный тег
        assertEquals(tag, captor.getValue().getTag());
    }
}
