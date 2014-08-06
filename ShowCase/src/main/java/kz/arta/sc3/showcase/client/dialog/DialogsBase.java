package kz.arta.sc3.showcase.client.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.dialog.DialogSimple;

import java.util.HashMap;

/**
 * User: vsl
 * Date: 23.07.14
 * Time: 18:13
 */
class DialogsBase {

    protected HashMap<SimpleButton, DialogSimple> buttonToDialog;

    @UiFactory
    protected SimpleButton simpleButton(String text) {
        return new SimpleButton(text);
    }

    protected DialogSimple createDialog(String titleText, Widget content) {
        return new DialogSimple(titleText, content);
    }

    protected DialogSimple createDialog(String titleText, int width, int height) {
        SimplePanel content = new SimplePanel();
        content.setSize(width + "px", height + "px");
        content.getElement().getStyle().setBackgroundColor("green");

        return createDialog(titleText, content);
    }

    protected void showDialog(ClickEvent event) {
        SimpleButton button = (SimpleButton) event.getSource();
        DialogSimple dialog = buttonToDialog.get(button);
        dialog.center();
        dialog.show();
    }

}
