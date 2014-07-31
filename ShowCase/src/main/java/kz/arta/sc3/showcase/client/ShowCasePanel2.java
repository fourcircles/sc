package kz.arta.sc3.showcase.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import kz.arta.sc3.showcase.client.buttons.ColorButtons;
import kz.arta.sc3.showcase.client.buttons.IconButtons;
import kz.arta.sc3.showcase.client.buttons.SimpleButtons;
import kz.arta.sc3.showcase.client.dialog.Dialogs;
import kz.arta.sc3.showcase.client.dialog.SimpleDialogs;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.ComboBox;

import java.util.HashSet;

/**
 * User: vsl
 * Date: 22.07.14
 * Time: 12:39
 */
public class ShowCasePanel2 implements IsWidget{
    interface ShowCasePanel2UiBinder extends UiBinder<LayoutPanel, ShowCasePanel2> {
    }
    private static ShowCasePanel2UiBinder binder = GWT.create(ShowCasePanel2UiBinder.class);

    LayoutPanel panel;
    @UiField TabLayoutPanel tabPanel;
    @UiField ComboBox localesCombo;
    @UiField ComboBox themesCombo;
    @UiField Button about;
    @UiField Tree tree;

    @UiField TreeItem treeButtons;
    @UiField TreeItem treeSimpleButtons;
    @UiField TreeItem treeButtonsWithIcons;
    @UiField TreeItem treeColorButtons;
    @UiField TreeItem treeDialog;
    @UiField TreeItem treeDialogNoButtons;
    @UiField TreeItem treeDialogYesButtons;

    HashSet<Widget> openWidgets;

    private void translate(HasText item) {
        item.setText(SCMessages.i18n.tr(item.getText()));
    }

    private void translate() {
        translate(treeButtons);
        translate(treeSimpleButtons);
        translate(treeButtonsWithIcons);

        translate(treeDialog);
        translate(treeDialogYesButtons);
        translate(treeDialogNoButtons);
    }

    public ShowCasePanel2() {
        panel = binder.createAndBindUi(this);
        panel.forceLayout();

        treeSimpleButtons.setUserObject(new SimpleButtons().asWidget());
        treeButtonsWithIcons.setUserObject(new IconButtons().asWidget());
        treeColorButtons.setUserObject(new ColorButtons().asWidget());

        treeDialogNoButtons.setUserObject(new SimpleDialogs().asWidget());
        treeDialogYesButtons.setUserObject(new Dialogs().asWidget());

        openWidgets = new HashSet<Widget>();
    }

    @UiHandler("tree")
    public void selectTreeItem(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();
        Widget widget = (Widget) item.getUserObject();
        if (openWidgets.contains(item.getUserObject())) {
            tabPanel.selectTab(widget);
        } else {
            tabPanel.add(widget, buildTab(item));
            tabPanel.selectTab(widget);
        }
    }

    private Widget buildTab(final TreeItem item) {
        HorizontalPanel hPanel = new HorizontalPanel();
        Label label = new Label(item.getText());
        Label close = new Label("[X]");
        hPanel.add(label);
        hPanel.add(close);

        close.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tabPanel.remove((Widget) item.getUserObject());
            }
        });
        return hPanel;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}