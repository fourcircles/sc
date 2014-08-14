package kz.arta.sc3.showcase.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DatePicker;
import kz.arta.sc3.showcase.client.resources.SCImageResources;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.ComboBox;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ButtonBase;
import kz.arta.synergy.components.client.button.*;
import kz.arta.synergy.components.client.dialog.Dialog;
import kz.arta.synergy.components.client.dialog.DialogSimple;
import kz.arta.synergy.components.client.input.ArtaTextArea;
import kz.arta.synergy.components.client.input.SearchResultInput;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.client.input.date.ArtaDatePicker;
import kz.arta.synergy.components.client.input.date.DateInput;
import kz.arta.synergy.components.client.input.date.DateTimeInput;
import kz.arta.synergy.components.client.input.date.TimeInput;
import kz.arta.synergy.components.client.input.tags.ObjectChooser;
import kz.arta.synergy.components.client.input.tags.TagInput;
import kz.arta.synergy.components.client.menu.ContextMenu;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.DropDownListMulti;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.theme.Theme;
import kz.arta.synergy.components.client.util.PPanel;
import kz.arta.synergy.components.style.client.Constants;

import java.util.HashSet;
import java.util.Set;

//todo реализовать отложенную загрузку
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

    Tree tree;

    TabLayoutPanel contentPanel;

    private Set<LoadPanel> tabs;

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
        navigationPanel.setWidth("100%");
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
                        "\nRevision: " + theme.get("revision") + "\nBuild stamp: " + theme.get("build_stamp"));
            }
        });
        titlePanel.add(about);
        about.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);


        ComboBox<Theme> themesCombo = new ComboBox<Theme>();
        for (Theme th : Theme.values()) {
            themesCombo.addItem(th.name(), th);
        }
        themesCombo.addValueChangeHandler(new ValueChangeHandler<Theme>() {
            @Override
            public void onValueChange(ValueChangeEvent<Theme> event) {
                setTheme(event.getValue());
            }
        });
        themesCombo.selectValue(currentTheme, false);
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
        localesCombo.selectValue(chosenLocale, false);
        localesCombo.getElement().getStyle().setMarginRight(20, Style.Unit.PX);
        titlePanel.add(localesCombo);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            RootPanel.getBodyElement().getStyle().setProperty("direction", HasDirection.Direction.RTL.name());
        }

        titlePanel.setWidth("100%");
        showCaseLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
        themesCombo.getElement().getStyle().setFloat(Style.Float.RIGHT);
        localesCombo.getElement().getStyle().setFloat(Style.Float.RIGHT);
        about.getElement().getStyle().setFloat(Style.Float.RIGHT);

        ArtaScrollPanel treeScroll = new ArtaScrollPanel(navigationPanel);
        add(titlePanel);
        add(treeScroll);
        add(contentPanel);

        setWidgetLeftWidth(treeScroll, 0, Style.Unit.PCT, TREE_WIDTH, Style.Unit.PCT);
        setWidgetTopBottom(treeScroll, TITLE_HEIGHT + SPACING, Style.Unit.PCT, 0, Style.Unit.PCT);

        setWidgetRightWidth(contentPanel, 0, Style.Unit.PCT, 100 - TREE_WIDTH - SPACING, Style.Unit.PCT);
        setWidgetTopBottom(contentPanel, TITLE_HEIGHT + SPACING, Style.Unit.PCT, 0, Style.Unit.PCT);

        setWidgetTopBottom(titlePanel, 0, Style.Unit.PCT, 100 - TITLE_HEIGHT, Style.Unit.PCT);

        setBorder(treeScroll);
        setBorder(titlePanel);
        setBorder(contentPanel);

        setSize("100%", "100%");

        forceLayout();

        tabs = new HashSet<LoadPanel>();

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                TreeItem selectedItem = event.getSelectedItem();
                if (selectedItem != null && selectedItem.getUserObject() != null) {
                    ((LoadPanel) selectedItem.getUserObject()).execute();
                }
            }
        });

    }

    public TreeItem addCategory(String text) {
        TreeItem item = new TreeItem();
        Label label = new Label(text);
        item.setWidget(label);
        tree.addItem(item);

        return item;
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
        FlowPanel panel = new FlowPanel();

        if (!buttons) {
            SimpleButton empty = setUpDialog(SCMessages.i18n.tr("Пустой"), new DialogSimple());
            empty.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
            panel.add(empty);
        }
        SimpleButton tiny = setUpDialog(116, 84, buttons, true, true);
        tiny.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        SimpleButton small = setUpDialog(300, 300, buttons, true, true);
        small.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        SimpleButton middle = setUpDialog(400, 400, buttons, true, true);
        middle.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        SimpleButton big = setUpDialog(800, 500, buttons, true, true);
        big.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(tiny);
        panel.add(small);
        panel.add(middle);
        panel.add(big);
        panel.add(new HTML("<p/>"));
        if (buttons) {
            SimpleButton noLeft = setUpDialog(400, 400, true, false, true);
            noLeft.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
            SimpleButton noRight = setUpDialog(400, 400, true, true, false);
            noRight.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
            SimpleButton onlySave = setUpDialog(400, 400, true, false, false);
            onlySave.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

            panel.add(noLeft);
            panel.add(noRight);
            panel.add(onlySave);
        }

        panel.setSize("100%", "100%");

        return panel;
    }

    private void fillCombobox(ComboBox<String> combobox) {
        combobox.addItem(SCMessages.i18n.tr("Приблизить"), ImageResources.IMPL.zoom(), SCMessages.i18n.tr("Приблизить"));
        combobox.addItem(SCMessages.i18n.tr("Налево"), ImageResources.IMPL.navigationLeft(), SCMessages.i18n.tr("Налево"));
        combobox.addItem(SCMessages.i18n.tr("Направо"), ImageResources.IMPL.navigationRight(), SCMessages.i18n.tr("Направо"));
        combobox.addItem(SCMessages.i18n.tr("Простооченьдлинныйпунктменю,чтобыпосмотретьчтопроисходит"),
                SCMessages.i18n.tr("Простооченьдлинныйпунктменю,чтобыпосмотретьчтопроисходит"));
        for (int i = 1; i < 30; i++) {
            combobox.addItem(SCMessages.i18n.tr("Пункт меню ") + i, SCMessages.i18n.tr("Пункт меню ") + i);
        }
    }
    public Widget getComboboxPanel() {
//        FlowPanel comboBoxPanel = new FlowPanel();
//
//        final ComboBox<String> combo = new ComboBox<String>();
//        fillCombobox(combo);
//        combo.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
//        combo.setReadOnly(false);
//        comboBoxPanel.add(combo);
//
//        ComboBox<Integer> statesCombo = new ComboBox<Integer>();
//        comboBoxPanel.add(statesCombo);
//
//        statesCombo.setReadOnly(true);
//
//        statesCombo.addItem("Включен editable", 1);
//        statesCombo.addItem("Включен readonly", 2);
//        statesCombo.addItem("Выключены", 3);
//
//        statesCombo.addValueChangeHandler(new ValueChangeHandler<Integer>() {
//            @Override
//            public void onValueChange(ValueChangeEvent<Integer> event) {
//                switch (event.getValue()) {
//                    case 1:
//                        combo.setEnabled(true);
//                        combo.setReadOnly(false);
//                        break;
//                    case 2:
//                        combo.setEnabled(true);
//                        combo.setReadOnly(true);
//                        break;
//                    case 3:
//                        combo.setEnabled(false);
//                }
//            }
//        });
//
//
//        return comboBoxPanel;
        FlowPanel comboBoxPanel = new FlowPanel();

        ComboBox<String> combo1 = new ComboBox<String>();
        fillCombobox(combo1);
        combo1.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        combo1.setReadOnly(false);
        comboBoxPanel.add(combo1);

        ComboBox comboDisabled = new ComboBox();
        comboDisabled.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        comboDisabled.setEnabled(false);
        comboBoxPanel.add(comboDisabled);

        ComboBox<String> comboReadOnly = new ComboBox<String>();
        fillCombobox(comboReadOnly);
        comboReadOnly.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        comboReadOnly.setReadOnly(true);
        comboReadOnly.setWidth(250);
        comboBoxPanel.add(comboReadOnly);

        return comboBoxPanel;
    }

    private static class FlowPanel_ extends FlowPanel {
        public FlowPanel_() {
            super();
            sinkEvents(Event.ONCONTEXTMENU);
        }
    }

    private abstract class LoadPanel implements Command {
        private Widget content;
        private Widget tabWidget;

        @Override
        public void execute() {
            if (!tabs.contains(this)) {
                addTab();
                tabs.add(this);
            } else {
                selectTab();
            }
        }

        public void addTab() {
            if (content == null) {
                content = getContentWidget();
            }
            if (tabWidget == null) {
                tabWidget = createTabWidget();
            }
            contentPanel.add(content, tabWidget);
            contentPanel.selectTab(content);
        }

        private FlowPanel createTabWidget() {
            FlowPanel tab = new FlowPanel();

            InlineLabel textLabel = new InlineLabel(getText());
            textLabel.getElement().getStyle().setDisplay(Style.Display.INLINE);
            tab.add(textLabel);

            InlineLabel closeLabel = new InlineLabel("[X]");
            closeLabel.getElement().getStyle().setDisplay(Style.Display.INLINE);
            closeLabel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (content != null) {
                        contentPanel.remove(content);
                    }
                    tabs.remove(LoadPanel.this);
                }
            });
            tab.add(closeLabel);
            return tab;
        }

        public void selectTab() {
            contentPanel.selectTab(content);
        }

        public abstract String getText();
        public abstract Widget getContentWidget();
    }

    private void addTreeItem(TreeItem parentItem, LoadPanel loadPanel) {
        TreeItem newItem = new TreeItem();
        newItem.setText(loadPanel.getText());
        newItem.setUserObject(loadPanel);
        parentItem.addItem(newItem);
    }
    private void treeSetUp() {
        tree = new Tree();

        TreeItem category1 = addCategory(SCMessages.i18n.tr("Кнопки"));
        TreeItem category2 = addCategory(SCMessages.i18n.tr("Диалог"));
        TreeItem category3 = addCategory(SCMessages.i18n.tr("Поля ввода"));

        addTreeItem(category1, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Простая кнопка");
            }

            @Override
            public Widget getContentWidget() {
                return getSimpleButtonPanel();
            }
        });
        addTreeItem(category1, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Кнопка с иконкой");
            }

            @Override
            public Widget getContentWidget() {
                return getIconButtonPanel();
            }
        });
        addTreeItem(category1, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Кнопки");
            }

            @Override
            public Widget getContentWidget() {
                return getColorButtonPanel();
            }
        });
        addTreeItem(category1, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Групповые кнопки");
            }

            @Override
            public Widget getContentWidget() {
                return getGroupButton();
            }
        });

        addTreeItem(category2, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Диалог без кнопок");
            }

            @Override
            public Widget getContentWidget() {
                return setUpDialogs(false);
            }
        });
        addTreeItem(category2, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Диалог с кнопками");
            }

            @Override
            public Widget getContentWidget() {
                return setUpDialogs(true);
            }
        });

        addTreeItem(category3, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Поле ввода текста");
            }

            @Override
            public Widget getContentWidget() {
                return getTextInputs();
            }
        });
        addTreeItem(category3, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Поле с тегами");
            }

            @Override
            public Widget getContentWidget() {
                return getTagInputs();
            }
        });
        addTreeItem(category3, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Комбобокс");
            }

            @Override
            public Widget getContentWidget() {
                return getComboboxPanel();
            }
        });
        addTreeItem(category3, new LoadPanel() {
            @Override
            public String getText() {
                return SCMessages.i18n.tr("Дата/время");
            }

            @Override
            public Widget getContentWidget() {
                return getDateInputs();
            }
        });
    }

    /**
     * Панель с компонентами дата/время
     */
    private Widget getDateInputs() {
        FlowPanel panel = new FlowPanel();

        int width = 300;

        PPanel firstRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel firstRowPanel = new FlowPanel();
        firstRow.setWidget(firstRowPanel);
        firstRowPanel.add(createLabel(SCMessages.i18n.tr("Поле время: "), width));
        TimeInput timeInputNotAllowEmpty = new TimeInput();
        timeInputNotAllowEmpty.setTitle(SCMessages.i18n.tr("Обязательное поле ввода"));
        firstRowPanel.add(timeInputNotAllowEmpty);
        TimeInput timeInputAllowEmpty = new TimeInput(true);
        timeInputAllowEmpty.setTitle(SCMessages.i18n.tr("Необязательное поле ввода"));
        firstRowPanel.add(timeInputAllowEmpty);
        TimeInput timeInput = new TimeInput();
        timeInput.setTitle(SCMessages.i18n.tr("Неактивное поле ввода"));
        timeInput.setEnabled(false);
        firstRowPanel.add(timeInput);
        panel.add(firstRow);
        firstRow.getElement().getStyle().setPadding(5, Style.Unit.PX);
        for (int i = 1; i < firstRowPanel.getWidgetCount(); i++) {
            firstRowPanel.getWidget(i).getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        }

        PPanel secondRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel secondRowPanel = new FlowPanel();
        secondRow.setWidget(secondRowPanel);
        secondRowPanel.add(createLabel(SCMessages.i18n.tr("Поле дата - режим выбора день: "), width));
        DateInput dateInput= new DateInput();
        dateInput.setTitle(SCMessages.i18n.tr("Обязательное поле ввода"));
        secondRowPanel.add(dateInput);
        DateInput dateInput1 = new DateInput(true);
        dateInput1.setTitle(SCMessages.i18n.tr("Необязательное поле ввода"));
        secondRowPanel.add(dateInput1);

        DateInput dateInput2 = new DateInput();
        dateInput2.setTitle(SCMessages.i18n.tr("Неактивное поле ввода"));
        dateInput2.setEnabled(false);
        secondRowPanel.add(dateInput2);
        panel.add(secondRow);
        secondRow.getElement().getStyle().setPadding(5, Style.Unit.PX);
        for (int i = 1; i < secondRowPanel.getWidgetCount(); i++) {
            secondRowPanel.getWidget(i).getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        }

        PPanel thirdRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel thirdRowPanel = new FlowPanel();
        thirdRow.setWidget(thirdRowPanel);
        thirdRowPanel.add(createLabel(SCMessages.i18n.tr("Поле дата - режим выбора неделя и месяц: "), width));
        DateInput dateInputWeek = new DateInput(ArtaDatePicker.CalendarMode.WEEK);
        dateInputWeek.setTitle(SCMessages.i18n.tr("Неделя"));
        thirdRowPanel.add(dateInputWeek);
        DateInput dateInputMonth = new DateInput(ArtaDatePicker.CalendarMode.MONTH);
        dateInputMonth.setTitle(SCMessages.i18n.tr("Месяц"));
        thirdRowPanel.add(dateInputMonth);
        DateInput dateInputMonthDisabled = new DateInput(ArtaDatePicker.CalendarMode.MONTH);
        dateInputMonthDisabled.setEnabled(false);
        dateInputMonthDisabled.setTitle(SCMessages.i18n.tr("Неактивное поле ввода"));
        thirdRowPanel.add(dateInputMonthDisabled);
        panel.add(thirdRow);
        thirdRow.getElement().getStyle().setPadding(5, Style.Unit.PX);
        for (int i = 1; i < thirdRowPanel.getWidgetCount(); i++) {
            thirdRowPanel.getWidget(i).getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        }

        PPanel fourthRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel fourthRowPanel = new FlowPanel();
        fourthRow.setWidget(fourthRowPanel);
        fourthRowPanel.add(createLabel(SCMessages.i18n.tr("Поле ввода даты/времени: "), width));
        DateTimeInput dateTimeInput = new DateTimeInput();
        dateTimeInput.setTitle(SCMessages.i18n.tr("Обязательное поле ввода"));
        fourthRowPanel.add(dateTimeInput);

        DateTimeInput dateTimeInput1 = new DateTimeInput(true);
        dateTimeInput1.setTitle(SCMessages.i18n.tr("Необязательное поле ввода"));
        fourthRowPanel.add(dateTimeInput1);

        DateTimeInput dateTimeInput2 = new DateTimeInput(true);
        dateTimeInput2.setTitle(SCMessages.i18n.tr("Неактивное поле ввода"));
        dateTimeInput2.setEnabled(false);
        fourthRowPanel.add(dateTimeInput2);
        panel.add(fourthRow);
        fourthRow.getElement().getStyle().setPadding(5, Style.Unit.PX);
        for (int i = 1; i < fourthRowPanel.getWidgetCount(); i++) {
            fourthRowPanel.getWidget(i).getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        }

        FlowPanel datePickerPanel = new FlowPanel();
        datePickerPanel.add(new ArtaDatePicker());
        panel.add(datePickerPanel);
        datePickerPanel.getElement().getStyle().setPadding(5, Style.Unit.PX);

        FlowPanel datePickerPanel1 = new FlowPanel();
        datePickerPanel1.add(new ArtaDatePicker(ArtaDatePicker.CalendarMode.WEEK));
        panel.add(datePickerPanel1);
        datePickerPanel1.getElement().getStyle().setPadding(5, Style.Unit.PX);

        FlowPanel datePickerPanel2 = new FlowPanel();
        datePickerPanel2.add(new ArtaDatePicker(ArtaDatePicker.CalendarMode.MONTH));
        panel.add(datePickerPanel2);
        datePickerPanel2.getElement().getStyle().setPadding(5, Style.Unit.PX);

        FlowPanel dateTest = new FlowPanel();
        dateTest.add(new DatePicker());
        panel.add(dateTest);
        dateTest.getElement().getStyle().setPadding(5, Style.Unit.PX);

        return new ArtaScrollPanel(panel);
    }
    /**
     * Смешивает массив
     * @param array массив
     */
    private static void shuffle(Object[] array) {
        for (int i = array.length; i > 1; i--) {
            int randomPos = Random.nextInt(i);

            Object tmp = array[i - 1];
            array[i - 1] = array[randomPos];
            array[randomPos] = tmp;
        }
    }

    private static String[] join(String[] array1, String[] array2) {
        String[] res = new String[array1.length * (array2.length + 1)];
        int i = 0;
        for (String left : array1) {
            for (String right: array2) {
                res[i++] = left + " " + right;
            }
            res[i++] = left;
        }
        return res;
    }

    private String[] createShuffledNames() {
        String[] firstNames = new String[]{"Bill", "Vasya", "Jane", "Steve"};
        String[] lastNames = new String[]{"Gates", "Pupkin", "Jones", "Jobs"};
        String[] names = join(firstNames, lastNames);
        shuffle(names);
        return names;
    }

    private DropDownList<String> createList(Widget relativeWidget) {
        DropDownList<String> list = new DropDownList<String>(relativeWidget, new SimpleEventBus());

        for (String name : createShuffledNames()) {
            list.addItem(name, name);
        }

        return list;
    }

    private DropDownListMulti<String> createMultiList(Widget parent) {
        DropDownListMulti<String> multiList = new DropDownListMulti<String>(parent, new SimpleEventBus());

        String[] names = createShuffledNames();
        for (String name : names) {
            multiList.addItem(name, name);
        }

        return multiList;
    }

    private static InlineLabel createLabel(String text) {
        return createLabel(text, 180);
    }

    private static InlineLabel createLabel(String text, int width) {
        InlineLabel label = new InlineLabel(SCMessages.i18n.tr(text));
        label.setStyleName(SynergyComponents.resources.cssComponents().mainText());
        label.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        label.setWidth(width + "px");
        return label;
    }

    /**
     * Поля с тегами
     * @return панель с полями с тегами
     */
    private Widget getTagInputs() {

        FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setLineHeight(1, Style.Unit.PX);

        PPanel firstRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel firstRowPanel = new FlowPanel();
        firstRow.setWidget(firstRowPanel);
        firstRowPanel.add(createLabel("Поля с индикаторами: "));

        PPanel thirdRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel thirdRowPanel = new FlowPanel();
        thirdRow.setWidget(thirdRowPanel);
        thirdRowPanel.add(createLabel("Поля без кнопки: "));

        PPanel forthRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel forthRowPanel = new FlowPanel();
        forthRow.setWidget(forthRowPanel);
        forthRowPanel.add(createLabel("Мультикомбобокс: "));

        PPanel fifthRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel fifthRowPanel = new FlowPanel();
        fifthRow.setWidget(fifthRowPanel);
        fifthRowPanel.add(createLabel("Выбор объекта: "));

        final TagInput noListHasIndicator= new TagInput();
        noListHasIndicator.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        noListHasIndicator.setWidth(350);
        firstRowPanel.add(noListHasIndicator);

        final TagInput<String> hasListHasIndicator = new TagInput<String>();
        hasListHasIndicator.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        hasListHasIndicator.setWidth(300);
        hasListHasIndicator.setDropDownList(createMultiList(hasListHasIndicator));

        hasListHasIndicator.setTitle(SCMessages.i18n.tr("Фильтрация списка по вхождению текста"));
        hasListHasIndicator.setFilterType(false);

        firstRowPanel.add(hasListHasIndicator);

        firstRow.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        panel.add(firstRow);

        TagInput noListNoButton = new TagInput(false);
        noListNoButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        thirdRowPanel.add(noListNoButton);

        TagInput<String> hasListNoButton = new TagInput<String>(false);
        hasListNoButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        hasListNoButton.setDropDownList(createMultiList(hasListNoButton));
        hasListNoButton.setTitle(SCMessages.i18n.tr("Префиксный выбор из списка"));
        thirdRowPanel.add(hasListNoButton);

        thirdRow.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        panel.add(thirdRow);

        TagInput<String> multiComboBox = new TagInput<String>();
        multiComboBox.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        multiComboBox.setMultiComboBox(true);
        multiComboBox.setDropDownList(createMultiList(multiComboBox));
        multiComboBox.setWidth(300);
        forthRowPanel.add(multiComboBox);

        panel.add(forthRow);
        forthRow.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        ObjectChooser chooser = new ObjectChooser(new SimpleEventBus());
        chooser.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        fifthRowPanel.add(chooser);

        panel.add(fifthRow);

        return panel;
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

        Panel searchResultPanel = new FlowPanel();
        searchResultPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        searchResultPanel.setHeight("32px");
        searchResultPanel.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        SearchResultInput<String> searchEnabledWithButton = new SearchResultInput<String>(true);
        searchEnabledWithButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        DropDownList<String> list = createList(searchEnabledWithButton);
        for (DropDownList.Item item : list.getItems()) {
            item.setIcon(ImageResources.IMPL.magzhan());
        }
        searchEnabledWithButton.setList(list);
        searchResultPanel.add(searchEnabledWithButton);

        SearchResultInput<String> searchDisabledWithButton = new SearchResultInput<String>(true);
        searchDisabledWithButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        DropDownList<String> list2 = createList(searchDisabledWithButton);
        for (DropDownList.Item item : list2.getItems()) {
            item.setIcon(ImageResources.IMPL.magzhan());
        }
        searchDisabledWithButton.setList(list2);
        searchDisabledWithButton.setEnabled(false);
        searchResultPanel.add(searchDisabledWithButton);

        SearchResultInput<String> searchNoButton = new SearchResultInput<String>(false);
        searchNoButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        DropDownList<String> list3 = createList(searchNoButton);
        searchNoButton.setList(list3);
        searchResultPanel.add(searchNoButton);

        panel.add(searchResultPanel);


        Panel textAreaPanel = new FlowPanel();
        textAreaPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);

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

        final ArtaTextArea textAreaWidth = new ArtaTextArea(false);
        textAreaWidth.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        textAreaWidth.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        textAreaWidth.setPlaceHolder(SCMessages.i18n.tr("Многострочное поле ввода с заданной шириной"));
        textAreaWidth.setWidth("500px");
        textAreaPanel.add(textAreaWidth);
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
                textAreaEmpty.checkInput();
                textAreaSize.checkInput();
                textAreaWidth.checkInput();
            }
        });
        Panel buttonPanel = new FlowPanel();
        buttonPanel.add(button);
        button.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        button.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        buttonPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        panel.add(buttonPanel);

        ArtaScrollPanel scroll = new ArtaScrollPanel();
        scroll.setWidget(panel);

        return panel;

    }



    /**
     * Простые кнопки
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
        simpleButton4.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(SCMessages.i18n.tr("Кнопка с меню была нажата!"));
            }
        });
        simpleButton4.setWidth("140px");
        simpleButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButton4.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton4);
        simpleButton4.setContextMenu(menuForSimple);

//        Tabs tabs = new Tabs();
//        simpleButtonPanel.add(tabs);
//        tabs.addTab("first tab");
//        tabs.addTab("second tab");
//        tabs.addTab("third tab");
//        tabs.addTab("very very very very very long tab");

        return simpleButtonPanel;
    }

    /**
     * Кнопки с иконками
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
     */
    private Widget getColorButtonPanel() {
        FlowPanel colorButtonPanel = new FlowPanel();
        colorButtonPanel.setHeight("2000px");
        colorButtonPanel.setWidth("2000px");

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
        colorButton7.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(SCMessages.i18n.tr("Кнопка с меню была нажата!"));
            }
        });
        colorButton7.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        colorButton7.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        ContextMenu menu3 = createSimpleMenu();
        menu3.addItem(SCMessages.i18n.tr("Очень-очень длинный текст"), null);
        colorButton7.setContextMenu(menu3);

        ArtaScrollPanel scroll = new ArtaScrollPanel();
        scroll.setWidget(colorButtonPanel);
        return scroll;
    }

    /**
     * Групповые кнопки
     */
    private Widget getGroupButton() {
        FlowPanel panel = new FlowPanel();
        final SimpleToggleButton toggleButton = new SimpleToggleButton(SCMessages.i18n.tr("Кнопка с длинным текстом"));
        toggleButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        toggleButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(toggleButton);

        final SimpleToggleButton toggleButton1 = new SimpleToggleButton(SCMessages.i18n.tr("Не нажата"));
        toggleButton1.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (toggleButton1.isPressed()) {
                    toggleButton1.setText(SCMessages.i18n.tr("Нажата"));
                } else {
                    toggleButton1.setText(SCMessages.i18n.tr("Не нажата"));
                }
            }
        });
        toggleButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        toggleButton1.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(toggleButton1);


        GroupButtonPanel groupButtonPanel = new GroupButtonPanel(true);
        groupButtonPanel.addButton(SCMessages.i18n.tr("Первая кнопка длинная"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            }
        });
        groupButtonPanel.addButton(SCMessages.i18n.tr("Вторая"), new ClickHandler() {
            @Override
                public void onClick(ClickEvent event) {
                if (((SimpleToggleButton) event.getSource()).isPressed()) {
                    ((SimpleToggleButton) event.getSource()).setText(SCMessages.i18n.tr("Вторая нажата"));
                } else {
                    ((SimpleToggleButton) event.getSource()).setText(SCMessages.i18n.tr("Вторая"));
                }
            }
        });
        groupButtonPanel.addButton(SCMessages.i18n.tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (((SimpleToggleButton) event.getSource()).isPressed()) {
                    ((SimpleToggleButton) event.getSource()).setText(SCMessages.i18n.tr("Третья нажата"));
                } else {
                    ((SimpleToggleButton) event.getSource()).setText(SCMessages.i18n.tr("Третья"));
                }
            }
        });
        groupButtonPanel.buildPanel();
        groupButtonPanel.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel);

        GroupButtonPanel groupButtonPanel1 = new GroupButtonPanel(true, true);
        groupButtonPanel1.addButton(SCMessages.i18n.tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel1.addButton(SCMessages.i18n.tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel1.addButton(SCMessages.i18n.tr("Третья"), new ClickHandler() {
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
        groupButtonPanel2.addButton(SCMessages.i18n.tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.addButton(SCMessages.i18n.tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.addButton(SCMessages.i18n.tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.buildPanel();
        groupButtonPanel2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel2.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel2);

        GroupButtonPanel groupButtonPanel3 = new GroupButtonPanel(true, true);
        groupButtonPanel3.addButton(SCMessages.i18n.tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.addButton(SCMessages.i18n.tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.addButton(SCMessages.i18n.tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.buildPanel();
        groupButtonPanel3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel3.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel3);

        GroupButtonPanel groupButtonPanel4 = new GroupButtonPanel();
        groupButtonPanel4.addButton(SCMessages.i18n.tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.addButton(SCMessages.i18n.tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.addButton(SCMessages.i18n.tr("Третья"), new ClickHandler() {
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
