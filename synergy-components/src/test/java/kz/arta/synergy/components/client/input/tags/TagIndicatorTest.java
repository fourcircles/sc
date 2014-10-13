package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 15.08.14
 * Time: 12:46
 */
@RunWith(GwtMockitoTestRunner.class)
public class TagIndicatorTest {
    @GwtMock Label label;
    @GwtMock PopupPanel popupPanel;

    @Mock Element element;
    @Mock Style style;

    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    EventBus bus;
    TagIndicator indicator;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);

        when(label.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);

        bus = new SimpleEventBus();
        new SynergyComponents().onModuleLoad();
    }

    @Test
    public void testShowPopup() {
        ArgumentCaptor<ClickHandler> handlerCaptor = ArgumentCaptor.forClass(ClickHandler.class);
        when(label.addClickHandler(handlerCaptor.capture())).thenReturn(null);
        indicator = new TagIndicator(bus);

        ClickHandler handler = handlerCaptor.getValue();

        //срыт, но тегов нет
        when(popupPanel.isShowing()).thenReturn(false);
        handler.onClick(null);

        indicator.add(new Tag<String>("tag"));

        //показан, не пуст
        when(popupPanel.isShowing()).thenReturn(true);
        handler.onClick(null);

        //скрыт, не пуст
        when(popupPanel.isShowing()).thenReturn(false);
        handler.onClick(null);

        verify(popupPanel, times(1)).showRelativeTo(label);
    }

    @Test
    public void testEnableness() {
        ArgumentCaptor<ClickHandler> handlerCaptor = ArgumentCaptor.forClass(ClickHandler.class);
        when(label.addClickHandler(handlerCaptor.capture())).thenReturn(null);

        indicator = new TagIndicator(bus);
        indicator.add(new Tag<Object>("tag"));

        ClickHandler handler = handlerCaptor.getValue();

        when(popupPanel.isShowing()).thenReturn(false);

        indicator.setEnabled(true);
        handler.onClick(null);

        indicator.setEnabled(false);
        handler.onClick(null);

        verify(popupPanel, times(1)).showRelativeTo(any(UIObject.class));
    }
}
