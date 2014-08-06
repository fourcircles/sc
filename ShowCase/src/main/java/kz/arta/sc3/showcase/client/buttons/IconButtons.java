package kz.arta.sc3.showcase.client.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.button.ContextMenuButton;

/**
 * User: vsl
 * Date: 23.07.14
 * Time: 14:30
 */
public class IconButtons extends ButtonsBase implements IsWidget{
    interface IconButtonsUiBinder extends UiBinder<FlowPanel, IconButtons> {
    }
    private static IconButtonsUiBinder ourUiBinder = GWT.create(IconButtonsUiBinder.class);

    FlowPanel panel;
    @UiField ContextMenuButton contextButton1;
    @UiField ContextMenuButton contextButton2;
    @UiField ContextMenuButton contextButton3;

    public IconButtons() {
        panel = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}