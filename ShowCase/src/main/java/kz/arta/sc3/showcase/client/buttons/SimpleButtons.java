package kz.arta.sc3.showcase.client.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.button.SimpleButton;

/**
 * User: vsl
 * Date: 22.07.14
 * Time: 17:14
 */
public class SimpleButtons extends ButtonsBase implements IsWidget{
    interface SimpleButtonsUiBinder extends UiBinder<FlowPanel, SimpleButtons> {
    }
    private static SimpleButtonsUiBinder ourUiBinder = GWT.create(SimpleButtonsUiBinder.class);

    FlowPanel panel;
    @UiField SimpleButton buttonClick;

    public SimpleButtons() {
        panel = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}