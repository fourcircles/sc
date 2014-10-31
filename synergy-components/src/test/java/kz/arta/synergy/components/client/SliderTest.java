package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SliderTest {

    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;
    private Slider spy;
    private Style valueLineStyle;

    @Before
    public void setUp() throws Exception {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();

        Slider slider = new Slider(true);

        SimplePanel valueLine = mock(SimplePanel.class);
        Element valueLineElement = mock(Element.class);
        valueLineStyle = mock(Style.class);

        when(valueLine.getElement()).thenReturn(valueLineElement);
        when(valueLineElement.getStyle()).thenReturn(valueLineStyle);

        slider.valueLine = valueLine;

        spy = spy(slider);
        when(spy.getAbsoluteLeft()).thenReturn(100);
        when(spy.getOffsetWidth()).thenReturn(200);

    }

    @Test
    public void testClickInside() {
        ClickEvent click = mock(ClickEvent.class);
        when(click.getClientX()).thenReturn(150);
        spy.click(click);
        assertEquals(Double.valueOf(0.25), spy.getValue());
    }

    @Test
    public void testClickFarLeft() {
        ClickEvent click = mock(ClickEvent.class);
        when(click.getClientX()).thenReturn(100);
        spy.click(click);
        assertEquals(Double.valueOf(0), spy.getValue());
    }

    @Test
    public void testClickFarRight() {
        ClickEvent click = mock(ClickEvent.class);
        when(click.getClientX()).thenReturn(300);
        spy.click(click);
        assertEquals(Double.valueOf(1.0), spy.getValue());
    }

    @Test
    public void testMouseMove() {
        spy.dragging = true;

        MouseMoveEvent move = mock(MouseMoveEvent.class);
        when(move.getClientX()).thenReturn(200);
        spy.circleMouseMove(move);

        assertEquals(Double.valueOf(0.5), spy.getValue());
    }

    @Test
    public void testMouseMoveOverRightBorder() {
        spy.dragging = true;

        MouseMoveEvent move = mock(MouseMoveEvent.class);
        when(move.getClientX()).thenReturn(1000);
        spy.circleMouseMove(move);

        assertEquals(Double.valueOf(1.0), spy.getValue());
    }

    @Test
    public void testMouseMoveOverLeftBorder() {
        spy.dragging = true;

        MouseMoveEvent move = mock(MouseMoveEvent.class);
        when(move.getClientX()).thenReturn(20);
        spy.circleMouseMove(move);

        assertEquals(Double.valueOf(0.0), spy.getValue());
    }

    @Test
    public void testSetValue() {
        double value = 0.666;
        spy.setValue(value);
        assertEquals(Double.valueOf(value), spy.getValue());

        verify(valueLineStyle, times(1)).setWidth(value * 100, Style.Unit.PCT);
    }

    @Test
    public void testSetBigValue() {
        spy.setValue(6.66);
        assertEquals(Double.valueOf(1.0), spy.getValue());
        verify(valueLineStyle, times(1)).setWidth(1.0 * 100, Style.Unit.PCT);
    }

    @Test
    public void testSetNegativeValue() {
        spy.setValue(-0.2);
        assertEquals(Double.valueOf(0.0), spy.getValue());
        verify(valueLineStyle, times(1)).setWidth(0.0 * 100, Style.Unit.PCT);
    }
}