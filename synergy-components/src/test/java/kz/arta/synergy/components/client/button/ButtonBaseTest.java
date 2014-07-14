package kz.arta.synergy.components.client.button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: user
 * Date: 04.07.14
 * Time: 10:24
 */
@RunWith(GwtMockitoTestRunner.class)
public class ButtonBaseTest {

    ButtonBase buttonBase;
    ButtonBase buttonBaseSpy;

    ClickHandler clickHandler;

    @GwtMock
    ComponentResources resources;

    @GwtMock
    CssComponents css;

    @GwtMock
    GradientLabel gradientLabel;

    @Before
    public void setUp() {

        SynergyComponents.resources = resources;
        when(resources.cssComponents()).thenReturn(css);
        when(css.disabled()).thenReturn("disabled");

        clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                buttonBase.setText("clicked");
            }
        };

        buttonBase = new ButtonBase();
        buttonBaseSpy = Mockito.spy(buttonBase);

        // Generate a new mocking click handler
        HasClickHandlersMock hasClickHandlers = new HasClickHandlersMock();

        when(buttonBaseSpy.getButton()).thenReturn(hasClickHandlers);
        buttonBaseSpy.getButton().addClickHandler(clickHandler);
    }



    @Test
    public void getText_test() {
        buttonBase.setText("Button text");
        verify(gradientLabel).setText("Button text");
        assertEquals("Button text", buttonBase.getText());
    }

    @Test
    public void click_test() {
        buttonBaseSpy.getButton().fireEvent(new ClickEventMock());
        assertEquals("clicked", buttonBase.getText());
    }

    @Test
    public void setEnabled_test() {
        buttonBase.setEnabled(false);
        assertEquals(false, buttonBase.isEnabled());
        buttonBase.setEnabled(true);
        assertEquals(true, buttonBase.isEnabled());
    }

}
