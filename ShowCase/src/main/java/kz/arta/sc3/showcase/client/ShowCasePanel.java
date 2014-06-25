package kz.arta.sc3.showcase.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.*;
import kz.arta.sc3.showcase.client.css.CssTheme;
import kz.arta.sc3.showcase.client.css.Resources;
import kz.arta.sc3.showcase.client.images.TreeResources;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 23.06.14
 * Time: 12:43
 */
public class ShowCasePanel extends LayoutPanel {
    private final static int TITLE_HEIGHT = 5;
    private final static int TREE_WIDTH = 15;
    private final static int SPACING = 1;

    private final static Theme defaultTheme = Theme.BLUE;
    private StyleElement styleElement;

    Tree tree;

    TabLayoutPanel contentPanel;

    TreeItem selectedItem;

    private ArrayList<ShowComponent> tabbedComponents;

    private ListBox stylesListBox;
    private ArrayList<Theme> themes;

    public void setBorder(Widget w) {
        w.getElement().getStyle().setProperty("border", "solid 1px black");
    }

    public ShowCasePanel(StyleElement styleElement) {
        this.styleElement = styleElement;

        contentPanel = new TabLayoutPanel(30, Style.Unit.PX);
        themes = new ArrayList<Theme>();

        treeSetUp();

        Label navigationLabel = new Label("Навигация");
        LayoutPanel leftPanel = new LayoutPanel();
        leftPanel.add(navigationLabel);
        leftPanel.add(tree);

        leftPanel.setWidgetTopBottom(navigationLabel, 0, Style.Unit.PCT, 95, Style.Unit.PCT);
        leftPanel.setWidgetTopBottom(tree, 5, Style.Unit.PCT, 0, Style.Unit.PCT);

        LayoutPanel titlePanel = new LayoutPanel();
        Label showCaseLabel = new Label("ShowCase");
        titlePanel.add(showCaseLabel);

        stylesListBox = new ListBox();
        Theme.addAllThemes(this);

        stylesListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                stylesListBox.getTitle();
                setTheme(themes.get(stylesListBox.getSelectedIndex()));
            }
        });

        titlePanel.add(stylesListBox);
        titlePanel.setWidth("100%");

        titlePanel.setWidgetLeftWidth(showCaseLabel, 0, Style.Unit.PCT, 50, Style.Unit.PCT);
        titlePanel.setWidgetRightWidth(stylesListBox, 0, Style.Unit.PCT, 50, Style.Unit.PCT);

        add(titlePanel);
        add(leftPanel);
        add(contentPanel);

        setWidgetLeftWidth(leftPanel, 0, Style.Unit.PCT, TREE_WIDTH, Style.Unit.PCT);
        setWidgetTopBottom(leftPanel, TITLE_HEIGHT + SPACING, Style.Unit.PCT, 0, Style.Unit.PCT);

        setWidgetRightWidth(contentPanel, 0, Style.Unit.PCT, 100 - TREE_WIDTH - SPACING, Style.Unit.PCT);
        setWidgetTopBottom(contentPanel, TITLE_HEIGHT + SPACING, Style.Unit.PCT, 0, Style.Unit.PCT);

        setWidgetTopBottom(titlePanel, 0, Style.Unit.PCT, 100 - TITLE_HEIGHT, Style.Unit.PCT);

        setBorder(leftPanel);
        setBorder(titlePanel);
        setBorder(contentPanel);

        setSize("100%", "100%");

        forceLayout();

        setTheme(defaultTheme);

        tabbedComponents = new ArrayList<ShowComponent>();

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                selectedItem = event.getSelectedItem();
            }
        });

        tree.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (selectedItem.getUserObject() != null) {
                    ShowComponent component = (ShowComponent) selectedItem.getUserObject();
                    showTab(component);
                }
            }
        });
    }

    public void closeTab(ShowComponent component) {
        contentPanel.remove(component.asWidget());
        tabbedComponents.remove(component);
    }

    public void showTab(ShowComponent component) {
        if (tabbedComponents.contains(component)) {
            contentPanel.selectTab(component);
        } else {
            //new tab
            contentPanel.add(component, component.getTabTitle());
            tabbedComponents.add(component);
            contentPanel.selectTab(contentPanel);
        }
    }

    public TreeItem addCategory(String text) {
        TreeItem item = new TreeItem();
        Label label = new Label(text);
        item.setWidget(label);
        tree.addItem(item);

        return item;
    }

    void addLeaf(TreeItem category, TreeItem displayItem, Widget contentWidget) {
        displayItem.setUserObject(contentWidget);
        category.addItem(displayItem);
    }

    public void addLeaf(TreeItem where, Widget displayWidget, Widget contentWidget) {
        TreeItem treeItem = new TreeItem(displayWidget);
        addLeaf(where, treeItem, contentWidget);
    }

    public void addLeaf(TreeItem where, String displayText, Widget contentWidget) {
        addLeaf(where, new TreeItem(new Label(displayText)), contentWidget);
    }

    private void treeSetUp() {
        tree = new Tree(TreeResources.IMPL, true);

        TreeItem category1 = addCategory("category1");
        TreeItem category2 = addCategory("category2");

        int cnt = 0;
        new ShowComponent(this, category1, "tree_node_" + cnt, "tab_title_" + cnt, new Label("content_" + cnt));
        cnt++;
        new ShowComponent(this, category1, "tree_node_" + cnt, "tab_title_" + cnt, new Label("content_" + cnt));
        cnt++;
        new ShowComponent(this, category2, "tree_node_" + cnt, "tab_title_" + cnt, new Label("content_" + cnt));
        cnt++;
        new ShowComponent(this, category2, "tree_node_" + cnt, "tab_title_" + cnt, new Label("content_" + cnt));
        cnt++;
    }

    private void addTheme(Theme theme) {
        if (!themes.contains(theme)) {
            themes.add(theme);
            stylesListBox.addItem(theme.themeName);
        }
    }

    private void setTheme(Theme theme) {
        stylesListBox.setSelectedIndex(themes.indexOf(theme));
        StyleInjector.setContents(styleElement, theme.getCss().getText());
    }

    static enum Theme {
        RED("redTheme"), BLUE("blueTheme"), YELLOW("yellowTheme");
        String themeName;

        Theme(String themeName) {
            this.themeName = themeName;
        }

        String getThemeName() {
            return themeName;
        }

        public static Theme getTheme(String themeName) {
            for (Theme theme : Theme.values()) {
                if (theme.themeName.equals(themeName)) {
                    return theme;
                }
            }
            return null;
        }

        public static void addAllThemes(ShowCasePanel panel) {
            for (Theme theme : Theme.values()) {
                panel.addTheme(theme);
            }
        }

        public CssTheme getCss() {
            switch (this) {
                case RED:
                    return Resources.IMPL.red();
                case BLUE:
                    return Resources.IMPL.blue();
                default:
                    return Resources.IMPL.blue();
            }
        }
    }
}
