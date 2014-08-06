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
import kz.arta.synergy.components.client.dialog.Dialog;
import kz.arta.synergy.components.client.dialog.DialogSimple;

import java.util.HashMap;

/**
 * User: vsl
 * Date: 23.07.14
 * Time: 17:59
 */
public class Dialogs extends DialogsBase implements IsWidget{
    interface DialogsUiBinder extends UiBinder<FlowPanel, Dialogs> {
    }
    private static DialogsUiBinder ourUiBinder = GWT.create(DialogsUiBinder.class);

    FlowPanel panel;
    @UiField SimpleButton button116_84;
    @UiField SimpleButton button300_300;
    @UiField SimpleButton button400_400;
    @UiField SimpleButton button800_500;
    @UiField SimpleButton buttonNoLeft;
    @UiField SimpleButton buttonNoRight;
    @UiField SimpleButton buttonOnlyMiddle;

    @Override
    protected DialogSimple createDialog(String titleText, Widget content) {
        return new Dialog(titleText, content);
    }

    public Dialogs() {
        panel = ourUiBinder.createAndBindUi(this);

        buttonToDialog = new HashMap<SimpleButton, DialogSimple>();

        buttonToDialog.put(button116_84, createDialog(button116_84.getText(), 116, 84));
        buttonToDialog.put(button300_300, createDialog(button300_300.getText(), 300, 300));
        buttonToDialog.put(button400_400, createDialog(button400_400.getText(), 400, 400));
        buttonToDialog.put(button800_500, createDialog(button800_500.getText(), 800, 500));

        Dialog noLeftDialog = (Dialog) createDialog(buttonNoLeft.getText(), 400, 400);
        noLeftDialog.setLeftButtonVisible(false);
        buttonToDialog.put(buttonNoLeft, noLeftDialog);

        Dialog noRightDialog = (Dialog) createDialog(buttonNoRight.getText(), 400, 400);
        noRightDialog.setRightButtonVisible(false);
        buttonToDialog.put(buttonNoRight, noRightDialog);

        Dialog onlyMiddleDialog = (Dialog) createDialog(buttonOnlyMiddle.getText(), 400, 400);
        onlyMiddleDialog.setRightButtonVisible(false);
        onlyMiddleDialog.setLeftButtonVisible(false);
        buttonToDialog.put(buttonOnlyMiddle, onlyMiddleDialog);
    }

    @UiHandler(value={"button116_84", "button300_300", "button400_400", "button800_500",
                      "buttonNoLeft", "buttonNoRight", "buttonOnlyMiddle"} )
    void empty(ClickEvent event) {
        showDialog(event);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}