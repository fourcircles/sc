package kz.arta.sc3.showcase.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.sc3.showcase.client.resources.SCImageResources;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.button.TextColorButton;
import kz.arta.synergy.components.client.dialog.ArtaDialogBox;
import kz.arta.synergy.components.client.dialog.ArtaDialogBoxSimple;
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

        Label navigationLabel = new Label(SCMessages.i18n.tr("Навигация"));
        FlowPanel navigationPanel = new FlowPanel();
        navigationPanel.add(navigationLabel);
        navigationPanel.add(tree);

        navigationLabel.setWidth("100%");
        tree.setWidth("100%");
        navigationLabel.getElement().getStyle().setProperty("borderBottom", "solid 1px");

        FlowPanel titlePanel = new FlowPanel();
        Label showCaseLabel = new Label("ShowCase");
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

    private SimpleButton setUpDialog(int width, int height, boolean buttons, boolean backButton, boolean moreButton) {
        String title = "Content size: " + width + "x" + height;
        SimpleButton button = new SimpleButton(title);

        Label contentLabel = new Label("content");
        contentLabel.setSize("100%", "100%");
        SimplePanel sPanel = new SimplePanel(contentLabel);
        sPanel.setSize(width + "px", height + "px");
        sPanel.getElement().getStyle().setBackgroundColor("pink");

        final ArtaDialogBoxSimple dialog;
        if (buttons) {
            ArtaDialogBox withButtons = new ArtaDialogBox(title, sPanel);
            withButtons.setBackButtonVisible(backButton);
            withButtons.setMoreButtonVisible(moreButton);

            dialog = withButtons;
        } else {
            dialog = new ArtaDialogBoxSimple(title, sPanel);
        }
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialog.center();
                dialog.show();
            }
        });
        return button;
    }

    //TODO remove HorizontalPanel
    private Panel setUpDialogs(boolean buttons) {
        SimpleButton small = setUpDialog(300, 300, buttons, true, true);
        SimpleButton middle = setUpDialog(400, 400, buttons, true, true);
        SimpleButton big = setUpDialog(800, 500, buttons, true, true);
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(small);
        hPanel.add(middle);
        hPanel.add(big);
        if (buttons) {
            hPanel.add(setUpDialog(400, 400, buttons, false, true));
            hPanel.add(setUpDialog(400, 400, buttons, true, false));
        }

        hPanel.getElement().getStyle().setBackgroundColor("black");
        hPanel.setSize("100%", "100%");

        return hPanel;
    }

    private void treeSetUp() {
        tree = new Tree();

        TreeItem category1 = addCategory(SCMessages.i18n.tr("Кнопки"));
        TreeItem category2 = addCategory("category2");

        int cnt = 0;

        FlowPanel simpleButtonPanel = new FlowPanel();

        SimpleButton simpleButton = new SimpleButton(SCMessages.i18n.tr("Простая кнопка"));
        simpleButton.setWidth(140);
        simpleButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton);

        SimpleButton simpleButton1 = new SimpleButton(SCMessages.i18n.tr("Неактивная кнопка"));
        simpleButton1.setEnabled(false);
        simpleButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton1);

        SimpleButton simpleButton2 = new SimpleButton("Кнопка с кликом");
        simpleButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton2);
        simpleButton2.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(SCMessages.i18n.tr("Кнопка была нажата!"));
            }
        });

        new ShowComponent(this, category1, SCMessages.i18n.tr("Простая кнопка"), SCMessages.i18n.tr("Простая кнопка"), simpleButtonPanel);

        FlowPanel iconButtonPanel = new FlowPanel();

        SimpleButton iconButton = new SimpleButton(SCMessages.i18n.tr("Кнопка с иконкой"), SCImageResources.IMPL.zoom());
        iconButtonPanel.add(iconButton);
        iconButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        SimpleButton iconButton1 = new SimpleButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), SCImageResources.IMPL.zoom());
        iconButtonPanel.add(iconButton1);
        iconButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        SimpleButton iconButton2 = new SimpleButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), SCImageResources.IMPL.zoom());
        iconButton2.setWidth(150);
        iconButtonPanel.add(iconButton2);
        iconButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        SimpleButton iconButton3 = new SimpleButton(SCMessages.i18n.tr("Кнопка неактивная"), SCImageResources.IMPL.zoom());
        iconButton3.setWidth(200);
        iconButtonPanel.add(iconButton3);
        iconButton3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton3.setEnabled(false);

        ImageButton iconButton4 = new ImageButton(SCImageResources.IMPL.zoom());
        iconButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButtonPanel.add(iconButton4);

        ImageButton iconButton5 = new ImageButton(SCImageResources.IMPL.zoom());
        iconButton5.setEnabled(false);
        iconButtonPanel.add(iconButton5);

        new ShowComponent(this, category1, SCMessages.i18n.tr("Кнопка с иконкой"), SCMessages.i18n.tr("Кнопка с иконкой"), iconButtonPanel);

        FlowPanel colorButtonPanel = new FlowPanel();

        TextColorButton colorButton = new TextColorButton(SCMessages.i18n.tr("Создать"), TextColorButton.APPROVE_BUTTON);
        colorButtonPanel.add(colorButton);
        colorButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        TextColorButton colorButton1 = new TextColorButton(SCMessages.i18n.tr("Удалить"), TextColorButton.DECLINE_BUTTON);
        colorButtonPanel.add(colorButton1);
        colorButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        TextColorButton colorButton2 = new TextColorButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), TextColorButton.DECLINE_BUTTON);
        colorButtonPanel.add(colorButton2);
        colorButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        TextColorButton colorButton3 = new TextColorButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), TextColorButton.DECLINE_BUTTON);
        colorButton3.setWidth(100);
        colorButtonPanel.add(colorButton3);
        colorButton3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        TextColorButton colorButton4 = new TextColorButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), TextColorButton.APPROVE_BUTTON);
        colorButton4.setWidth(100);
        colorButtonPanel.add(colorButton4);
        colorButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        TextColorButton colorButton5 = new TextColorButton(SCMessages.i18n.tr("Кнопка неактивная"), TextColorButton.APPROVE_BUTTON);
        colorButton5.setEnabled(false);
        colorButtonPanel.add(colorButton5);
        colorButton5.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        TextColorButton colorButton6 = new TextColorButton(SCMessages.i18n.tr("Кнопка неактивная"), TextColorButton.DECLINE_BUTTON);
        colorButton6.setEnabled(false);
        colorButtonPanel.add(colorButton6);
        colorButton6.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        new ShowComponent(this, category1, SCMessages.i18n.tr("Кнопки"), SCMessages.i18n.tr("Кнопки"), colorButtonPanel);

        cnt++;
        new ShowComponent(this, category2, "Диалог без кнопок", "Диалог без кнопок", setUpDialogs(false));
        cnt++;
        new ShowComponent(this, category2, "Диалог с кнопками", "Диалог с кнопками", setUpDialogs(true));
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
