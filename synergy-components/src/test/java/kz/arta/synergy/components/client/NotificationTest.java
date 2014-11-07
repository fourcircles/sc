package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 06.11.14
 * Time: 12:19
 */
@SuppressWarnings("NonJREEmulationClassesInClientCode")
@RunWith(GwtMockitoTestRunner.class)
public class NotificationTest {
    @GwtMock ComponentResources resources;
    @GwtMock Image icon;

    @Mock CssComponents cssComponents;

    @GwtMock ImageResources imageResources;
    @Mock ImageResource success;
    @Mock ImageResource failure;
    @Mock ImageResource warning;
    @Mock ImageResource question;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();
    }

    @Test
    public void testIconImageSuccess() {
        new Notification("", Notification.Type.SUCCESS);

        ArgumentCaptor<ImageResource> captor = ArgumentCaptor.forClass(ImageResource.class);
        Mockito.verify(icon, times(1)).setResource(captor.capture());
        assertEquals("notificationSuccess", captor.getValue().getName());
    }

    @Test
    public void testIconImageFailure() {
        new Notification("", Notification.Type.FAILURE);

        ArgumentCaptor<ImageResource> captor = ArgumentCaptor.forClass(ImageResource.class);
        Mockito.verify(icon, times(1)).setResource(captor.capture());
        assertEquals("notificationFailure", captor.getValue().getName());
    }

    @Test
    public void testIconImageQuestion() {
        new Notification("", Notification.Type.QUESTION);

        ArgumentCaptor<ImageResource> captor = ArgumentCaptor.forClass(ImageResource.class);
        Mockito.verify(icon, times(1)).setResource(captor.capture());
        assertEquals("notificationQuestion", captor.getValue().getName());
    }

    @Test
    public void testIconImageWarning() {
        new Notification("", Notification.Type.WARNING);

        ArgumentCaptor<ImageResource> captor = ArgumentCaptor.forClass(ImageResource.class);
        Mockito.verify(icon, times(1)).setResource(captor.capture());
        assertEquals("notificationYellowQuestion", captor.getValue().getName());
    }

    @Test
    public void testIconNoImage() {
        Element iconElement = mock(Element.class);
        when(icon.getElement()).thenReturn(iconElement);
        when(iconElement.getStyle()).thenReturn(mock(Style.class));

        new Notification("", Notification.Type.NEUTRAL);

        ArgumentCaptor<ImageResource> captor = ArgumentCaptor.forClass(ImageResource.class);
        Mockito.verify(icon, times(0)).setResource(captor.capture());
    }

    @Test
    public void testVerticalAlignment() {
        Element contentContainerElement = mock(Element.class);
        Element iconElement = mock(Element.class);
        Style contentContainerStyle = mock(Style.class);
        Style iconStyle = mock(Style.class);
        when(contentContainerElement.getStyle()).thenReturn(contentContainerStyle);
        when(iconElement.getStyle()).thenReturn(iconStyle);

        Notification notification = new Notification("", Notification.Type.SUCCESS);
        notification.contentContainer = mock(SimplePanel.class);
        when(notification.contentContainer.getElement()).thenReturn(contentContainerElement);
        when(notification.icon.getElement()).thenReturn(iconElement);

        when(notification.contentContainer.getOffsetHeight()).thenReturn(40);

        notification.align();

        verify(contentContainerStyle, times(0)).setMarginTop(anyDouble(), any(Style.Unit.class));
        verify(iconStyle, times(1)).setMarginTop(7, Style.Unit.PX);
    }

    /**
     * Если контект -- одна строка текста, то он должен смещаться вниз, а не картинка.
     */
    @Test
    public void testVerticalAlignmentOneLine() {
        Element contentContainerElement = mock(Element.class);
        Element iconElement = mock(Element.class);
        Style contentContainerStyle = mock(Style.class);
        Style iconStyle = mock(Style.class);
        when(contentContainerElement.getStyle()).thenReturn(contentContainerStyle);
        when(iconElement.getStyle()).thenReturn(iconStyle);

        Notification notification = new Notification("", Notification.Type.SUCCESS);
        notification.contentContainer = mock(SimplePanel.class);
        when(notification.contentContainer.getElement()).thenReturn(contentContainerElement);
        when(notification.icon.getElement()).thenReturn(iconElement);

        when(notification.contentContainer.getOffsetHeight()).thenReturn(20);

        notification.align();

        verify(iconStyle, times(0)).setMarginTop(anyDouble(), any(Style.Unit.class));
        verify(contentContainerStyle, times(1)).setMarginTop(3, Style.Unit.PX);
    }

    @Test
    public void testToSafeHtmlSingleString() {
        SafeHtml safeHtml = Notification.toSafeHtml("hello");
        assertEquals("hello", safeHtml.asString());
    }

    @Test
    public void testToSafeHtml() {
        SafeHtml safeHtml = Notification.toSafeHtml("hello", null, Arrays.asList("message1", "message2", "message3"));
        String html = safeHtml.asString();
        assertTrue(html.contains("message1"));
        assertTrue(html.contains("message2"));
        assertTrue(html.contains("message3"));

        Pattern pattern = Pattern.compile(Notification.LIST_ITEM_START + ".+?" + Notification.LIST_ITEM_END);
        Matcher matcher = pattern.matcher(html);
        int countLi = 0;
        while(matcher.find()) {
            countLi++;
        }
        assertEquals(3, countLi);
    }

    @Test
    public void testToSafeHtmlEmptyList() {
        SafeHtml safeHtml = Notification.toSafeHtml("hello", null, new ArrayList<String>());
        String html = safeHtml.asString();
        assertTrue(html.contains(html));
        //no list
        assertFalse(html.contains(Notification.LIST_ITEM_START));
        assertFalse(html.contains(Notification.LIST_START));
    }
}
