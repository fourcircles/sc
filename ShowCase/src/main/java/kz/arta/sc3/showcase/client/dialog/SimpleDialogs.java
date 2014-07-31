package kz.arta.sc3.showcase.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.dialog.DialogSimple;

import java.util.HashMap;

/**
 * User: vsl
 * Date: 23.07.14
 * Time: 16:28
 */
public class SimpleDialogs extends DialogsBase implements IsWidget{
    interface SimpleDialogsUiBinder extends UiBinder<FlowPanel, SimpleDialogs> {
    }
    private static SimpleDialogsUiBinder ourUiBinder = GWT.create(SimpleDialogsUiBinder.class);
    FlowPanel panel;

    @UiField SimpleButton empty;
    @UiField SimpleButton button116_84;
    @UiField SimpleButton button300_300;
    @UiField SimpleButton button400_400;
    @UiField SimpleButton button800_500;

    public SimpleDialogs() {
        panel = ourUiBinder.createAndBindUi(this);

        buttonToDialog = new HashMap<SimpleButton, DialogSimple>();
        buttonToDialog.put(empty, createDialog(empty.getText(), 0, 0));
        buttonToDialog.put(button116_84, createDialog(button116_84.getText(), 116, 84));
        buttonToDialog.put(button300_300, createDialog(button300_300.getText(), 300, 300));
        buttonToDialog.put(button400_400, createDialog(button400_400.getText(), 400, 400));
        buttonToDialog.put(button800_500, createDialog(button800_500.getText(), 800, 500));
    }

    @UiHandler(value={"empty", "button116_84", "button300_300", "button400_400", "button800_500"} )
    void empty(ClickEvent event) {
        showDialog(event);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}