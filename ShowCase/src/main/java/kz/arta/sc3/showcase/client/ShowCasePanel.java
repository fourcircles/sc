package kz.arta.sc3.showcase.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.theme.Theme;

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

    private Theme currentTheme;

    Tree tree;

    TabLayoutPanel contentPanel;

    TreeItem selectedItem;

    private ArrayList<ShowComponent> tabbedComponents;

    private ListBox themeListBox;
    private ListBox localeListBox;

    private ArrayList<Theme> themes;
    private ArrayList<String> locales;

    public void setBorder(Widget w) {
        w.getElement().getStyle().setProperty("border", "solid 1px black");
    }

    public ShowCasePanel() {
        if (Cookies.getCookie("theme") == null) {
            currentTheme = Theme.standard;
        } else {
            currentTheme = Theme.getTheme(Cookies.getCookie("theme"));
        }

        contentPanel = new TabLayoutPanel(30, Style.Unit.PX);
        themes = new ArrayList<Theme>();

        treeSetUp();

        Label navigationLabel = new Label(SCMessages.i18n.tr("Navigation"));
        FlowPanel navigationPanel = new FlowPanel();
        navigationPanel.add(navigationLabel);
        navigationPanel.add(tree);

        navigationLabel.setWidth("100%");
        tree.setWidth("100%");
        navigationLabel.getElement().getStyle().setProperty("borderBottom", "solid 1px");

        FlowPanel titlePanel = new FlowPanel();
        Label showCaseLabel = new Label(SCMessages.i18n.tr("ShowCase"));
        titlePanel.add(showCaseLabel);

        themeListBox = new ListBox();
        addAllThemes();

        themeListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                Cookies.setCookie("theme", themes.get(themeListBox.getSelectedIndex()).name());
                Window.Location.reload();
            }
        });

        titlePanel.add(themeListBox);

        locales = new ArrayList<String>();
        localeListBox = new ListBox();
        for (String locale : LocaleInfo.getAvailableLocaleNames()) {
            if (!"default".equals(locale)) {
                locales.add(locale);
                localeListBox.addItem(locale);
            }
        }

        String chosenLocale = Window.Location.getParameter("locale");
        if (chosenLocale == null) {
            chosenLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        }
        localeListBox.setSelectedIndex(locales.indexOf(chosenLocale));

        localeListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                UrlBuilder newUrl = Window.Location.createUrlBuilder();
                newUrl.setParameter("locale", localeListBox.getItemText(localeListBox.getSelectedIndex()));
                Window.Location.assign(newUrl.buildString());
            }
        });

        titlePanel.add(localeListBox);

        titlePanel.setWidth("100%");
        showCaseLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
        themeListBox.getElement().getStyle().setFloat(Style.Float.RIGHT);
        localeListBox.getElement().getStyle().setFloat(Style.Float.RIGHT);

        add(titlePanel);
        add(navigationPanel);
        add(contentPanel);

        setWidgetLeftWidth(navigationPanel, 0, Style.Unit.PCT, TREE_WIDTH, Style.Unit.PCT);
        setWidgetTopBottom(navigationPanel, TITLE_HEIGHT + SPACING, Style.Unit.PCT, 0, Style.Unit.PCT);

        setWidgetRightWidth(contentPanel, 0, Style.Unit.PCT, 100 - TREE_WIDTH - SPACING, Style.Unit.PCT);
        setWidgetTopBottom(contentPanel, TITLE_HEIGHT + SPACING, Style.Unit.PCT, 0, Style.Unit.PCT);

        setWidgetTopBottom(titlePanel, 0, Style.Unit.PCT, 100 - TITLE_HEIGHT, Style.Unit.PCT);

        setBorder(navigationPanel);
        setBorder(titlePanel);
        setBorder(contentPanel);

        setSize("100%", "100%");

        forceLayout();

        setTheme(currentTheme);

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
        tree = new Tree();

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

    private void addAllThemes() {
        for (Theme theme : Theme.values()) {
            addTheme(theme);
        }
    }

    private void addTheme(Theme theme) {
        if (!themes.contains(theme)) {
            themes.add(theme);
            themeListBox.addItem(theme.name());
        }
    }

    private void setTheme(Theme theme) {
        themeListBox.setSelectedIndex(themes.indexOf(theme));
    }
}
