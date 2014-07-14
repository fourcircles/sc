package kz.arta.synergy.components.client.dialog;

import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ButtonBase;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: vsl
 * Date: 04.07.14
 * Time: 15:18
 */
@RunWith(GwtMockitoTestRunner.class)
public class DialogTest {
    @Mock
    Widget content;

    Dialog dialog;
    Dialog spyDialog;

    @GwtMock
    FlowPanel flowPanel;

    @GwtMock
    ComponentResources resources;
    @Mock Element element;
    @Mock Style style;

    @GwtMock
    CssComponents css;

    @Mock SimpleButton leftButton;
    @Mock SimpleButton saveButton;
    @Mock SimpleButton rightButton;

    private boolean isAttached = true;

    @Before
    public void setUp() {
        SynergyComponents.resources = resources;

        when(element.getStyle()).thenReturn(style);

        when(resources.cssComponents()).thenReturn(css);
        when(css.popupPanel()).thenReturn("");
        when(css.dialog()).thenReturn("");

        when(leftButton.getElement()).thenReturn(element);
        when(rightButton.getElement()).thenReturn(element);
        when(saveButton.getElement()).thenReturn(element);

        dialog = new Dialog() {
            @Override
            SimpleButton makeButton(String title) {
                return saveButton;
            }

            int makeButtonCnt = 0;
            @Override
            SimpleButton makeButton(String title, ImageResource img, ButtonBase.IconPosition position) {
                if (makeButtonCnt++ == 0) {
                    return DialogTest.this.leftButton;
                } else {
                    return DialogTest.this.rightButton;
                }
            }

            @Override
            public boolean isAttached() {
                return DialogTest.this.isAttached;
            }
        };

        spyDialog = spy(dialog);
    }

    @Test
    public void testAdjustMargin() {
        when(leftButton.isVisible()).thenReturn(true);
        when(leftButton.getOffsetWidth()).thenReturn(50);

        when(rightButton.isVisible()).thenReturn(true);
        when(rightButton.getOffsetWidth()).thenReturn(100);

        when(saveButton.getOffsetWidth()).thenReturn(20);

        when(spyDialog.getOffsetWidth()).thenReturn(100);

        spyDialog.adjustSaveButtonMargin();

        verify(spyDialog).setWidth((100 + 40) * 2 + 20 + "px");
    }

    @Test
    public void testAdjustMarginNoAdjust() {
        when(leftButton.isVisible()).thenReturn(true);
        when(leftButton.getOffsetWidth()).thenReturn(50);

        when(rightButton.isVisible()).thenReturn(true);
        when(rightButton.getOffsetWidth()).thenReturn(100);

        when(saveButton.getOffsetWidth()).thenReturn(20);

        when(spyDialog.getOffsetWidth()).thenReturn(300);

        verify(spyDialog, never()).setWidth(anyString());
    }
}
