package kz.arta.sc3.showcase.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.sc3.showcase.client.resources.SCImageResources;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.ComboBox;
import kz.arta.synergy.components.client.button.ButtonBase;
import kz.arta.synergy.components.client.button.*;
import kz.arta.synergy.components.client.dialog.Dialog;
import kz.arta.synergy.components.client.dialog.DialogSimple;
import kz.arta.synergy.components.client.input.*;
import kz.arta.synergy.components.client.input.tags.TagInput;
import kz.arta.synergy.components.client.menu.ContextMenu;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.scroll.ArtaVerticalScrollPanel;
import kz.arta.synergy.components.client.theme.Theme;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 23.06.14
 * Time: 12:43
 */
public class ShowCasePanel extends LayoutPanel {
    private final static int TITLE_HEIGHT = 8;
    private final static int TREE_WIDTH = 15;
    private final static int SPACING = 1;

    private Theme currentTheme;
    private ComboBox<Theme> themesCombo;

    Tree tree;

    TabLayoutPanel contentPanel;

    TreeItem selectedItem;

    private ArrayList<ShowComponent> tabbedComponents;

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

        final Dictionary theme = Dictionary.getDictionary("properties");

        Button about = new Button("About");
        about.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("About: \nSynergy components ShowCase\n\nVersion: " + theme.get("version") +
                        "\nRevision: " + theme.get("revision") +"\nBuild stamp: " + theme.get("build_stamp"));
            }
        });
        titlePanel.add(about);
        about.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);



        themesCombo = new ComboBox<Theme>();
        for (Theme th : Theme.values()) {
            themesCombo.addItem(th.name(), th);
        }
        themesCombo.addValueChangeHandler(new ValueChangeHandler<Theme>() {
            @Override
            public void onValueChange(ValueChangeEvent<Theme> event) {
                setTheme(event.getValue());
            }
        });
        themesCombo.setText(currentTheme.name());
        titlePanel.add(themesCombo);

        final ComboBox<String> localesCombo = new ComboBox<String>();
        localesCombo.setReadOnly(true);
        for (String locale: LocaleInfo.getAvailableLocaleNames()) {
            localesCombo.addItem(locale, locale);
        }
        localesCombo.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                UrlBuilder newUrl = Window.Location.createUrlBuilder();
                newUrl.setParameter("locale", event.getValue());
                Window.Location.assign(newUrl.buildString());
            }
        });
        String chosenLocale = Window.Location.getParameter("locale");
        if (chosenLocale == null) {
            chosenLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        }
        localesCombo.setText(chosenLocale);
        localesCombo.getElement().getStyle().setMarginRight(20, Style.Unit.PX);
        titlePanel.add(localesCombo);

        titlePanel.setWidth("100%");
        showCaseLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
        themesCombo.getElement().getStyle().setFloat(Style.Float.RIGHT);
        localesCombo.getElement().getStyle().setFloat(Style.Float.RIGHT);
        about.getElement().getStyle().setFloat(Style.Float.RIGHT);

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
                if (selectedItem != null && selectedItem.getUserObject() != null) {
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
            contentPanel.selectTab(component);
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

    private SimpleButton setUpDialog(String title, final DialogSimple dialog) {
        dialog.setText(title);
        SimpleButton button = new SimpleButton(title);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialog.center();
                dialog.show();
            }
        });
        return button;
    }

    private SimpleButton setUpDialog(int width, int height, boolean buttons, boolean backButton, boolean moreButton) {
        String title = SCMessages.i18n.tr("Размер") + ": " + width + "x" + height;
        SimpleButton button = new SimpleButton(title);

        Label contentLabel = new Label(SCMessages.i18n.tr("Содержимое"));
        contentLabel.setSize("100%", "100%");
        SimplePanel sPanel = new SimplePanel(contentLabel);
        sPanel.setSize(width + "px", height + "px");
        sPanel.getElement().getStyle().setBackgroundColor("pink");

        final DialogSimple dialog;
        if (buttons) {
            Dialog withButtons = new Dialog(title, sPanel);
            withButtons.setLeftButtonVisible(backButton);
            withButtons.setRightButtonVisible(moreButton);

            dialog = withButtons;
        } else {
            dialog = new DialogSimple();
            dialog.setText(title);
            dialog.setContent(sPanel);
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

    private Panel setUpDialogs(boolean buttons) {
        SimpleButton empty = setUpDialog(SCMessages.i18n.tr("Пустой"), new DialogSimple());
        empty.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        SimpleButton tiny = setUpDialog(116, 84, buttons, true, true);
        tiny.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        SimpleButton small = setUpDialog(300, 300, buttons, true, true);
        small.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        SimpleButton middle = setUpDialog(400, 400, buttons, true, true);
        middle.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        SimpleButton big = setUpDialog(800, 500, buttons, true, true);
        big.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        FlowPanel panel = new FlowPanel();
        panel.add(empty);
        panel.add(tiny);
        panel.add(small);
        panel.add(middle);
        panel.add(big);
        panel.add(new HTML("<p/>"));
        if (buttons) {
            SimpleButton noLeft = setUpDialog(400, 400, buttons, false, true);
            noLeft.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
            SimpleButton noRight = setUpDialog(400, 400, buttons, true, false);
            noRight.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
            SimpleButton onlySave = setUpDialog(400, 400, buttons, false, false);
            onlySave.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

            panel.add(noLeft);
            panel.add(noRight);
            panel.add(onlySave);
        }

        panel.setSize("100%", "100%");

        return panel;
    }

    private static class FlowPanel_ extends FlowPanel {
        public FlowPanel_() {
            super();
            sinkEvents(Event.ONCONTEXTMENU);
        }
    }

    private void treeSetUp() {
        tree = new Tree();

        TreeItem category1 = addCategory(SCMessages.i18n.tr("Кнопки"));
        TreeItem category2 = addCategory(SCMessages.i18n.tr("Диалог"));
        TreeItem category3 = addCategory(SCMessages.i18n.tr("Поля ввода"));

        int cnt = 0;


        new ShowComponent(this, category1, SCMessages.i18n.tr("Простая кнопка"), SCMessages.i18n.tr("Простая кнопка"), getSimpleButtonPanel());

        new ShowComponent(this, category1, SCMessages.i18n.tr("Кнопка с иконкой"), SCMessages.i18n.tr("Кнопка с иконкой"), getIconButtonPanel());

        new ShowComponent(this, category1, SCMessages.i18n.tr("Кнопки"), SCMessages.i18n.tr("Кнопки"), getColorButtonPanel());

        new ShowComponent(this, category1, SCMessages.i18n.tr("Групповые кнопки"), SCMessages.i18n.tr("Групповые кнопки"), getGroupButton());

        FlowPanel comboBoxPanel = new FlowPanel();

        ComboBox combo1 = new ComboBox();
        combo1.addItem(SCMessages.i18n.tr("Приблизить"), ImageResources.IMPL.zoom());
        combo1.addItem(SCMessages.i18n.tr("Налево"), ImageResources.IMPL.navigationLeft());
        combo1.addItem(SCMessages.i18n.tr("Направо"), ImageResources.IMPL.navigationRight());
        combo1.addItem(SCMessages.i18n.tr("Простооченьдлинныйпунктменю,чтобыпосмотретьчтопроисходит"), null);
        for (int i = 1; i < 30; i++) {
            combo1.addItem(SCMessages.i18n.tr("Пункт меню ") + i, null);
        }
        combo1.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        combo1.setReadOnly(false);
        comboBoxPanel.add(combo1);

        ComboBox comboDisabled = new ComboBox();
        comboDisabled.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        comboDisabled.setEnabled(false);
        comboBoxPanel.add(comboDisabled);

        ComboBox comboReadOnly = new ComboBox();
        comboReadOnly.addItem(SCMessages.i18n.tr("Приблизить"), ImageResources.IMPL.zoom());
        comboReadOnly.addItem(SCMessages.i18n.tr("Налево"), ImageResources.IMPL.navigationLeft());
        comboReadOnly.addItem(SCMessages.i18n.tr("Направо"), ImageResources.IMPL.navigationRight());
        comboReadOnly.addItem(SCMessages.i18n.tr("Простооченьдлинныйпунктменю,чтобыпосмотретьчтопроисходит"), null);
        for (int i = 1; i < 30; i++) {
            comboReadOnly.addItem(SCMessages.i18n.tr("Пункт меню ") + i, null);
        }
        comboReadOnly.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        comboReadOnly.setReadOnly(true);
        comboReadOnly.setWidth(250);
        comboBoxPanel.add(comboReadOnly);

        cnt++;
        new ShowComponent(this, category2, SCMessages.i18n.tr("Диалог без кнопок"), SCMessages.i18n.tr("Диалог без кнопок"), setUpDialogs(false));
        cnt++;
        new ShowComponent(this, category2, SCMessages.i18n.tr("Диалог с кнопками"), SCMessages.i18n.tr("Диалог с кнопками"), setUpDialogs(true));
        cnt++;
        new ShowComponent(this, category3, SCMessages.i18n.tr("Поле ввода текста"), SCMessages.i18n.tr("Поле ввода текста"), getTextInputs());
        cnt++;
        new ShowComponent(this, category3, SCMessages.i18n.tr("Комбобокс"), SCMessages.i18n.tr("Комбобокс"), comboBoxPanel);
    }

    /**
     * Текстовые поля
     * @return панель с текстовыми полями
     */
    private Widget getTextInputs() {
        FlowPanel panel = new FlowPanel();
        final TextInput textInput = new TextInput();
        textInput.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        textInput.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(textInput);
        textInput.setPlaceHolder(SCMessages.i18n.tr("Необязательное поле"));

        final TextInput input = new TextInput();
        input.setEnabled(false);
        panel.add(input);
        input.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        input.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        input.setPlaceHolder(SCMessages.i18n.tr("Неактивное поле"));

        final TextInput inputAllow = new TextInput(false);
        panel.add(inputAllow);
        inputAllow.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        inputAllow.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        inputAllow.setPlaceHolder(SCMessages.i18n.tr("Обязательное поле"));

        final TextInput widthInput = new TextInput(false);
        panel.add(widthInput);
        widthInput.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        widthInput.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        widthInput.setPlaceHolder(SCMessages.i18n.tr("Широкое поле ввода"));
        widthInput.setWidth("300px");

        final TextInput smallWidthInput = new TextInput(false);
        panel.add(smallWidthInput);
        smallWidthInput.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        smallWidthInput.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        smallWidthInput.setPlaceHolder(SCMessages.i18n.tr("Маленькое поле ввода"));
        smallWidthInput.setWidth("100px");

        Panel textAreaPanel = new FlowPanel();
        textAreaPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);

        final NumberInput numberInput = new NumberInput();
        numberInput.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        numberInput.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        numberInput.setPlaceHolder(SCMessages.i18n.tr("Числовое поле ввода"));
        textAreaPanel.add(numberInput);

        final TagInput tags = new TagInput();
        tags.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        tags.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        tags.setWidth(300);
        textAreaPanel.add(tags);

        final TagInput tagInputList = new TagInput();
        tagInputList.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        tagInputList.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        tagInputList.setWidth(300);

        DropDownList<String> tagList = new DropDownList<String>(tagInputList, null);
        for (int i = 0; i < 5; i++) {
            tagList.addItem("аа " + i, "value " + i);
            tagList.addItem("аб" + i, "value " + i);
            tagList.addItem("пб" + i, "value " + i);
        }
        tagInputList.setDropDownList(tagList);

        textAreaPanel.add(tagInputList);

        final ArtaTextArea textArea = new ArtaTextArea();
        textArea.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        textArea.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        textArea.setPlaceHolder(SCMessages.i18n.tr("Многострочное поле ввода"));
        textAreaPanel.add(textArea);

        final ArtaTextArea disableTextArea = new ArtaTextArea(false);
        disableTextArea.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        disableTextArea.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        disableTextArea.setPlaceHolder(SCMessages.i18n.tr("Неактивное многострочное поле ввода"));
        disableTextArea.setEnabled(false);
        textAreaPanel.add(disableTextArea);

        final ArtaTextArea textAreaEmpty = new ArtaTextArea(false);
        textAreaEmpty.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        textAreaEmpty.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        textAreaEmpty.setPlaceHolder(SCMessages.i18n.tr("Обязательное многострочное поле ввода"));
        textAreaPanel.add(textAreaEmpty);

        final ArtaTextArea textAreaSize = new ArtaTextArea(false);
        textAreaSize.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        textAreaSize.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        textAreaSize.setPlaceHolder(SCMessages.i18n.tr("Многострочное поле ввода с заданным размером"));
        textAreaSize.setPixelSize(300, 300);
        textAreaPanel.add(textAreaSize);

        panel.add(textAreaPanel);

        SimpleButton button = new SimpleButton(SCMessages.i18n.tr("Валидация полей"), SimpleButton.Type.APPROVE);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                textInput.checkInput();
                input.checkInput();
                inputAllow.checkInput();
                widthInput.checkInput();
                smallWidthInput.checkInput();
                textArea.checkInput();
            }
        });
        Panel buttonPanel = new FlowPanel();
        buttonPanel.add(button);
        button.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        button.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        buttonPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        panel.add(buttonPanel);


        panel.setSize("100%", "100%");
        panel.setHeight("2000px");

        ArtaVerticalScrollPanel scroll = new ArtaVerticalScrollPanel();
        scroll.setWidget(panel);
        return scroll;

    }



    /**
     * Простые кнопки
     * @return
     */
    private Widget getSimpleButtonPanel() {
        final ContextMenu menu = createSimpleMenu();

        FlowPanel simpleButtonPanel = new FlowPanel_() {
            @Override
            public void onBrowserEvent(Event event) {
                switch(DOM.eventGetType(event)) {
                    case Event.ONCONTEXTMENU :
                        event.preventDefault();
                        menu.show(event.getClientX(), event.getClientY());
                        break;
                }
                super.onBrowserEvent(event);
            }
        };

        SimpleButton simpleButton = new SimpleButton(SCMessages.i18n.tr("Простая кнопка"));
        simpleButton.setWidth("140px");
        simpleButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton);

        SimpleButton simpleButton1 = new SimpleButton(SCMessages.i18n.tr("Неактивная кнопка"));
        simpleButton1.setEnabled(false);
        simpleButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButton1.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton1);

        SimpleButton simpleButton2 = new SimpleButton(SCMessages.i18n.tr("Кнопка с кликом"));
        simpleButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButton2.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton2);
        simpleButton2.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(SCMessages.i18n.tr("Кнопка была нажата!"));
            }
        });

        ContextMenu menuForSimple = createSimpleMenu();
        ContextMenuButton simpleButton4 = new ContextMenuButton(SCMessages.i18n.tr("Кнопка с меню"));
        simpleButton4.setWidth("140px");
        simpleButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButton4.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton4);
        simpleButton4.setContextMenu(menuForSimple);
        return simpleButtonPanel;
    }

    /**
     * Кнопки с иконками
     * @return
     */
    private Widget getIconButtonPanel() {
        FlowPanel iconButtonPanel = new FlowPanel();

        SimpleButton iconButton = new SimpleButton(SCMessages.i18n.tr("Кнопка с иконкой"), SCImageResources.IMPL.zoom());
        iconButtonPanel.add(iconButton);
        iconButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton iconButton1 = new SimpleButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), SCImageResources.IMPL.zoom());
        iconButtonPanel.add(iconButton1);
        iconButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton1.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton iconButton2 = new SimpleButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), SCImageResources.IMPL.zoom(), ButtonBase.IconPosition.RIGHT);
        iconButton2.setWidth("150px");
        iconButtonPanel.add(iconButton2);
        iconButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton2.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton iconButton3 = new SimpleButton(SCMessages.i18n.tr("Кнопка неактивная"), SCImageResources.IMPL.zoom());
        iconButton3.setWidth("200px");
        iconButtonPanel.add(iconButton3);
        iconButton3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton3.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        iconButton3.setEnabled(false);

        ImageButton iconButton4 = new ImageButton(SCImageResources.IMPL.zoom());
        iconButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton4.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        iconButtonPanel.add(iconButton4);

        ImageButton iconButton5 = new ImageButton(SCImageResources.IMPL.zoom());
        iconButton5.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton5.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        iconButton5.setEnabled(false);
        iconButtonPanel.add(iconButton5);

        ContextMenu menu2 = createSimpleMenu();
        ContextMenuButton iconButton6 = new ContextMenuButton(SCMessages.i18n.tr("Кнопка с меню"), SCImageResources.IMPL.zoom());
        iconButton6.setWidth("150px");
        iconButtonPanel.add(iconButton6);
        iconButton6.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton6.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        iconButton6.setContextMenu(menu2);

        ContextMenu menu4 = createSimpleMenu();
        ContextMenuButton iconButton7 = new ContextMenuButton(SCMessages.i18n.tr("Кнопка с меню"), SCImageResources.IMPL.zoom(), ButtonBase.IconPosition.RIGHT);
        iconButton7.setWidth("150px");
        iconButtonPanel.add(iconButton7);
        iconButton7.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton7.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        iconButton7.setContextMenu(menu4);

        ContextMenu menu5 = createSimpleMenu();
        ContextMenuButton iconButton8 = new ContextMenuButton(SCMessages.i18n.tr("Кнопка с меню"), SCImageResources.IMPL.zoom(), ButtonBase.IconPosition.RIGHT);
        iconButton8.setWidth("400px");
        iconButtonPanel.add(iconButton8);
        iconButton8.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        iconButton8.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        iconButton8.setContextMenu(menu5);
        return iconButtonPanel;
    }



    /**
     * Цветные кнопки
     * @return
     */
    private Widget getColorButtonPanel() {
        FlowPanel colorButtonPanel = new FlowPanel();
        colorButtonPanel.setHeight("2000px");

        SimpleButton colorButton = new SimpleButton((SCMessages.i18n.tr("Создать")), SimpleButton.Type.APPROVE);
        colorButtonPanel.add(colorButton);
        colorButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton colorButton1 = new SimpleButton(SCMessages.i18n.tr("Удалить"), SimpleButton.Type.DECLINE);
        colorButtonPanel.add(colorButton1);
        colorButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton1.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton colorButton2 = new SimpleButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), SimpleButton.Type.DECLINE);
        colorButtonPanel.add(colorButton2);
        colorButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton2.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton colorButton3 = new SimpleButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), SimpleButton.Type.DECLINE);
        colorButton3.setWidth("100px");
        colorButtonPanel.add(colorButton3);
        colorButton3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton3.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton colorButton4 = new SimpleButton(SCMessages.i18n.tr("Кнопка с длинным текстом"), SimpleButton.Type.APPROVE);
        colorButton4.setWidth("100px");
        colorButtonPanel.add(colorButton4);
        colorButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton4.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton colorButton5 = new SimpleButton(SCMessages.i18n.tr("Кнопка неактивная"), SimpleButton.Type.APPROVE);
        colorButton5.setEnabled(false);
        colorButtonPanel.add(colorButton5);
        colorButton5.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton5.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        SimpleButton colorButton6 = new SimpleButton(SCMessages.i18n.tr("Кнопка неактивная"), SimpleButton.Type.DECLINE);
        colorButton6.setEnabled(false);
        colorButtonPanel.add(colorButton6);
        colorButton6.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton6.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        ContextMenuButton colorButton7 = new ContextMenuButton(SCMessages.i18n.tr("Кнопка с меню"), SimpleButton.Type.APPROVE);
        colorButtonPanel.add(colorButton7);
        colorButton7.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton7.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        ContextMenu menu3 = createSimpleMenu();
        menu3.addItem(SCMessages.i18n.tr("Очень-очень длинный текст"), null);
        colorButton7.setContextMenu(menu3);

        ArtaVerticalScrollPanel scroll = new ArtaVerticalScrollPanel();
        scroll.setWidget(colorButtonPanel);
        return scroll;
    }

    /**
     * Групповые кнопки
     * @return
     */
    private Widget getGroupButton() {
        FlowPanel panel = new FlowPanel();
        final SimpleToggleButton toggleButton = new SimpleToggleButton("Кнопка");
        toggleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (toggleButton.isPressed()) {
                    toggleButton.setText("Нажата");
                } else {
                    toggleButton.setText("Не нажата");
                }
            }
        });
        toggleButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        toggleButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(toggleButton);


        GroupButtonPanel groupButtonPanel = new GroupButtonPanel(true);
        groupButtonPanel.addButton("Первая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("Первая кнопка");
            }
        });
        groupButtonPanel.addButton("Вторая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("Вторая кнопка");
            }
        });
        groupButtonPanel.addButton("Третья", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("Третья кнопка");
            }
        });
        groupButtonPanel.buildPanel();
        groupButtonPanel.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel);

        GroupButtonPanel groupButtonPanel1 = new GroupButtonPanel(true, true);
        groupButtonPanel1.addButton("Первая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel1.addButton("Вторая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel1.addButton("Третья", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel1.buildPanel();
        groupButtonPanel1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel1.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel1);

        GroupButtonPanel groupButtonPanel2 = new GroupButtonPanel(true);
        groupButtonPanel2.setAllowEmptyToggle(false);
        groupButtonPanel2.addButton("Первая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.addButton("Вторая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.addButton("Третья", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.buildPanel();
        groupButtonPanel2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel2.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel2);

        GroupButtonPanel groupButtonPanel3 = new GroupButtonPanel(true, true);
        groupButtonPanel3.addButton("Первая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.addButton("Вторая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.addButton("Третья", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.buildPanel();
        groupButtonPanel3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel3.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel3);

        GroupButtonPanel groupButtonPanel4 = new GroupButtonPanel();
        groupButtonPanel4.addButton("Первая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.addButton("Вторая", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.addButton("Третья", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.buildPanel();
        groupButtonPanel4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel4.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel4);

        return panel;
    }

    private ContextMenu createSimpleMenu() {
        ContextMenu menu = new ContextMenu();
        menu.addItem("Zoom", ImageResources.IMPL.zoom(), null);
        menu.addItem("Left", ImageResources.IMPL.navigationLeft(), null);
        menu.addSeparator();
        menu.addItem("Right", ImageResources.IMPL.navigationRight(), null);
        return menu;
    }

    private void setTheme(Theme theme) {
        if (currentTheme != theme) {
            currentTheme = theme;
            Cookies.setCookie("theme", theme.name());
            Window.Location.reload();
        }
    }
}
