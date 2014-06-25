package kz.arta.sc3.showcase.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * User: vsl
 * Date: 24.06.14
 * Time: 14:03
 */
public class ShowComponent implements IsWidget{
    TreeItem parent;
    TreeItem treeItem;
    Widget tabTitle;
    Widget content;

    ShowCasePanel showCasePanel;


    private ShowComponent(ShowCasePanel showCasePanel, TreeItem parent, String nodeName, Widget content) {
        this.showCasePanel = showCasePanel;
        this.parent = parent;
        treeItem = new TreeItem();
        treeItem.setText(nodeName);
        treeItem.setUserObject(this);
        parent.addItem(treeItem);

        this.content = content;
    }

    public ShowComponent(ShowCasePanel showCasePanel, TreeItem parent, String nodeName, Widget tabTitle, Widget content) {
        this(showCasePanel, parent, nodeName, content);
        this.tabTitle = tabTitle;
    }

    public ShowComponent(final ShowCasePanel showCasePanel, TreeItem parent, String nodeName, String text, Widget content) {
        this(showCasePanel, parent, nodeName, content);

        HorizontalPanel hPanel = new HorizontalPanel();
        Label label = new Label(text);
        Label close = new Label("[X]");
        hPanel.add(label);
        hPanel.add(close);

        tabTitle = hPanel;

        ClickHandler closeTab = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showCasePanel.closeTab(ShowComponent.this);
            }
        };
        close.addClickHandler(closeTab);
    }


    public TreeItem getParent() {
        return parent;
    }

    public void setParent(TreeItem parent) {
        this.parent = parent;
    }

    public Widget getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(Widget tabTitle) {
        this.tabTitle = tabTitle;
    }

    public Widget getContent() {
        return content;
    }

    public void setContent(Widget content) {
        this.content = content;
    }

    @Override
    public Widget asWidget() {
        return content;
    }
}
