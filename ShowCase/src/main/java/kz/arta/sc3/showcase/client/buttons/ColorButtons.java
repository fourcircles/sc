package kz.arta.sc3.showcase.client.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: vsl
 * Date: 23.07.14
 * Time: 15:09
 */
public class ColorButtons extends ButtonsBase implements IsWidget{
    interface ColorButtonsUiBinder extends UiBinder<FlowPanel, ColorButtons> {
    }
    private static ColorButtonsUiBinder ourUiBinder = GWT.create(ColorButtonsUiBinder.class);
    FlowPanel panel;

    public ColorButtons() {
        panel = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}