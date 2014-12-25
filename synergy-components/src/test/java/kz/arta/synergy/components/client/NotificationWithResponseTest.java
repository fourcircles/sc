package kz.arta.synergy.components.client;


import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.style.client.Constants;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.CssComponents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


/**
 * User: vsl
 * Date: 07.11.14
 * Time: 12:15
 */
@RunWith(GwtMockitoTestRunner.class)
public class NotificationWithResponseTest {
    @GwtMock ComponentResources resources;
    @Mock CssComponents cssComponents;

    @Before
    public void setUp() {
        when(resources.cssComponents()).thenReturn(cssComponents);
        new SynergyComponents().onModuleLoad();
    }

    /**
     * Выясняет ширину уведомления с небольшим контентом и заданным количеством кнопок.
     *
     * @param buttonsNumber количество кнопок
     * @return ширина уведомления
     */
    private int nButtonsNotificationWidth(int buttonsNumber) {
        if (buttonsNumber < 2 || buttonsNumber > 3) {
            throw new IllegalArgumentException("Количество кнопок от 2 до 3");
        }
        int width = NotificationWithResponse.BUTTON_WIDTH * buttonsNumber;
        width += 10 * (buttonsNumber + 1);
        width += Constants.BORDER_WIDTH * 2;
        return width;
    }

    /**
     * Заменяет контейнер контента, кнопку "да" и иконку на моки.
     * Заменяет их методы нахождения ширины на параметры.
     *
     * @param popupWidth общая ширина попапа уведомления
     * @param contentWidth ширина контейнера контента (иконка + текст)
     * @param hasCancel имеет ли уведомление кнопку "отмена"
     * @return уведомление с моками
     */
    private NotificationWithResponse mockNotification(int popupWidth, int contentWidth, boolean hasCancel) {
        NotificationWithResponse notification = new NotificationWithResponse("", Notification.Type.WARNING, hasCancel);

        notification.contentContainer = mock(SimplePanel.class);
        when(notification.contentContainer.getElement()).thenReturn(mock(Element.class));
        when(notification.contentContainer.getElement().getStyle()).thenReturn(mock(Style.class));

        notification.yesButton = mock(SimpleButton.class);
        when(notification.yesButton.getElement()).thenReturn(mock(Element.class));
        when(notification.yesButton.getElement().getStyle()).thenReturn(mock(Style.class));

        when(notification.icon.getElement()).thenReturn(mock(Element.class));
        when(notification.icon.getElement().getStyle()).thenReturn(mock(Style.class));

        notification.popup = mock(PopupPanel.class);

        when(notification.popup.getOffsetWidth()).thenReturn(popupWidth);
        when(notification.contentContainer.getOffsetWidth()).thenReturn(contentWidth);

        return notification;
    }

    @Test
    public void testCenterContent() {
        int buttonsWidth = nButtonsNotificationWidth(2);
        NotificationWithResponse notification = mockNotification(buttonsWidth, buttonsWidth - 100, false);
        notification.align();

        verify(notification.icon.getElement().getStyle(), times(1)).setMarginLeft(12, Style.Unit.PX);
        verify(notification.yesButton.getElement().getStyle(), times(0)).setMarginLeft(anyDouble(), any(Style.Unit.class));
    }

    @Test
    public void testCenterButtons() {
        int buttonsWidth = nButtonsNotificationWidth(2);
        NotificationWithResponse notification = mockNotification(buttonsWidth + 100, buttonsWidth + 100, false);
        notification.align();

        verify(notification.icon.getElement().getStyle(), times(0)).setMarginLeft(anyDouble(), any(Style.Unit.class));
        verify(notification.yesButton.getElement().getStyle(), times(1)).setMarginLeft(44, Style.Unit.PX);
    }
}