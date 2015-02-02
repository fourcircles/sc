package kz.arta.sc3.showcase.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RowCountChangeEvent;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.*;
import kz.arta.synergy.components.client.button.ButtonBase;
import kz.arta.synergy.components.client.button.*;
import kz.arta.synergy.components.client.checkbox.ArtaCheckBox;
import kz.arta.synergy.components.client.checkbox.ArtaRadioButton;
import kz.arta.synergy.components.client.collapsing.CollapsingPanel;
import kz.arta.synergy.components.client.comments.*;
import kz.arta.synergy.components.client.dialog.Dialog;
import kz.arta.synergy.components.client.dialog.DialogSimple;
import kz.arta.synergy.components.client.input.ArtaTextArea;
import kz.arta.synergy.components.client.input.FilesPanel;
import kz.arta.synergy.components.client.input.SearchResultInput;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.client.input.date.ArtaDatePicker;
import kz.arta.synergy.components.client.input.date.DateInput;
import kz.arta.synergy.components.client.input.date.DateTimeInput;
import kz.arta.synergy.components.client.input.date.TimeInput;
import kz.arta.synergy.components.client.input.date.repeat.FullRepeatChooser;
import kz.arta.synergy.components.client.input.date.repeat.RepeatChooser;
import kz.arta.synergy.components.client.input.date.repeat.RepeatDate;
import kz.arta.synergy.components.client.input.number.*;
import kz.arta.synergy.components.client.input.tags.MultiComboBox;
import kz.arta.synergy.components.client.input.tags.ObjectChooser;
import kz.arta.synergy.components.client.input.tags.TagInput;
import kz.arta.synergy.components.client.menu.ContextMenu;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.DropDownListMulti;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.menu.filters.ListTextFilter;
import kz.arta.synergy.components.client.path.Path;
import kz.arta.synergy.components.client.path.PathItem;
import kz.arta.synergy.components.client.progressbar.ProgressBar;
import kz.arta.synergy.components.client.progressbar.ProgressBarConstraintLabel;
import kz.arta.synergy.components.client.progressbar.ProgressBarCustomLabel;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.stack.SingleStack;
import kz.arta.synergy.components.client.stack.StackPanel;
import kz.arta.synergy.components.client.stack.events.StackOpenEvent;
import kz.arta.synergy.components.client.table.*;
import kz.arta.synergy.components.client.table.column.*;
import kz.arta.synergy.components.client.table.events.TableHeaderMenuEvent;
import kz.arta.synergy.components.client.table.events.TableMenuEvent;
import kz.arta.synergy.components.client.table.events.TableSortEvent;
import kz.arta.synergy.components.client.tabs.TabPanel;
import kz.arta.synergy.components.client.tabs.events.TabCloseEvent;
import kz.arta.synergy.components.client.taskbar.TaskBar;
import kz.arta.synergy.components.client.taskbar.events.TaskBarEvent;
import kz.arta.synergy.components.client.theme.ColorType;
import kz.arta.synergy.components.client.theme.Theme;
import kz.arta.synergy.components.client.tree.FavoriteTree;
import kz.arta.synergy.components.client.tree.Tree;
import kz.arta.synergy.components.client.tree.TreeItem;
import kz.arta.synergy.components.client.tree.events.TreeItemContextMenuEvent;
import kz.arta.synergy.components.client.tree.events.TreeSelectionEvent;
import kz.arta.synergy.components.client.util.Br;
import kz.arta.synergy.components.client.util.PPanel;
import kz.arta.synergy.components.client.util.StyleUtils;
import kz.arta.synergy.components.style.client.Colors;
import kz.arta.synergy.components.style.client.Constants;

import java.util.*;

/**
 * User: vsl
 * Date: 23.06.14
 * Time: 12:43
 */
public class ShowCasePanel extends FlowPanel {
    private static final String THEME_COOKIE = "theme";
    private static final int LOCAL_TREE_SIZE = 400;
    public static final String DEFAULT_LOCALE = "default";
    public static final String DIRECTION_PROPERTY = "direction";

    private Theme currentTheme;

    @UiField Tree tree;
    @UiField TabPanel tabPanel;
    @UiField FlowPanel titlePanel;
    @UiField InlineLabel showCaseLabel;

    private Set<Widget> openTabs;

    @UiField TaskBar taskBar;
    @UiField FlowPanel codePanel;
    @UiField Label codeLabel;
    @UiField Image codeCloseButton;
    @UiField Code code;
    @UiField Label codeComponentName;

    private Image codeOpenButton;
    private String codeText;
    private String componentName;
    private Timer codeSampleTimer;

    /**
     * Таб, открывается сразу
     */
    private TreeItem firstTab;

    private static int dialogsCount = 1;

    interface ShowCasePanelUiBinder extends UiBinder<FlowPanel, ShowCasePanel> {
    }
    private static ShowCasePanelUiBinder binder = GWT.create(ShowCasePanelUiBinder.class);

    public ShowCasePanel() {
        if (Cookies.getCookie(THEME_COOKIE) == null) {
            currentTheme = Theme.STANDARD;
        } else {
            currentTheme = Theme.getTheme(Cookies.getCookie(THEME_COOKIE));
        }

        binder.createAndBindUi(this);

        titlePanel.getElement().setId("titlePanel");
        showCaseLabel.setStyleName(SynergyComponents.getResources().cssComponents().bigText());

        treeSetUp();
        setTreeIcons(ImageResources.IMPL.folder());

        final ContextMenu menu = new ContextMenu();
        setTreeItemHandler(tree, new TreeItemContextMenuEvent.Handler() {
            @Override
            public void onTreeContextMenu(TreeItemContextMenuEvent event) {
                event.getEvent().preventDefault();
                event.getEvent().stopPropagation();
                menu.clear();
                final TreeItem item = event.getItem();
                if (item.isSelected()) {
                    menu.add(new MenuItem<Command>(new Command() {
                        @Override
                        public void execute() {
                            item.setSelected(false);
                        }
                    }, Messages.i18n().tr("Снять выделение")));
                } else {
                    menu.add(new MenuItem<Command>(new Command() {
                        @Override
                        public void execute() {
                            item.setSelected(true);
                        }
                    }, Messages.i18n().tr("Выделить")));
                }

                if (item.hasItems()) {
                    menu.addSeparator(0);
                    if (item.isOpen()) {
                        menu.add(new MenuItem<Command>(new Command() {
                            @Override
                            public void execute() {
                                item.setOpen(false);
                            }
                        }, Messages.i18n().tr("Закрыть")));
                    } else {
                        menu.add(new MenuItem<Command>(new Command() {
                            @Override
                            public void execute() {
                                item.setOpen(true);
                            }
                        }, Messages.i18n().tr("Открыть")));
                    }
                }
                menu.show(event.getX(), event.getY());
            }
        });

        final Dictionary theme = Dictionary.getDictionary("properties");

        SimpleButton about = new SimpleButton("About");
        final DialogSimple aboutDialog = createAboutDialog();
        about.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                aboutDialog.center();
                aboutDialog.show();
            }
        });
        titlePanel.add(about);


        ComboBox<Theme> themesCombo = new ComboBox<Theme>();
        themesCombo.setReadOnly(true);
        for (Theme th : Theme.values()) {
            themesCombo.addItem(th.name().toLowerCase(), th);
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
            if (!DEFAULT_LOCALE.equals(locale)) {
                localesCombo.addItem(locale, locale);
            }
        }
        localesCombo.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                UrlBuilder newUrl = Window.Location.createUrlBuilder();
                newUrl.setParameter(LocaleInfo.getLocaleQueryParam(), event.getValue());
                Window.Location.assign(newUrl.buildString());
            }
        });
        String chosenLocale = LocaleInfo.getCurrentLocale().getLocaleName();
        localesCombo.selectValue(chosenLocale, false);

        titlePanel.add(localesCombo);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            RootPanel.getBodyElement().getStyle().setProperty(DIRECTION_PROPERTY, HasDirection.Direction.RTL.name());
        }

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            themesCombo.getElement().getStyle().setFloat(Style.Float.LEFT);
            localesCombo.getElement().getStyle().setFloat(Style.Float.LEFT);
            about.getElement().getStyle().setFloat(Style.Float.LEFT);
        } else {
            themesCombo.getElement().getStyle().setFloat(Style.Float.RIGHT);
            localesCombo.getElement().getStyle().setFloat(Style.Float.RIGHT);
            about.getElement().getStyle().setFloat(Style.Float.RIGHT);
        }

        Style tabsContentStyle = tabPanel.getContentPanel().getElement().getStyle();
        tabsContentStyle.setProperty("borderBottomStyle", "none");
        tabsContentStyle.setProperty("borderBottomLeftRadius", "0px");
        tabsContentStyle.setProperty("borderBottomRightRadius", "0px");

        codeLabel.addStyleName(SynergyComponents.getResources().cssComponents().mainText());
        codeComponentName.addStyleName(SynergyComponents.getResources().cssComponents().mainTextBold());

        add(titlePanel);
        add(tree);
        add(tabPanel);
        add(codePanel);
        add(taskBar);

        openTabs = new HashSet<Widget>();

        tree.addTreeSelectionEvent(new TreeSelectionEvent.Handler() {
            @Override
            public void onTreeSelection(TreeSelectionEvent event) {
                Object loadPanel = event.getTreeItem().getUserObject();
                if (loadPanel != null) {
                    ((LoadPanel) loadPanel).execute();
                }
            }
        });

        tabPanel.addTabCloseHandler(new TabCloseEvent.Handler() {
            @Override
            public void onTabClose(TabCloseEvent event) {
                openTabs.remove(event.getTab().getContent().asWidget());
            }
        });

        hideCode();
        codeCloseButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hideCode();
            }
        });
    }

    private DialogSimple createAboutDialog() {
        DialogSimple about = new DialogSimple(true);
        about.setText("About");

        final Dictionary properties = Dictionary.getDictionary("properties");

        FlowPanel root = new FlowPanel();
        root.getElement().getStyle().setPadding(10, Style.Unit.PX);

        Label version = new Label("Version: \t" +
                (properties.keySet().contains("version") ? properties.get("version") : "unknown"));
        Label revision = new Label("Revision: \t" +
                (properties.keySet().contains("revision") ? properties.get("revision") : "unknown"));
        Label stamp = new Label("Build stamp: \t" +
                (properties.keySet().contains("build_stamp") ? properties.get("build_stamp") : "unknown"));

        root.add(version);
        root.add(revision);
        root.add(stamp);

        for (int i = 0; i < root.getWidgetCount(); i++) {
            root.getWidget(i).setStyleName(SynergyComponents.getResources().cssComponents().mainText());
            root.getWidget(i).addStyleName(ShowCase.RESOURCES.css().aboutDialogItem());
        }

        about.setContent(root);
        about.getContent().getElement().getStyle().setHeight(110, Style.Unit.PX);
        about.getContent().getElement().getStyle().setWidth(300, Style.Unit.PX);

        return about;
    }

    private void hideCode() {
        codePanel.getElement().getStyle().setDisplay(Style.Display.NONE);
        tabPanel.getElement().getStyle().clearBottom();
    }

    private void showCode() {
        tabPanel.getElement().getStyle().setBottom(380, Style.Unit.PX);
        codePanel.getElement().getStyle().clearDisplay();
        code.updateScroll();
    }

    private void showCode(String componentName, String text) {
        code.setText(text);
        codeComponentName.setText(componentName);
        showCode();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                if (firstTab != null) {
                    firstTab.setSelected(true, true);
                    firstTab.getParent().setOpen(true, true);
                }
            }
        });
    }

    private void setTreeIcons(TreeItem item, ImageResource icon) {
        item.setIcon(icon);

        if (item.hasItems()) {
            for (TreeItem child : item.getItems()) {
                setTreeIcons(child, icon);
            }
        }
    }

    private void setTreeIcons(ImageResource icon) {
        for (TreeItem item : tree.getItems()) {
            setTreeIcons(item, icon);
        }
    }

    private void addCodeSample(final Widget widget, final String name, final String sampleCode) {
        if (codeOpenButton == null) {
            codeOpenButton = createCodeOpenButton();
            RootPanel.get().add(codeOpenButton);
        }

        if (codeSampleTimer == null) {
            codeSampleTimer = new Timer() {
                @Override
                public void run() {
                    codeOpenButton.getElement().getStyle().setDisplay(Style.Display.NONE);
                }
            };
        }

        widget.sinkEvents(Event.ONMOUSEOVER);
        widget.sinkEvents(Event.ONMOUSEOUT);

        widget.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                codeSampleTimer.cancel();
                componentName = name;
                codeText = sampleCode;
                Style widgetStyle = codeOpenButton.getElement().getStyle();
                widgetStyle.setTop(widget.getAbsoluteTop(), Style.Unit.PX);
                widgetStyle.setLeft(widget.getAbsoluteLeft() + widget.getOffsetWidth() + 3, Style.Unit.PX);
                widgetStyle.setDisplay(Style.Display.BLOCK);
            }
        }, MouseOverEvent.getType());

        widget.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                codeSampleTimer.cancel();
                codeSampleTimer.schedule(800);
            }
        }, MouseOutEvent.getType());

    }

    private Image createCodeOpenButton() {
        codeOpenButton = new Image();
        codeOpenButton.setResource(ShowCase.IMAGES.code());

        Style style = codeOpenButton.getElement().getStyle();
        style.setPosition(Style.Position.FIXED);
        style.setDisplay(Style.Display.NONE);
        style.setCursor(Style.Cursor.POINTER);
        style.setZIndex(1000);

        codeOpenButton.sinkEvents(Event.ONCLICK);
        codeOpenButton.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showCode(componentName, codeText);
            }
        }, ClickEvent.getType());

        return codeOpenButton;
    }

    private SimpleButton makeDialogButton(final DialogSimple dialog) {
        final SimpleButton button = new SimpleButton(dialog.getText());
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialog.center();
                dialog.show();
            }
        });
        dialog.addTaskBarHandler(new TaskBarEvent.AbstractHandler() {
            @Override
            public void onClose(TaskBarEvent event) {
                super.onClose(event);
                button.setEnabled(false);
            }
        });
        return button;
    }

    private void initDialog(final DialogSimple dialog, int width, int height, String title, boolean modal) {
        dialog.setText(title);

        if (!modal) {
            taskBar.addItem(dialog);
        }
        dialog.setModal(modal);

        FlowPanel content = new FlowPanel();
        content.getElement().getStyle().setWidth(width, Style.Unit.PX);
        content.getElement().getStyle().setHeight(height, Style.Unit.PX);
        dialog.setContent(content);
    }

    private void initButtonDialog(Dialog dialog, boolean hasLeft, boolean hasRight) {
        dialog.setLeftButtonVisible(hasLeft);
        dialog.setRightButtonVisible(hasRight);
    }

    private Dialog createDialog(String title,
                                      int width,
                                      int height,
                                      boolean modal,
                                      boolean backButton,
                                      boolean moreButton) {
        final Dialog dialog = new Dialog();
        initDialog(dialog, width, height, title, modal);

        if (backButton || moreButton) {
            Dialog withButtons = new Dialog();
            initButtonDialog(withButtons, backButton, moreButton);
        } else {
            throw new IllegalArgumentException();
        }

        return dialog;
    }

    private DialogSimple createDialog(String title, int width, int height, boolean modal) {
        DialogSimple dialog = new DialogSimple(modal);
        initDialog(dialog, width, height, title, modal);
        return dialog;
    }

    private void randomizePosition(PopupPanel popup) {
        int x = (int) (Math.random() * (Window.getClientWidth() - 400));
        int y = (int) (Math.random() * (Window.getClientHeight() - 400));
        popup.setPopupPosition(x, y);
    }

    private Panel createDialogsWithButtonsPanel() {
        final FlowPanel root = new FlowPanel();

        final SimpleButton showTiny = new SimpleButton(Messages.i18n().tr("Показать небольшой диалог"));
        showTiny.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        showTiny.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        showTiny.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Dialog dialog = new Dialog();
                initDialog(dialog, 116, 84, Messages.i18n().tr("Небольшой диалог"), true);
                initButtonDialog(dialog, false, true);
                dialog.center();
                dialog.show();
            }
        });
        root.add(showTiny);

        final SimpleButton showMedium = new SimpleButton(Messages.i18n().tr("Показать небольшой диалог"));
        showMedium.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        showMedium.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        showMedium.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Dialog dialog = new Dialog();
                initDialog(dialog, 400, 400, Messages.i18n().tr("Средний диалог"), true);
                initButtonDialog(dialog, true, true);
                dialog.center();
                dialog.show();
            }
        });
        root.add(showMedium);

        root.add(new Br());

        final TextInput input = new TextInput();
        input.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        input.setWidth("300px");
        input.setAllowEmpty(false);
        root.add(input);

        final SimpleButton newDialog = new SimpleButton(Messages.i18n().tr("Добавить новый диалог"));
        addCodeSample(newDialog, Messages.i18n().tr("Диалог с кнопками"), ShowCase.RESOURCES.dialogButtons().getText());
        setMarginLeft(newDialog, 20);
        newDialog.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (input.checkInput()) {
                    Dialog dialog = new Dialog();
                    randomizePosition(dialog);
                    initDialog(dialog, 400, 400, input.getText(), false);
                    initButtonDialog(dialog, true, true);
                    taskBar.addItem(dialog);
                    input.setValue("", false);
                }
            }
        });
        root.add(newDialog);

        return root;
    }

    private Panel createDialogsPanel() {
        FlowPanel root = new FlowPanel();

        final SimpleButton showTiny = new SimpleButton(Messages.i18n().tr("Показать небольшой диалог"));
        addCodeSample(showTiny, Messages.i18n().tr("Небольшой диалог"), ShowCase.RESOURCES.dialogModalSmall().getText());
        setMarginLeft(showTiny, 20);
        showTiny.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        showTiny.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogSimple dialog = new DialogSimple();
                initDialog(dialog, 116, 84, Messages.i18n().tr("Небольшой диалог"), true);
                dialog.center();
                dialog.show();
            }
        });
        root.add(showTiny);

        final SimpleButton showMedium = new SimpleButton(Messages.i18n().tr("Показать средний диалог"));
        addCodeSample(showMedium, Messages.i18n().tr("Средний диалог"), ShowCase.RESOURCES.dialogModalMedium().getText());
        showMedium.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        setMarginLeft(showMedium, 20);
        showMedium.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogSimple dialog = new DialogSimple();
                initDialog(dialog, 400, 400, Messages.i18n().tr("Средний диалог"), true);
                dialog.center();
                dialog.show();
            }
        });
        root.add(showMedium);

        SimpleButton newDialog = new SimpleButton(Messages.i18n().tr("Добавить новый диалог"));
        addCodeSample(newDialog, Messages.i18n().tr("Диалог"), ShowCase.RESOURCES.dialog().getText());
        newDialog.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        setMarginLeft(newDialog, 20);
        newDialog.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogSimple dialog = new DialogSimple();

                randomizePosition(dialog);
                initDialog(dialog, 400, 400, Messages.i18n().tr("Диалог #") + dialogsCount++, false);
                taskBar.addItem(dialog);
            }
        });
        root.add(newDialog);

        return root;
    }

    private void fillComboBox(ComboBox<String> comboBox) {
        comboBox.addItem(Messages.i18n().tr("Приблизить"), ImageResources.IMPL.zoom(), Messages.i18n().tr("Приблизить"));
        comboBox.addItem(Messages.i18n().tr("Налево"), ImageResources.IMPL.navigationLeft(), Messages.i18n().tr("Налево"));
        comboBox.addItem(Messages.i18n().tr("Направо"), ImageResources.IMPL.navigationRight(), Messages.i18n().tr("Направо"));
        comboBox.addItem(Messages.i18n().tr("Простооченьдлинныйпунктменю,чтобыпосмотретьчтопроисходит"),
                Messages.i18n().tr("Простооченьдлинныйпунктменю,чтобыпосмотретьчтопроисходит"));
        for (int i = 1; i < 30; i++) {
            comboBox.addItem(Messages.i18n().tr("Пункт меню ") + i, Messages.i18n().tr("Пункт меню ") + i);
        }
    }

    public Widget getComboBoxPanel() {
        FlowPanel comboBoxPanel = new FlowPanel();
        comboBoxPanel.getElement().getStyle().setPadding(10, Style.Unit.PX);

        final ComboBox<String> combo = new ComboBox<String>();
        addCodeSample(combo, Messages.i18n().tr("Комбобокс"), ShowCase.RESOURCES.combobox().getText());

        fillComboBox(combo);
        combo.setReadOnly(false);
        comboBoxPanel.add(combo);

        ComboBox<ComboState> statesCombo = new ComboBox<ComboState>();
        addCodeSample(statesCombo, Messages.i18n().tr("Комбобокс"), ShowCase.RESOURCES.combobox().getText());
        setMarginLeft(statesCombo, 20);
        comboBoxPanel.add(statesCombo);

        statesCombo.setReadOnly(true);

        statesCombo.addItem(Messages.i18n().tr("Включен, изменяем"), ComboState.ON);
        statesCombo.addItem(Messages.i18n().tr("Включен, неизменяем"), ComboState.ON_READONLY);
        statesCombo.addItem(Messages.i18n().tr("Выключен"), ComboState.OFF);

        statesCombo.selectValue(ComboState.ON, false);

        statesCombo.addValueChangeHandler(new ValueChangeHandler<ComboState>() {
            @Override
            public void onValueChange(ValueChangeEvent<ComboState> event) {
                switch (event.getValue()) {
                    case ON:
                        combo.setEnabled(true);
                        combo.setReadOnly(false);
                        break;
                    case ON_READONLY:
                        combo.setEnabled(true);
                        combo.setReadOnly(true);
                        break;
                    case OFF:
                        combo.setEnabled(false);
                        break;
                    default:
                }
            }
        });

        return comboBoxPanel;
    }

    public Widget getTabsPanel() {
        FlowPanel root = new FlowPanel();
        root.getElement().getStyle().setPadding(10, Style.Unit.PX);

        SimpleButton addTab = new SimpleButton(Messages.i18n().tr("Добавить вкладку"));
        root.add(addTab);

        SimpleButton addNonclosingTab = new SimpleButton(Messages.i18n().tr("Добавить незакрывающуюся вкладку"));
        addNonclosingTab.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        root.add(addNonclosingTab);

        final TabPanel localTabPanel = new TabPanel();
        addCodeSample(localTabPanel, Messages.i18n().tr("Панель вкладок"), ShowCase.RESOURCES.tabs().getText());
        localTabPanel.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        localTabPanel.setWidth("450px");
        localTabPanel.setHeight("300px");

        root.add(localTabPanel);

        addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка") + " 1");
        addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка") + " 2");
        addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка") + " 3");

        localTabPanel.selectTab(1);

        addTab.addClickHandler(new ClickHandler() {
            private int tabCount = 3;
            @Override
            public void onClick(ClickEvent event) {
                addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка") + " " + ++tabCount);
            }
        });

        addNonclosingTab.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSimpleTab(localTabPanel, Messages.i18n().tr("Вкладка"), false);
            }
        });

        return root;
    }

    private void addSimpleTab(TabPanel tabPanel, String text) {
        addSimpleTab(tabPanel, text, true);
    }

    private void addSimpleTab(TabPanel tabPanel, String text, boolean closable) {
        Label label = new Label(text);
        label.setStyleName(SynergyComponents.getResources().cssComponents().mainTextBold());
        SimplePanel panel = new SimplePanel(label);
        panel.getElement().getStyle().setPadding(10, Style.Unit.PX);

        tabPanel.addTab(text, panel, closable);
    }

    private abstract class LoadPanel implements Command {
        private Widget content;
        private String text;

        public LoadPanel(String text) {
            this.text = text;
        }

        @Override
        public void execute() {
            if (content == null) {
                content = getContentWidget();
            }
            if (!openTabs.contains(content)) {
                tabPanel.addTab(getText(), content);
                openTabs.add(content);
            }
            tabPanel.selectTab(content);
        }

        public String getText() {
            return text;
        }

        public abstract Widget getContentWidget();
    }

    private TreeItem addTreeItem(TreeItem parentItem, LoadPanel loadPanel) {
        TreeItem item = tree.addItem(parentItem, loadPanel.getText());
        item.setUserObject(loadPanel);
        return item;
    }

    private void treeSetUp() {
        tree.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        TreeItem basicComponents = tree.addItem(Messages.i18n().tr("Базовые компоненты"));

        TreeItem buttons = tree.addItem(basicComponents, Messages.i18n().tr("Кнопки"));

        addTreeItem(buttons, new LoadPanel(Messages.i18n().tr("Простая кнопка")) {
            @Override
            public Widget getContentWidget() {
                return getSimpleButtonPanel();
            }
        });
        addTreeItem(buttons, new LoadPanel(Messages.i18n().tr("Кнопка с иконкой")) {
            @Override
            public Widget getContentWidget() {
                return getIconButtonPanel();
            }
        });
        addTreeItem(buttons, new LoadPanel(Messages.i18n().tr("Цветные кнопки")) {
            @Override
            public Widget getContentWidget() {
                return getColorButtonPanel();
            }
        });
        addTreeItem(buttons, new LoadPanel(Messages.i18n().tr("Групповые кнопки")) {
            @Override
            public Widget getContentWidget() {
                return getGroupButton();
            }
        });

        TreeItem fields = tree.addItem(basicComponents, Messages.i18n().tr("Поля"));

        addTreeItem(fields, new LoadPanel(Messages.i18n().tr("Поле ввода текста")) {
            @Override
            public Widget getContentWidget() {
                return getTextInputs();
            }
        });
        addTreeItem(fields, new LoadPanel(Messages.i18n().tr("Поле с тегами")) {
            @Override
            public Widget getContentWidget() {
                return getTagInputs();
            }
        });
        addTreeItem(fields, new LoadPanel(Messages.i18n().tr("Комбобокс")) {
            @Override
            public Widget getContentWidget() {
                return getComboBoxPanel();
            }
        });
        addTreeItem(fields, new LoadPanel(Messages.i18n().tr("Компонент повторения")) {
            @Override
            public Widget getContentWidget() {
                return getPeriodInputs();
            }
        });

        addTreeItem(basicComponents, new LoadPanel(Messages.i18n().tr("Коллапсинг-панели")) {
            @Override
            public Widget getContentWidget() {
                return getCollapsingPanel();
            }
        });
        addTreeItem(basicComponents, new LoadPanel(Messages.i18n().tr("Вкладки")) {
            @Override
            public Widget getContentWidget() {
                return getTabsPanel();
            }
        });
        addTreeItem(basicComponents, new LoadPanel(Messages.i18n().tr("Чекбоксы и радиокнопки")) {
            @Override
            public Widget getContentWidget() {
                return getCheckBoxPanel();
            }
        });
        addTreeItem(basicComponents, new LoadPanel(Messages.i18n().tr("Дерево (черное)")) {
            @Override
            public Widget getContentWidget() {
                return getTreePanel();
            }
        });

        TreeItem table = tree.addItem(basicComponents, Messages.i18n().tr("Таблица"));
        addTreeItem(table, new LoadPanel(Messages.i18n().tr("Таблица - ряды")) {
            @Override
            public Widget getContentWidget() {
                return getTablePanel(true);
            }
        });
        addTreeItem(table, new LoadPanel(Messages.i18n().tr("Таблица - ячейки")) {
            @Override
            public Widget getContentWidget() {
                return getTablePanel(false);
            }
        });

        TreeItem treeTable = tree.addItem(basicComponents, Messages.i18n().tr("Дерево-таблица"));
        addTreeItem(treeTable, new LoadPanel(Messages.i18n().tr("Дерево-таблица - ряды")) {
            @Override
            public Widget getContentWidget() {
                return getTreeTable(true);
            }
        });
        addTreeItem(treeTable, new LoadPanel(Messages.i18n().tr("Дерево-таблица - ячейки")) {
            @Override
            public Widget getContentWidget() {
                return getTreeTable(false);
            }
        });

        addTreeItem(basicComponents, new LoadPanel(Messages.i18n().tr("Путь")) {
            @Override
            public Widget getContentWidget() {
                return getPathPanel();
            }
        });
        addTreeItem(basicComponents, new LoadPanel(Messages.i18n().tr("Индикаторы и слайдеры")) {
            @Override
            public Widget getContentWidget() {
                return getSlidersPanel();
            }
        });

        TreeItem complexComponents = tree.addItem(Messages.i18n().tr("Сложные компоненты"));
        addTreeItem(complexComponents, new LoadPanel(Messages.i18n().tr("Дата/время")) {
            @Override
            public Widget getContentWidget() {
                return getDateInputs();
            }
        });
        addTreeItem(complexComponents, new LoadPanel(Messages.i18n().tr("Панель комментариев")) {
            @Override
            public Widget getContentWidget() {
                return getCommentsPanel();
            }
        });
        addTreeItem(complexComponents, new LoadPanel(Messages.i18n().tr("Панель файлов")) {
            @Override
            public Widget getContentWidget() {
                return getFilesPanelPanel();
            }
        });
        addTreeItem(complexComponents, new LoadPanel(Messages.i18n().tr("Уведомления")) {
            @Override
            public Widget getContentWidget() {
                return getNotificationsPanel();
            }
        });


        TreeItem shell = tree.addItem(Messages.i18n().tr("Оболочка интерфейса"));
        addTreeItem(shell, new LoadPanel(Messages.i18n().tr("Диалог без кнопок")) {
            @Override
            public Widget getContentWidget() {
                return createDialogsPanel();
            }
        });
        addTreeItem(shell, new LoadPanel(Messages.i18n().tr("Диалог с кнопками")) {
            @Override
            public Widget getContentWidget() {
                return createDialogsWithButtonsPanel();
            }
        });
    }

    private void setTreeItemHandler(TreeItem item, TreeItemContextMenuEvent.Handler handler) {
        item.addTreeContextMenuHandler(handler);
        if (!item.hasItems()) {
            return;
        }
        for (TreeItem child : item.getItems()) {
            setTreeItemHandler(child, handler);
        }
    }

    private void setTreeItemHandler(Tree tree, TreeItemContextMenuEvent.Handler handler) {
        for (TreeItem item : tree.getItems()) {
            setTreeItemHandler(item, handler);
        }
    }
    private void copyTreeItem(Tree tree, TreeItem src, TreeItem dst) {
        if (!src.hasItems()) {
            return;
        }
        for (TreeItem item : src.getItems()) {
            TreeItem newItem = tree.addItem(dst, item.getText());
            copyTreeItem(tree, item, newItem);
        }
    }

    private Tree copyTree(Tree tree, boolean withScroll, boolean white) {
        Tree newTree = new Tree(withScroll, white);
        for (TreeItem item : tree.getItems()) {
            TreeItem newItem = newTree.addItem(item.getText());
            copyTreeItem(newTree, item, newItem);
        }
        return newTree;
    }

    private TreeItem getFirstDeepest(Tree tree) {
        TreeItem item = tree.getItems().get(0);
        while (item.hasItems()) {
            item = item.getItems().get(0);
        }
        return item;
    }

    private Widget getTreePanel() {
        FlowPanel root = new FlowPanel();

        kz.arta.synergy.components.client.tree.Tree localTree = copyTree(tree, true, false);
        addCodeSample(localTree, Messages.i18n().tr("Дерево"), ShowCase.RESOURCES.tree().getText());

        localTree.getElement().getStyle().setHeight(LOCAL_TREE_SIZE, Style.Unit.PX);
        localTree.getElement().getStyle().setWidth(LOCAL_TREE_SIZE, Style.Unit.PX);

        localTree.getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        localTree.getElement().getStyle().setMarginTop(20, Style.Unit.PX);

        setTreeIcons(localTree.getItems().get(0).getItems().get(0), ImageResources.IMPL.favouriteFolder());
        setTreeIcons(localTree.getItems().get(0).getItems().get(1), ImageResources.IMPL.project());
        setTreeIcons(localTree.getItems().get(1), ImageResources.IMPL.portfolio());
        setTreeIcons(localTree.getItems().get(2), ImageResources.IMPL.portfolio());

        TreeItem deepestItem = getFirstDeepest(localTree);
        localTree.addItem(getFirstDeepest(localTree), deepestItem.getText() + " " + deepestItem.getText()).setIcon(ImageResources.IMPL.favouriteFolder());
        deepestItem = getFirstDeepest(localTree);
        localTree.addItem(getFirstDeepest(localTree), deepestItem.getText() + " " + deepestItem.getText()).setIcon(ImageResources.IMPL.favouriteFolder());
        deepestItem = getFirstDeepest(localTree);
        localTree.addItem(getFirstDeepest(localTree), deepestItem.getText() + " " + deepestItem.getText()).setIcon(ImageResources.IMPL.favouriteFolder());

        root.add(localTree);

        return root;
    }

    private Widget getTablePanel(final boolean onlyRows) {
        FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setPadding(20, Style.Unit.PX);
        panel.getElement().getStyle().setPaddingLeft(40, Style.Unit.PX);
        panel.getElement().getStyle().setPaddingRight(40, Style.Unit.PX);

        final Table<User> table = new Table<User>(29);
        if (onlyRows) {
            addCodeSample(table, Messages.i18n().tr("Таблица с выбором рядом"), ShowCase.RESOURCES.tableRows().getText());
        } else {
            addCodeSample(table, Messages.i18n().tr("Таблица с выбором ячеек"), ShowCase.RESOURCES.tableCells().getText());
        }
        table.enableHat(true);
        table.getHat().setName(Messages.i18n().tr("Таблица"));
        table.getHat().enableAddButton(true);
        table.getHat().enablePagerAlways(true);
        table.getHat().enablePager(true);
        table.setMultiLine(!onlyRows);

        table.getCore().setOnlyRows(onlyRows);
        table.setHeight(500 + "px");

        final ArtaTextColumn<User> idColumn = new ArtaTextColumn<User>("#") {
            @Override
            public String getValue(User object) {
                return "" + object.getKey();
            }

            @Override
            public int getMinWidth() {
                return 30;
            }
        };
        idColumn.setSortable(true);
        table.addColumn(idColumn);
        table.setColumnWidth(idColumn, 50);

        final IconColumn<User> iconColumn = new IconColumn<User>("") {
            @Override
            public ImageResource getImage(User object) {
                if (object.getKey() % 2 == 0) {
                    return ImageResources.IMPL.favouriteFolder();
                } else {
                    return null;
                }
            }
        };
        table.addColumn(iconColumn);
        table.setColumnWidth(iconColumn, 26);
        iconColumn.setResizeLock(true);

        final CheckBoxColumn<User> boxColumn = new CheckBoxColumn<User>("") {
            @Override
            public boolean getValue(User object) {
                return object.isAlive();
            }

            @Override
            public void setValue(User object, boolean newValue) {
                object.setAlive(newValue);
                System.out.println();
            }

            @Override
            public String getBackgroundColor(User object) {
                return object.getLifeLived() < 0.6 ? Colors.progressNormal.hex() : Colors.declineButtonBG2.hex();
            }
        };
        table.addColumn(boxColumn);
        table.setColumnWidth(boxColumn, CheckBoxColumn.WIDTH);
        boxColumn.setResizeLock(true);

        final ProgressColumn<User> progressColumn = new ProgressColumn<User>("Прогресс") {
            @Override
            public double getValue(User object) {
                return object.getLifeLived();
            }

            @Override
            public ProgressBar.Type getType(User object) {
                return object.getLifeLived() < 0.6 ? ProgressBar.Type.NEGATIVE : ProgressBar.Type.NEGATIVE;
            }

            @Override
            public double getOptionalValue(User object) {
                return 0.6;
            }
        };
        table.addColumn(progressColumn);
        table.setColumnWidth(progressColumn, ProgressColumn.WIDTH + 20);
        progressColumn.setResizeLock(true);

        final ArtaEditableTextColumn<User> firstNameColumn = new ArtaEditableTextColumn<User>(Messages.i18n().tr("Имя")) {
            @Override
            public String getValue(User value) {
                return value.getFirstName();
            }

            @Override
            public void setValue(User value, String text) {
                value.setFirstName(text);
            }

            @Override
            public String getTextColor(User object) {
                return object.getLifeLived() < 0.6 ? super.getTextColor(object) : Colors.declineButtonBG2.hex();
            }
        };
        firstNameColumn.setSortable(true);
        table.addColumn(firstNameColumn);

        final ArtaEditableTextColumn<User> lastNameColumn = new ArtaEditableTextColumn<User>(Messages.i18n().tr("Фамилия")) {
            @Override
            public String getValue(User value) {
                return value.getLastName();
            }

            @Override
            public void setValue(User value, String text) {
                value.setLastName(text);
            }

            @Override
            public String getTextColor(User object) {
                return object.getLifeLived() < 0.6 ? super.getTextColor(object) : Colors.declineButtonBG2.hex();
            }
        };
        lastNameColumn.setSortable(true);
        table.addColumn(lastNameColumn);

        ArtaEditableTextColumn<User> addressColumn = new ArtaEditableTextColumn<User>(Messages.i18n().tr("Почтовый индекс")) {
            @Override
            public String getValue(User value) {
                return value.getAddress();
            }

            @Override
            public void setValue(User value, String text) {
                value.setAddress(text);
            }

            @Override
            public String getTextColor(User object) {
                return object.getLifeLived() < 0.6 ? super.getTextColor(object) : Colors.declineButtonBG2.hex();
            }
        };
        addressColumn.setSortable(true);
        table.addColumn(addressColumn);

        for (int i = 4; i < table.getCore().getColumns().size(); i++) {
            table.setColumnWidth(i, 400);
        }
        table.setWidth("1000px");

        final ListDataProvider<User> provider = new ListDataProvider<User>();
        provider.addDataDisplay(table.getCore());

        final List<User> list = provider.getList();
        for (int i = 0; i < 190; i++) {
            list.add(new User("jon" + i, "jones" + i, "" + (85281 + i), i % 4 != 0, Random.nextDouble()));
        }
        provider.flush();

        TableSortEvent.ListHandler<User> listHandler = new TableSortEvent.ListHandler<User>(list);
        listHandler.setComparator(idColumn, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user1.getKey() > user2.getKey() ? 1 : user1.getKey() < user2.getKey() ? -1 : 0;
            }
        });
        listHandler.setComparator(firstNameColumn, new Comparator<User>() {
            @Override
            public int compare(User user, User user2) {
                //noinspection NonJREEmulationClassesInClientCode
                return user.getFirstName().compareTo(user2.getFirstName());
            }
        });
        listHandler.setComparator(lastNameColumn, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                //noinspection NonJREEmulationClassesInClientCode
                return user1.getLastName().compareTo(user2.getLastName());
            }
        });
        listHandler.setComparator(addressColumn, new Comparator<User>() {
            @Override
            public int compare(User user, User user2) {
                //noinspection NonJREEmulationClassesInClientCode
                return user.getAddress().compareTo(user2.getAddress());
            }
        });
        table.getCore().addSortHandler(listHandler);

        final Pager simplePager = new Pager(false);
        simplePager.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        simplePager.setDisplay(table.getCore());

        FlowPanel firstRow = new FlowPanel();
        firstRow.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        firstRow.getElement().getStyle().setPosition(Style.Position.RELATIVE);

        ArtaCheckBox checkBox = new ArtaCheckBox();
        checkBox.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        checkBox.getElement().getStyle().setTop(8, Style.Unit.PX);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            checkBox.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        } else {
            checkBox.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        }
        checkBox.setValue(true, false);
        checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                simplePager.setEnabled(event.getValue());
            }
        });

        firstRow.add(simplePager);
        firstRow.add(checkBox);

        panel.add(firstRow);
        panel.add(table);

        ArtaScrollPanel scroll = new ArtaScrollPanel(panel);
        scroll.setHeight("100%");

        final ContextMenu tableMenu = new ContextMenu();
        tableMenu.add(new MenuItem<Command>(new Command() {
            @Override
            public void execute() {
                Set<User> selected = table.getCore().getSelectionModel().getSelectedObjects();
                list.removeAll(selected);
                provider.flush();
            }
        }, Messages.i18n().tr("Удалить")));
        table.getCore().addContextMenuHandler(new TableMenuEvent.Handler() {
            @Override
            public void onTableMenu(TableMenuEvent event) {
                if (onlyRows) {
                    tableMenu.show(event.getX(), event.getY());
                }
            }
        });

        final ContextMenu headerMenu = new ContextMenu();
        table.addHeaderMenuHandler(new TableHeaderMenuEvent.Handler<User>() {
            @Override
            public void onTableHeaderMenu(final TableHeaderMenuEvent<User> event) {
                headerMenu.clear();
                headerMenu.add(new MenuItem<Command>(new Command() {
                    @Override
                    public void execute() {
                        table.getCore().sort(event.getColumn());
                    }
                }, Messages.i18n().tr("Отсортировать")));
                headerMenu.show(event.getX(), event.getY());
            }
        });

        return scroll;
    }

    private Widget getPathPanel() {
        final FlowPanel root = new FlowPanel();
        final Tree localTree = copyTree(tree, false, true);
        addCodeSample(localTree, Messages.i18n().tr("Дерево"), ShowCase.RESOURCES.tree().getText());

        localTree.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);

        /*дерево с избранным*/
        final FavoriteTree favoriteTree = new FavoriteTree(localTree);
        favoriteTree.getElement().getStyle().setWidth(LOCAL_TREE_SIZE, Style.Unit.PX);

        final FlowPanel panel = new FlowPanel();
        panel.add(favoriteTree);
        panel.add(localTree);

        ArtaScrollPanel scrollPanel = new ArtaScrollPanel(panel);
        scrollPanel.setHeight(LOCAL_TREE_SIZE + "px");
        scrollPanel.setWidth(LOCAL_TREE_SIZE + "px");
        scrollPanel.getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        scrollPanel.addStyleName(SynergyComponents.getResources().cssComponents().tree());

        for (TreeItem item : localTree.getItems()) {
            setTreeIcons(item, ImageResources.IMPL.folder());
        }

        TreeItem deepestItem = getFirstDeepest(localTree);
        localTree.addItem(getFirstDeepest(localTree), deepestItem.getText() + " " + deepestItem.getText()).setIcon(ImageResources.IMPL.folder());
        deepestItem = getFirstDeepest(localTree);
        localTree.addItem(getFirstDeepest(localTree), deepestItem.getText() + " " + deepestItem.getText()).setIcon(ImageResources.IMPL.folder());
        deepestItem = getFirstDeepest(localTree);
        localTree.addItem(getFirstDeepest(localTree), deepestItem.getText() + " " + deepestItem.getText()).setIcon(ImageResources.IMPL.folder());

        final Path path = new Path();
        path.getElement().getStyle().setWidth(80, Style.Unit.PCT);
        path.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        path.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        path.getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        path.getElement().getStyle().setMarginBottom(20, Style.Unit.PX);

        path.addButtonClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final TreeItem selectedItem = localTree.getSelectedItem();
                if (selectedItem != null && !selectedItem.isFavorite()) {
                    selectedItem.setFavorite(true);
                    TreeItem favorite = favoriteTree.addItem(favoriteTree.getRootItem(), selectedItem.getText());
                    favorite.setIcon(ImageResources.IMPL.folder());
                    favorite.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            selectedItem.setSelected(true);
                        }
                    });
                    if (!favoriteTree.getRootItem().isOpen()) {
                        favoriteTree.getRootItem().setOpen(true);
                    }
                }
            }
        });

        localTree.addTreeSelectionEvent(new TreeSelectionEvent.Handler() {
            @Override
            public void onTreeSelection(TreeSelectionEvent event) {
                TreeItem item = event.getTreeItem();
                List<PathItem> pathItems = new ArrayList<PathItem>();

                do {
                    ImageResource icon = item.getIcon();
                    PathItem pathItem = new PathItem(item.getText(), icon);
                    final TreeItem finalItem = item;
                    pathItem.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            finalItem.setSelected(true, true);
                        }
                    });
                    pathItems.add(0, pathItem);
                    item = item.getParent();
                } while (item != null);
                path.setPath(pathItems);
            }
        });
        root.add(path);
        root.add(scrollPanel);

        return root;
    }

    private Widget getTreeTable(final boolean onlyRows) {
        FlowPanel root = new FlowPanel();

        final Table<UserTree> table = new Table<UserTree>(10, new ProvidesKey<UserTree>() {
            @Override
            public Object getKey(UserTree item) {
                return item.getKey();
            }
        });
        table.getCore().setMultiSelectionAllowed(true);
        addCodeSample(table, Messages.i18n().tr("Дерево-таблица"), ShowCase.RESOURCES.treetable().getText());
        Style tableStyle = table.getElement().getStyle();
        tableStyle.setPosition(Style.Position.ABSOLUTE);
        tableStyle.setBottom(20, Style.Unit.PX);
        tableStyle.setTop(20, Style.Unit.PX);
        tableStyle.setLeft(40, Style.Unit.PX);
        tableStyle.setRight(40, Style.Unit.PX);

        table.getCore().addRowCountChangeHandler(new RowCountChangeEvent.Handler() {
            @Override
            public void onRowCountChange(RowCountChangeEvent event) {
                table.getCore().setPageSize(event.getNewRowCount());
            }
        });

        table.getCore().setOnlyRows(onlyRows);
        table.enableHat(true);
        table.getHat().setName(Messages.i18n().tr("Дерево-таблица"));
        table.getHat().enableAddButton(true);

        ArtaTextColumn<UserTree> idColumn = new ArtaTextColumn<UserTree>("id") {
            @Override
            public String getValue(UserTree object) {
                return "" + object.getKey();
            }
        };
        table.addColumn(idColumn);

        TreeColumn<UserTree> treeColumn = new TreeColumn<UserTree>("Имя человека", !onlyRows) {
            @Override
            public String getValue(UserTree object) {
                return object.getFirstName();
            }

            @Override
            public void setValue(UserTree object, String value) {
                object.setFirstName(value);
            }
        };
        table.addColumn(treeColumn);

        AbstractArtaColumn<UserTree> lastNameColumn;
        if (!onlyRows) {
            lastNameColumn = new ArtaEditableTextColumn<UserTree>(Messages.i18n().tr("Фамилия")) {
                @Override
                public String getValue(UserTree object) {
                    return object.getLastName();
                }

                @Override
                public void setValue(UserTree object, String value) {
                    object.setLastName(value);
                }
            };
            table.addColumn(lastNameColumn);
        } else {
            lastNameColumn = new ArtaTextColumn<UserTree>(Messages.i18n().tr("Фамилия")) {
                @Override
                public String getValue(UserTree object) {
                    return object.getLastName();
                }
            };

        }
        table.addColumn(lastNameColumn);

        AbstractArtaColumn<UserTree> addressColumn;
        if (!onlyRows) {
            addressColumn = new ArtaEditableTextColumn<UserTree>(Messages.i18n().tr("Адрес")) {
                @Override
                public String getValue(UserTree object) {
                    return object.getAddress();
                }

                @Override
                public void setValue(UserTree object, String value) {
                    object.setAddress(value);
                }
            };
        } else {
            addressColumn = new ArtaTextColumn<UserTree>(Messages.i18n().tr("Адрес")) {
                @Override
                public String getValue(UserTree object) {
                    return object.getAddress();
                }
            };
        }
        table.addColumn(addressColumn);

        table.getCore().setColumnWidth(idColumn, 65);
        table.getCore().setColumnWidth(lastNameColumn, 150);
        table.getCore().setColumnWidth(addressColumn, 150);

        final TreeTableProvider<UserTree> provider = new TreeTableProvider<UserTree>();
        provider.addDataDisplay(table.getCore());

        for (int i = 0; i < 15; i++) {
            String name = "John";
            String lastName = "Jones";
            String address = "Orinbor";

            UserTree user = new UserTree(null, name + i, lastName + i, address + " " + (i + 1));
            UserTree user2 = new UserTree(user, name + (i + 1), lastName + (i + 1), address + " " + (i + 2));
            new UserTree(user2, name + (i + 2), lastName + (i + 2), address + " " + (i + 3));
            new UserTree(user2, name + (i + 3), lastName + (i + 3), address + " " + (i + 4));
            provider.addItem(user);
        }
        provider.flush();

        final ContextMenu menu = new ContextMenu();
        final MenuItem<Command> deleteItem = new MenuItem<Command>(new Command() {
            @Override
            public void execute() {
                if (onlyRows) {
                    for (UserTree userTree : table.getCore().getSelectionModel().getSelectedObjects()) {
                        userTree.removeFromParent();
                        provider.getList().remove(userTree);
                    }
                    provider.flush();
                }
                table.getCore().getSelectionModel().clear();
            }
        }, Messages.i18n().tr("Удалить"));

        final MenuItem<Command> openItems = new MenuItem<Command>(new Command() {
            @Override
            public void execute() {
                for (TreeTableItem user : table.getCore().getSelectionModel().getSelectedObjects()) {
                    if (user.hasChildren()) {
                        user.open();
                    }
                }
                table.getCore().getSelectionModel().clear();
            }
        }, Messages.i18n().tr("Открыть"));

        final MenuItem<Command> closeItems = new MenuItem<Command>(new Command() {
            @Override
            public void execute() {
                for (UserTree user : table.getCore().getSelectionModel().getSelectedObjects()) {
                    if (user.hasChildren()) {
                        user.close();
                    }
                }
                table.getCore().getSelectionModel().clear();
            }
        }, Messages.i18n().tr("Закрыть"));

        menu.add(deleteItem);
        menu.add(openItems);
        menu.add(closeItems);


        table.getCore().addContextMenuHandler(new TableMenuEvent.Handler() {
            @Override
            public void onTableMenu(TableMenuEvent event) {
                if (onlyRows) {
                    menu.show(event.getX(), event.getY());
                }
            }
        });

        final ContextMenu headerMenu = new ContextMenu();
        table.addHeaderMenuHandler(new TableHeaderMenuEvent.Handler<UserTree>() {
            @Override
            public void onTableHeaderMenu(final TableHeaderMenuEvent<UserTree> event) {
                final String headerText = event.getColumn().getHeader().getText();
                headerMenu.clear();
                headerMenu.add(new MenuItem<Command>(new Command() {
                    @Override
                    public void execute() {
                        if (!headerText.contains(Messages.i18n().tr("Изменили"))) {
                            event.getColumn().getHeader().setText(headerText + " " + Messages.i18n().tr("Изменили"));
                        } else {
                            event.getColumn().getHeader().setText(event.getColumn().getHeader().getText().replace(Messages.i18n().tr("Изменили"), ""));
                        }
                    }
                }, !headerText.contains(Messages.i18n().tr("Изменили")) ? Messages.i18n().tr("Изменить текст столбца") : Messages.i18n().tr("Вернуть текст столбца")));
                headerMenu.show(event.getX(), event.getY());
            }
        });

        root.add(table);

        return root;
    }

    /**
     * Панель с компонентами дата/время
     */
    private Widget getDateInputs() {
        FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setPadding(10, Style.Unit.PX);

        int width = 300;

        PPanel firstRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel firstRowPanel = new FlowPanel();
        firstRow.setWidget(firstRowPanel);
        firstRowPanel.add(createLabel(Messages.i18n().tr("Поле время: "), width));
        TimeInput timeInputNotAllowEmpty = new TimeInput();
        timeInputNotAllowEmpty.setTitle(Messages.i18n().tr("Обязательное поле ввода"));
        firstRowPanel.add(timeInputNotAllowEmpty);
        TimeInput timeInputAllowEmpty = new TimeInput(true);
        timeInputAllowEmpty.setTitle(Messages.i18n().tr("Необязательное поле ввода"));
        timeInputAllowEmpty.setTime(12, 12);
        firstRowPanel.add(timeInputAllowEmpty);
        TimeInput timeInput = new TimeInput();
        timeInput.setTitle(Messages.i18n().tr("Неактивное поле ввода"));
        timeInput.setEnabled(false);
        firstRowPanel.add(timeInput);
        panel.add(firstRow);
        firstRow.getElement().getStyle().setPadding(5, Style.Unit.PX);
        for (int i = 1; i < firstRowPanel.getWidgetCount(); i++) {
            Widget widget = firstRowPanel.getWidget(i);
            setMarginLeft(widget, 20);
            addCodeSample(widget, Messages.i18n().tr("Поле ввода времени"), ShowCase.RESOURCES.timeInput().getText());
        }

        PPanel secondRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel secondRowPanel = new FlowPanel();
        secondRow.setWidget(secondRowPanel);
        secondRowPanel.add(createLabel(Messages.i18n().tr("Поле дата - режим выбора день: "), width));
        DateInput dateInput= new DateInput();
        dateInput.setTitle(Messages.i18n().tr("Обязательное поле ввода"));
        secondRowPanel.add(dateInput);
        DateInput dateInput1 = new DateInput(true);
        dateInput1.setTitle(Messages.i18n().tr("Необязательное поле ввода"));
        secondRowPanel.add(dateInput1);
        dateInput1.setDate(new Date());

        DateInput dateInput2 = new DateInput();
        dateInput2.setTitle(Messages.i18n().tr("Неактивное поле ввода"));
        dateInput2.setEnabled(false);
        secondRowPanel.add(dateInput2);
        panel.add(secondRow);
        secondRow.getElement().getStyle().setPadding(5, Style.Unit.PX);
        for (int i = 1; i < secondRowPanel.getWidgetCount(); i++) {
            Widget widget = secondRowPanel.getWidget(i);
            setMarginLeft(widget, 20);
            addCodeSample(widget, Messages.i18n().tr("Поле ввода даты"), ShowCase.RESOURCES.dateInput().getText());
        }

        PPanel thirdRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel thirdRowPanel = new FlowPanel();
        thirdRow.setWidget(thirdRowPanel);
        thirdRowPanel.add(createLabel(Messages.i18n().tr("Поле дата - режим выбора неделя и месяц: "), width));
        DateInput dateInputWeek = new DateInput(ArtaDatePicker.CalendarMode.WEEK);
        addCodeSample(dateInputWeek, Messages.i18n().tr("Поле ввода даты (режим выбора - неделя)"), ShowCase.RESOURCES.dateInputWeek().getText());
        dateInputWeek.setTitle(Messages.i18n().tr("Неделя"));
        thirdRowPanel.add(dateInputWeek);
        DateInput dateInputMonth = new DateInput(ArtaDatePicker.CalendarMode.MONTH);
        addCodeSample(dateInputMonth, Messages.i18n().tr("Поле ввода даты (режим выбора - месяц)"), ShowCase.RESOURCES.dateInputMonth().getText());
        dateInputMonth.setTitle(Messages.i18n().tr("Месяц"));
        thirdRowPanel.add(dateInputMonth);
        DateInput dateInputMonthDisabled = new DateInput(ArtaDatePicker.CalendarMode.MONTH);
        dateInputMonthDisabled.setEnabled(false);
        dateInputMonthDisabled.setTitle(Messages.i18n().tr("Неактивное поле ввода"));
        thirdRowPanel.add(dateInputMonthDisabled);
        panel.add(thirdRow);
        thirdRow.getElement().getStyle().setPadding(5, Style.Unit.PX);
        for (int i = 1; i < thirdRowPanel.getWidgetCount(); i++) {
            thirdRowPanel.getWidget(i).getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        }

        PPanel fourthRow = new PPanel(Constants.BUTTON_HEIGHT + Constants.BORDER_WIDTH);
        FlowPanel fourthRowPanel = new FlowPanel();
        fourthRow.setWidget(fourthRowPanel);
        fourthRowPanel.add(createLabel(Messages.i18n().tr("Поле ввода даты/времени: "), width));
        DateTimeInput dateTimeInput = new DateTimeInput();
        addCodeSample(dateTimeInput, Messages.i18n().tr("Поле ввода даты-время"), ShowCase.RESOURCES.dateTimeInput().getText());
        dateTimeInput.setTitle(Messages.i18n().tr("Обязательное поле ввода"));
        fourthRowPanel.add(dateTimeInput);

        DateTimeInput dateTimeInput1 = new DateTimeInput(true);
        addCodeSample(dateTimeInput1, Messages.i18n().tr("Поле ввода даты-время"), ShowCase.RESOURCES.dateTimeInput().getText());
        dateTimeInput1.setTitle(Messages.i18n().tr("Необязательное поле ввода"));
        fourthRowPanel.add(dateTimeInput1);

        DateTimeInput dateTimeInput2 = new DateTimeInput(true);
        dateTimeInput2.setTitle(Messages.i18n().tr("Неактивное поле ввода"));
        dateTimeInput2.setEnabled(false);
        fourthRowPanel.add(dateTimeInput2);
        panel.add(fourthRow);
        fourthRow.getElement().getStyle().setPadding(5, Style.Unit.PX);
        for (int i = 1; i < fourthRowPanel.getWidgetCount(); i++) {
            setMarginLeft(fourthRowPanel.getWidget(i), 20);
        }

        FlowPanel datePickerPanel = new FlowPanel();
        ArtaDatePicker pickerDay = new ArtaDatePicker();
        addCodeSample(pickerDay, Messages.i18n().tr("Календарь (режим выбора - день)"), ShowCase.RESOURCES.datePickerDay().getText());
        datePickerPanel.add(pickerDay);
        ArtaDatePicker pickerDayDark = new ArtaDatePicker(ColorType.BLACK);
        addCodeSample(pickerDayDark, Messages.i18n().tr("Календарь (режим выбора - день)"), ShowCase.RESOURCES.datePickerDay().getText());
        datePickerPanel.add(pickerDayDark);
        setMarginLeft(datePickerPanel.getWidget(0), 20);
        setMarginLeft(datePickerPanel.getWidget(1), 20);
        panel.add(datePickerPanel);
        datePickerPanel.getElement().getStyle().setPadding(5, Style.Unit.PX);

        FlowPanel datePickerPanel1 = new FlowPanel();
        ArtaDatePicker pickerWeek = new ArtaDatePicker(ArtaDatePicker.CalendarMode.WEEK);
        addCodeSample(pickerWeek, Messages.i18n().tr("Календарь (режим выбора - неделя)"), ShowCase.RESOURCES.datePickerWeek().getText());
        datePickerPanel1.add(pickerWeek);
        ArtaDatePicker pickerWeekDark = new ArtaDatePicker(ArtaDatePicker.CalendarMode.WEEK, ColorType.BLACK);
        addCodeSample(pickerWeekDark, Messages.i18n().tr("Календарь (режим выбора - неделя)"), ShowCase.RESOURCES.datePickerWeek().getText());
        datePickerPanel1.add(pickerWeekDark);
        setMarginLeft(datePickerPanel1.getWidget(0), 20);
        setMarginLeft(datePickerPanel1.getWidget(1), 20);
        panel.add(datePickerPanel1);
        datePickerPanel1.getElement().getStyle().setPadding(5, Style.Unit.PX);

        FlowPanel datePickerPanel2 = new FlowPanel();
        ArtaDatePicker pickerMonth = new ArtaDatePicker(ArtaDatePicker.CalendarMode.MONTH);
        addCodeSample(pickerMonth, Messages.i18n().tr("Календарь (режим выбора - месяц)"), ShowCase.RESOURCES.datePickerMonth().getText());
        datePickerPanel2.add(pickerMonth);
        ArtaDatePicker pickerMonthDark = new ArtaDatePicker(ArtaDatePicker.CalendarMode.MONTH, ColorType.BLACK);
        addCodeSample(pickerMonthDark, Messages.i18n().tr("Календарь (режим выбора - месяц)"), ShowCase.RESOURCES.datePickerMonth().getText());
        datePickerPanel2.add(pickerMonthDark);
        setMarginLeft(datePickerPanel2.getWidget(0), 20);
        setMarginLeft(datePickerPanel2.getWidget(1), 20);
        panel.add(datePickerPanel2);
        datePickerPanel2.getElement().getStyle().setPadding(5, Style.Unit.PX);

        FlowPanel dateTest = new FlowPanel();
        dateTest.add(new DatePicker());
        panel.add(dateTest);
        dateTest.getElement().getStyle().setPadding(5, Style.Unit.PX);

        ArtaScrollPanel scroll = new ArtaScrollPanel(panel);
        scroll.setHeight("100%");

        return scroll;
    }

    private Widget makeLineForCollapsingPanel(String text, int textWidth, int inputWidth, boolean dateInput) {

        FlowPanel row = new FlowPanel();
        StyleUtils.setLineHeight(row.getElement(), 32, Style.Unit.PX);
        row.setStyleName(SynergyComponents.getResources().cssComponents().mainText());
        row.setWidth(textWidth + inputWidth + 5 + "px");

        InlineLabel label = new InlineLabel(text);
        label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        label.setWidth(textWidth + "px");

        row.add(label);


        if (!dateInput) {
            TextInput input = new TextInput();
            input.setWidth(inputWidth - Constants.COMMON_INPUT_PADDING * 2 - Constants.BORDER_WIDTH * 2 + "px");
            row.add(input);
        } else {
            DateInput input = new DateInput();
            input.setWidth(inputWidth - Constants.BORDER_WIDTH * 2 + "px");
            row.add(input);
        }

        row.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        return row;
    }

    private Widget getCollapsingPanel() {
        FlowPanel root = new FlowPanel();
        root.getElement().getStyle().setPadding(10, Style.Unit.PX);

        FlowPanel collapsingPanels = new FlowPanel();

        final CollapsingPanel meta = new CollapsingPanel(Messages.i18n().tr("Метаданные"));
        meta.addButton(Messages.i18n().tr("Кнопка"));
        addCodeSample(meta, Messages.i18n().tr("Коллапсинг-панель"), ShowCase.RESOURCES.collapsingPanel().getText());
        meta.setWidth("500px");
        meta.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        meta.getPanel().getElement().getStyle().setPadding(18, Style.Unit.PX);

        meta.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Дата публикации"), 200, 264, true));
        meta.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Название"), 200, 264, false));
        meta.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Создатель"), 200, 264, false));
        meta.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Тема"), 200, 264, false));
        meta.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Описание"), 200, 264, false));

        collapsingPanels.add(meta);

        final CollapsingPanel classifier = new CollapsingPanel(Messages.i18n().tr("Классификатор"));
        classifier.addButton(Messages.i18n().tr("Кнопка"));
        classifier.showButton();
        classifier.setWidth("500px");
        classifier.getPanel().getElement().getStyle().setPadding(18, Style.Unit.PX);
        classifier.addButtonClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                classifier.hideButton();
                meta.showButton();
            }
        });
        meta.addButtonClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                classifier.showButton();
                meta.hideButton();
            }
        });

        classifier.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Дата публикации"), 200, 264, true));
        classifier.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Название"), 200, 264, false));
        classifier.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Создатель"), 200, 264, false));
        classifier.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Тема"), 200, 264, false));
        classifier.getPanel().add(makeLineForCollapsingPanel(Messages.i18n().tr("Описание"), 200, 264, false));
        collapsingPanels.add(classifier);
        root.add(collapsingPanels);

        final StackPanel stacks = new StackPanel(Arrays.asList(
                new SingleStack(Messages.i18n().tr("Первая")),
                new SingleStack(Messages.i18n().tr("Вторая")),
                new SingleStack(Messages.i18n().tr("Третья")),
                new SingleStack(Messages.i18n().tr("Четвертая"))), 500);
        stacks.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        ArtaScrollPanel scroll1 = new ArtaScrollPanel(ColorType.BLACK);
        FlowPanel colorButtonPanel = new FlowPanel();
        colorButtonPanel.setPixelSize(500, 1000);
        scroll1.setWidget(colorButtonPanel);
        scroll1.setHeight("100%");
        stacks.getStacks().get(0).getPanel().add(scroll1);

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            stacks.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        } else {
            stacks.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        }
        stacks.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        stacks.getElement().getStyle().setMarginTop(10, Style.Unit.PX);


        SingleStack stackWhite = new SingleStack(Messages.i18n().tr("Первая"));
        ArtaScrollPanel scroll2 = new ArtaScrollPanel();
        FlowPanel colorButtonPanel2 = new FlowPanel();
        colorButtonPanel2.setPixelSize(500, 1000);
        scroll2.setWidget(colorButtonPanel2);
        scroll2.setHeight("100%");
        stackWhite.getPanel().add(scroll2);
        final StackPanel whiteStacks = new StackPanel(Arrays.asList(stackWhite,
                new SingleStack(Messages.i18n().tr("Вторая")),
                new SingleStack(Messages.i18n().tr("Третья")),
                new SingleStack(Messages.i18n().tr("Четвертая"))), 500, ColorType.WHITE);
        whiteStacks.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        whiteStacks.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        whiteStacks.getElement().getStyle().setMarginTop(10, Style.Unit.PX);

        final FlowPanel checkBoxesPanel = new FlowPanel();
        checkBoxesPanel.setSize("20px", "500px");
        checkBoxesPanel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        checkBoxesPanel.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            checkBoxesPanel.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        } else {
            checkBoxesPanel.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        }
        final ArtaCheckBox[] checkBoxes = new ArtaCheckBox[] {
            new ArtaCheckBox(), new ArtaCheckBox(), new ArtaCheckBox(), new ArtaCheckBox()
        };

        final ArtaRadioButton[] radioButtons = new ArtaRadioButton[] {
                new ArtaRadioButton("stacks"), new ArtaRadioButton("stacks"),
                new ArtaRadioButton("stacks"), new ArtaRadioButton("stacks"),
        };

        for (int i = 0; i < checkBoxes.length; i++) {
            final int finalI = i;
            checkBoxes[i].addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    stacks.getStacks().get(finalI).setEnabled(event.getValue());
                    whiteStacks.getStacks().get(finalI).setEnabled(event.getValue());
                    radioButtons[finalI].setEnabled(event.getValue());
                }
            });
            checkBoxesPanel.add(checkBoxes[i]);
            checkBoxes[i].getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
            checkBoxes[i].setValue(true, false);
            checkBoxes[i].getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        }
        checkBoxes[0].setEnabled(false);

        root.add(checkBoxesPanel);
        root.add(stacks);
        root.add(whiteStacks);

        FlowPanel radioButtonsPanel = new FlowPanel();
        radioButtonsPanel.setSize("20px", "500px");
        radioButtonsPanel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        radioButtonsPanel.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            radioButtonsPanel.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        } else {
            radioButtonsPanel.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        }
        radioButtonsPanel.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);

        for (int i = 0; i < radioButtons.length; i++) {
            final int finalI = i;
            radioButtonsPanel.add(radioButtons[i]);
            radioButtons[i].getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
            radioButtons[i].getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            radioButtons[i].addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    stacks.openStack(finalI, false);
                    whiteStacks.openStack(finalI, false);
                    for (int j = 0; j < checkBoxes.length; j++) {
                        if (j != finalI) {
                            checkBoxes[j].setEnabled(true);
                        } else {
                            checkBoxes[j].setEnabled(false);
                        }
                    }
                }
            });
        }
        radioButtons[0].setValue(true, false);

        root.add(radioButtonsPanel);

        stacks.addStackOpenHandler(new StackOpenEvent.Handler() {
            @Override
            public void onStackOpened(StackOpenEvent event) {
                whiteStacks.openStack(event.getIndex(), false);
                radioButtons[event.getIndex()].setValue(true, false);
                for (int i = 0; i < checkBoxes.length; i++) {
                    if (i != event.getIndex()) {
                        checkBoxes[i].setEnabled(true);
                    } else {
                        checkBoxes[i].setEnabled(false);
                    }
                }
            }
        });

        whiteStacks.addStackOpenHandler(new StackOpenEvent.Handler() {
            @Override
            public void onStackOpened(StackOpenEvent event) {
                stacks.openStack(event.getIndex(), false);
                radioButtons[event.getIndex()].setValue(true, false);
                for (int i = 0; i < checkBoxes.length; i++) {
                    if (i != event.getIndex()) {
                        checkBoxes[i].setEnabled(true);
                    } else {
                        checkBoxes[i].setEnabled(false);
                    }
                }
            }
        });

        addCodeSample(whiteStacks, Messages.i18n().tr("Стек-панель"), ShowCase.RESOURCES.stackPanel().getText());

        ArtaScrollPanel scroll = new ArtaScrollPanel(root);
        scroll.setHeight("100%");
        return scroll;
    }

    public Widget getCheckBoxPanel() {
        FlowPanel root = new FlowPanel();

        ArtaCheckBox checkbox1 = new ArtaCheckBox();
        addCodeSample(checkbox1, Messages.i18n().tr("Чекбокс"), ShowCase.RESOURCES.checkbox().getText());
        checkbox1.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        setMarginLeft(checkbox1, 20);
        root.add(checkbox1);

        ArtaCheckBox checkbox2 = new ArtaCheckBox();
        checkbox2.setEnabled(false);
        checkbox2.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        setMarginLeft(checkbox2, 20);
        root.add(checkbox2);

        ArtaCheckBox checkbox3 = new ArtaCheckBox();
        addCodeSample(checkbox3, Messages.i18n().tr("Неактивный чекбокс"), ShowCase.RESOURCES.checkboxDisabled().getText());
        checkbox3.setValue(true, false);
        checkbox3.setEnabled(false);
        checkbox3.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        setMarginLeft(checkbox3, 20);
        root.add(checkbox3);

        root.add(new Br());

        ArtaRadioButton[] radios = new ArtaRadioButton[] {
                new ArtaRadioButton("showCaseRadio"), new ArtaRadioButton("showCaseRadio"),
                new ArtaRadioButton("showCaseRadio"), new ArtaRadioButton("showCaseRadio"),
                new ArtaRadioButton("showCaseRadio2")
        };

        addCodeSample(radios[0], Messages.i18n().tr("Радио-кнопка"), ShowCase.RESOURCES.radiobutton().getText());

        radios[3].setEnabled(false);
        radios[4].setValue(true);
        radios[4].setEnabled(false);

        for (ArtaRadioButton radio : radios) {
            radio.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
            setMarginLeft(radio, 20);
            root.add(radio);
        }

        return root;
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

    private DropDownList<String> createList() {
        DropDownList<String> list = new DropDownList<String>();
        for (String name : createShuffledNames()) {
            list.add(new MenuItem<String>(name, name));
        }

        return list;
    }

    private DropDownListMulti<String> createMultiList(Widget parent) {
        DropDownListMulti<String> list = new DropDownListMulti<String>();
        String[] names = createShuffledNames();
        for (String name : names) {
            list.add(new MenuItem<String>(name, name));
        }

        return list;
    }

    private static InlineLabel createLabel(String text) {
        return createLabel(text, 180);
    }

    private static InlineLabel createLabel(String text, int width) {
        InlineLabel label = new InlineLabel(Messages.i18n().tr(text));
        label.setStyleName(SynergyComponents.getResources().cssComponents().mainText());
        label.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        StyleUtils.setLineHeight(label.getElement(), 32, Style.Unit.PX);
        label.setWidth(width + "px");
        return label;
    }

    private Widget getPeriodInputs() {
        FlowPanel root = new FlowPanel();

        ArtaCheckBox box = new ArtaCheckBox();
        box.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        box.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        box.setValue(true, false);
        root.add(box);

        final FullRepeatChooser chooser = new FullRepeatChooser();
        addCodeSample(chooser, Messages.i18n().tr("Компонент повторения"), ShowCase.RESOURCES.periodInput().getText());
        chooser.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        chooser.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        chooser.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        root.add(chooser);

        chooser.setMode(RepeatChooser.MODE.YEAR);
        chooser.getChooser().addSelected(new RepeatDate(22, 2, RepeatChooser.MODE.YEAR));

        box.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                chooser.setEnabled(event.getValue());
            }
        });
        return root;
    }

    /**
     * Поля с тегами
     * @return панель с полями с тегами
     */
    private Widget getTagInputs() {
        FlowPanel root = new FlowPanel();
        StyleUtils.setLineHeight(root.getElement(), 1, Style.Unit.PX);
        root.getElement().getStyle().setPadding(10, Style.Unit.PX);

        final List<HasEnabled> enabledWidgets = new ArrayList<HasEnabled>();

        FlowPanel[] rows = new FlowPanel[5];
        int currentRow = 0;

        //first row

        rows[currentRow] = new FlowPanel();
        rows[currentRow].add(createLabel("Поля с индикаторами: "));

        final TagInput noListHasIndicator = new TagInput();
        addCodeSample(noListHasIndicator, Messages.i18n().tr("Поле с тэгами без списка"), ShowCase.RESOURCES.tagInput().getText());
        setMarginLeft(noListHasIndicator, 10);
        noListHasIndicator.setWidth(350);

        rows[currentRow].add(noListHasIndicator);
        enabledWidgets.add(noListHasIndicator);

        final TagInput<String> hasListHasIndicator = new TagInput<String>();
        addCodeSample(hasListHasIndicator, Messages.i18n().tr("Поле с тэгами"), ShowCase.RESOURCES.tagInputList().getText());
        setMarginLeft(hasListHasIndicator, 20);
        hasListHasIndicator.setWidth(300);
        for (String name : createShuffledNames()) {
            hasListHasIndicator.addListItem(name, name);
        }
        hasListHasIndicator.setListEnabled(true);

        hasListHasIndicator.setTitle(Messages.i18n().tr("Фильтрация списка по вхождению текста"));
        hasListHasIndicator.setListFilter(ListTextFilter.createPrefixFilter());

        rows[currentRow].add(hasListHasIndicator);
        enabledWidgets.add(hasListHasIndicator);

        //второй ряд

        currentRow++;
        rows[currentRow] = new FlowPanel();
        rows[currentRow].add(createLabel("Поля без кнопки"));

        TagInput noListNoButton = new TagInput(false);
        noListNoButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        rows[currentRow].add(noListNoButton);
        enabledWidgets.add(noListNoButton);

        TagInput<String> hasListNoButton = new TagInput<String>(false);
        addCodeSample(hasListNoButton, Messages.i18n().tr("Поле с тэгами без кнопки"),
                ShowCase.RESOURCES.tagInputNoButton().getText());
        hasListNoButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        for (String name : createShuffledNames()) {
            hasListNoButton.addListItem(name, name);
        }
        hasListNoButton.setListEnabled(true);

        hasListNoButton.setTitle(Messages.i18n().tr("Префиксный выбор из списка"));
        rows[currentRow].add(hasListNoButton);
        enabledWidgets.add(hasListNoButton);

        //третий ряд

        currentRow++;
        rows[currentRow] = new FlowPanel();
        rows[currentRow].add(createLabel("Мультикомбобокс"));

        MultiComboBox<String> multiComboBox = new MultiComboBox<String>();
        addCodeSample(multiComboBox, Messages.i18n().tr("Комбобокс"), ShowCase.RESOURCES.multiComboBox().getText());
        String[] comboNames = createShuffledNames();

        for (String name : comboNames) {
            multiComboBox.addListItem(name, name);
        }
        multiComboBox.select(comboNames[0]);
        multiComboBox.select(comboNames[1]);
        multiComboBox.select(comboNames[comboNames.length - 1]);

        multiComboBox.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        multiComboBox.setWidth(300);
        rows[currentRow].add(multiComboBox);
        enabledWidgets.add(multiComboBox);

        //четвертый ряд
        currentRow++;
        rows[currentRow] = new FlowPanel();
        rows[currentRow].add(createLabel("Выбор объекта"));

        ObjectChooser chooser = new ObjectChooser(new SimpleEventBus());
        chooser.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        rows[currentRow].add(chooser);
        enabledWidgets.add(chooser);

        //пятый ряд
        currentRow++;
        rows[currentRow] = new FlowPanel();

        SimpleButton button = new SimpleButton(Messages.i18n().tr("Включить/выключить"));
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                for (HasEnabled item : enabledWidgets) {
                    item.setEnabled(!item.isEnabled());
                }
            }
        });
        rows[currentRow].add(button);

        for (FlowPanel row : rows) {
            root.add(row);
            row.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        }

        return root;
    }

    /**
     * Текстовые поля
     * @return панель с текстовыми полями
     */
    private Widget getTextInputs() {
        FlowPanel root = new FlowPanel();
        root.getElement().getStyle().setPadding(10, Style.Unit.PX);

        final TextInput textInput = new TextInput();
        addCodeSample(textInput, Messages.i18n().tr("Текстовое поле"), ShowCase.RESOURCES.textInput().getText());
        textInput.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        root.add(textInput);
        textInput.setPlaceHolder(Messages.i18n().tr("Необязательное поле"));

        final TextInput input = new TextInput();
        addCodeSample(input, Messages.i18n().tr("Отключенное текстовое поле"), ShowCase.RESOURCES.textInputDisabled().getText());
        input.setEnabled(false);
        root.add(input);
        input.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(input, 20);
        input.setPlaceHolder(Messages.i18n().tr("Неактивное поле"));

        final TextInput inputAllow = new TextInput(false);
        addCodeSample(inputAllow, Messages.i18n().tr("Обязательное текстовое поле"), ShowCase.RESOURCES.textInputNonEmpty().getText());
        root.add(inputAllow);
        inputAllow.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(inputAllow, 20);
        inputAllow.setPlaceHolder(Messages.i18n().tr("Обязательное поле"));

        final TextInput widthInput = new TextInput(false);
        root.add(widthInput);
        widthInput.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(widthInput, 20);
        widthInput.setPlaceHolder(Messages.i18n().tr("Широкое поле ввода"));
        widthInput.setWidth("300px");

        final TextInput smallWidthInput = new TextInput(false);
        root.add(smallWidthInput);
        smallWidthInput.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(smallWidthInput, 10);
        smallWidthInput.setPlaceHolder(Messages.i18n().tr("Маленькое поле ввода"));
        smallWidthInput.setWidth("100px");

        root.add(new Br());

        SearchResultInput<String> searchEnabledWithButton = new SearchResultInput<String>(true);
        addCodeSample(searchEnabledWithButton, Messages.i18n().tr("Поле поиска"), ShowCase.RESOURCES.search().getText());

        DropDownList<String> list = createList();
        for (MenuItem<String> item : list.getItems()) {
            item.setIcon(ImageResources.IMPL.magzhan());
        }
        searchEnabledWithButton.setList(list);
        root.add(searchEnabledWithButton);

        SearchResultInput<String> searchDisabledWithButton = new SearchResultInput<String>(true);
        setMarginLeft(searchDisabledWithButton, 20);
        DropDownList<String> list2 = createList();
        for (MenuItem<String> item : list2.getItems()) {
            item.setIcon(ImageResources.IMPL.magzhan());
        }
        searchDisabledWithButton.setList(list2);
        searchDisabledWithButton.setEnabled(false);
        root.add(searchDisabledWithButton);

        SearchResultInput<String> searchNoButton = new SearchResultInput<String>(false);
        setMarginLeft(searchNoButton, 10);
        DropDownList<String> list3 = createList();
        searchNoButton.setList(list3);
        root.add(searchNoButton);

        root.add(new Br());

        NumberInput integersInput = new NumberInput();
        addCodeSample(integersInput, Messages.i18n().tr("Числовое поле (целые)"), ShowCase.RESOURCES.numberInputInteger().getText());
        integersInput.setPlaceHolder(Messages.i18n().tr("Только цифры"));
        root.add(integersInput);

        List<InputConstraint> constraintsForDouble = new ArrayList<InputConstraint>();
        constraintsForDouble.add(DoubleConstraint.getInstance());
        NumberInput doublesInput = new NumberInput(constraintsForDouble);
        addCodeSample(doublesInput, Messages.i18n().tr("Числовое поле (дробные)"), ShowCase.RESOURCES.numberInputDouble().getText());

        doublesInput.setPlaceHolder(Messages.i18n().tr("Дробные"));
        setMarginLeft(doublesInput, 20);
        root.add(doublesInput);

        List<InputConstraint> max200 = new ArrayList<InputConstraint>();
        max200.add(OnlyDigitsConstraint.getInstance());
        max200.add(new MaxNumberConstraint(200));
        NumberInput maxInput = new NumberInput(max200);
        addCodeSample(maxInput, Messages.i18n().tr("Числовое поле (целые, ограничены сверху)"),
                ShowCase.RESOURCES.numberInputMax().getText());
        setMarginLeft(maxInput, 20);
        maxInput.setPlaceHolder(Messages.i18n().tr("Максимально 200"));
        root.add(maxInput);

        root.add(new Br());

        final ArtaTextArea textArea = new ArtaTextArea();
        textArea.setPlaceHolder(Messages.i18n().tr("Многострочное поле ввода"));
        root.add(textArea);

        final ArtaTextArea disableTextArea = new ArtaTextArea(false);
        setMarginLeft(disableTextArea, 10);
        disableTextArea.setPlaceHolder(Messages.i18n().tr("Неактивное многострочное поле ввода"));
        disableTextArea.setEnabled(false);
        root.add(disableTextArea);

        final ArtaTextArea textAreaEmpty = new ArtaTextArea(false);
        setMarginLeft(textAreaEmpty, 10);
        textAreaEmpty.setPlaceHolder(Messages.i18n().tr("Обязательное многострочное поле ввода"));
        root.add(textAreaEmpty);

        root.add(new Br());

        final ArtaTextArea textAreaSize = new ArtaTextArea(false);
        addCodeSample(textAreaSize, Messages.i18n().tr("Многострочное поле ввода"), ShowCase.RESOURCES.textarea().getText());
        textAreaSize.setPlaceHolder(Messages.i18n().tr("Многострочное поле ввода с заданным размером"));
        textAreaSize.setPixelSize(300, 300);
        root.add(textAreaSize);

        final ArtaTextArea textAreaWidth = new ArtaTextArea(false);
        setMarginLeft(textAreaWidth, 20);
        textAreaWidth.setPlaceHolder(Messages.i18n().tr("Многострочное поле ввода с заданной шириной"));
        textAreaWidth.setWidth("500px");
        root.add(textAreaWidth);

        root.add(new Br());

        SimpleButton button = new SimpleButton(Messages.i18n().tr("Валидация полей"), SimpleButton.Type.APPROVE);
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
        root.add(button);
        button.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        ArtaScrollPanel scroll = new ArtaScrollPanel();
        scroll.setWidget(root);
        scroll.setHeight("100%");

        return scroll;
    }

    private void setMarginLeft(Widget w, int margin) {
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            w.getElement().getStyle().setMarginRight(margin, Style.Unit.PX);
        } else {
            w.getElement().getStyle().setMarginLeft(margin, Style.Unit.PX);
        }
    }

    /**
     * Простые кнопки
     */
    private Widget getSimpleButtonPanel() {
        final FlowPanel simpleButtonPanel = new FlowPanel();
        int horizontalMargin = 20;

        simpleButtonPanel.getElement().getStyle().setPadding(10, Style.Unit.PX);

        final SimpleButton simpleButton = new SimpleButton(Messages.i18n().tr("Простая кнопка"));
        addCodeSample(simpleButton, simpleButton.getText(), ShowCase.RESOURCES.simpleButton().getText());
        simpleButton.setWidth("140px");
        simpleButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        simpleButtonPanel.add(simpleButton);

        SimpleButton simpleButton1 = new SimpleButton(Messages.i18n().tr("Неактивная кнопка"));
        addCodeSample(simpleButton1, simpleButton1.getText(), ShowCase.RESOURCES.simpleButtonDisabled().getText());
        simpleButton1.setEnabled(false);
        simpleButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(simpleButton1, horizontalMargin);
        simpleButtonPanel.add(simpleButton1);

        SimpleButton simpleButton2 = new SimpleButton(Messages.i18n().tr("Кнопка с кликом"));
        addCodeSample(simpleButton2, simpleButton2.getText(), ShowCase.RESOURCES.simpleButtonClick().getText());
        simpleButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(simpleButton2, horizontalMargin);
        simpleButtonPanel.add(simpleButton2);
        simpleButton2.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(Messages.i18n().tr("Кнопка была нажата!"));
            }
        });

        ContextMenu menu = new ContextMenu();

        menu.add(new MenuItem<Command>(null, "Zoom", ImageResources.IMPL.zoom()));
        menu.add(new MenuItem<Command>(null, "Left", ImageResources.IMPL.navigationLeft()));
        menu.add(new MenuItem<Command>(null, "Right", ImageResources.IMPL.navigationRight()));
        menu.addSeparator(1);

        ContextMenuButton simpleButton4 = new ContextMenuButton(Messages.i18n().tr("Кнопка с меню"));
        addCodeSample(simpleButton4, simpleButton4.getText(), ShowCase.RESOURCES.simpleButtonMenu().getText());
        simpleButton4.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(Messages.i18n().tr("Кнопка с меню была нажата!"));
            }
        });
        simpleButton4.setWidth("140px");
        simpleButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(simpleButton4, horizontalMargin);
        simpleButtonPanel.add(simpleButton4);

        simpleButton4.setContextMenu(menu);

        Label pluralLabel = new Label(Messages.i18n().trn("Кнопка", "%d кнопок", 5));

        return simpleButtonPanel;
    }


    private int notificationsDelay = 1000;

    private ClickHandler createClickHandlerForNotification(final Notification notification, final ArtaCheckBox checkBox) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (notification.isShowing()) {
                    notification.hide();
                } else {
                    notification.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = (Window.getClientWidth() - offsetWidth) / 2;
                            notification.setPopupPosition(left, 40);
                        }
                    });
                }

                if (checkBox.getValue()) {
                    new Timer() {
                        @Override
                        public void run() {
                            notification.hide();
                        }
                    }.schedule(notificationsDelay);
                }
            }
        };
    }

    private ClickHandler createClickHandlerForNotification(final Notification notification, final boolean hide) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (notification.isShowing()) {
                    notification.hide();
                } else {
                    notification.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = (Window.getClientWidth() - offsetWidth) / 2;
                            notification.setPopupPosition(left, 40);
                        }
                    });
                }

                if (hide) {
                    new Timer() {
                        @Override
                        public void run() {
                            notification.hide();
                        }
                    }.schedule(notificationsDelay);
                }
            }
        };
    }

    private Widget getNotificationsPanel() {
        FlowPanel root = new FlowPanel();
        root.addStyleName(SynergyComponents.getResources().cssComponents().mainText());
        root.getElement().getStyle().setColor(Colors.textColor2().hex());

        final ArtaCheckBox hideCheckbox = new ArtaCheckBox();
        hideCheckbox.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        hideCheckbox.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        hideCheckbox.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        hideCheckbox.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        hideCheckbox.setValue(true, false);
        final Label hideLabel = new Label(Messages.i18n().tr("Скрывать ли уведомления:"));
        hideLabel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        hideLabel.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        hideLabel.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        final Label delayLabel = new Label("Delay: ");
        delayLabel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        delayLabel.getElement().getStyle().setMarginTop(8, Style.Unit.PX);
        delayLabel.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);

        final NumberInput delayInput = new NumberInput(Arrays.<InputConstraint>asList(OnlyDigitsConstraint.getInstance()));
        delayInput.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        delayInput.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        delayInput.setText(Integer.toString(notificationsDelay));

        root.add(hideLabel);
        root.add(hideCheckbox);
        root.add(new Br());
        root.add(delayLabel);
        root.add(delayInput);

        hideCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                Style.Visibility visibility = event.getValue() ? Style.Visibility.VISIBLE : Style.Visibility.HIDDEN;
                delayInput.getElement().getStyle().setVisibility(visibility);
                delayLabel.getElement().getStyle().setVisibility(visibility);
            }
        });

        delayInput.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                notificationsDelay = delayInput.getIntegerValue();
            }
        });

        root.add(new Br());
        final SimpleButton success = new SimpleButton(Messages.i18n().tr("Успех"));
        addCodeSample(success, Messages.i18n().tr("Уведомление об успехе"), ShowCase.RESOURCES.notificationSuccess().getText());
        success.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        success.getElement().getStyle().setMarginTop(30, Style.Unit.PX);

        final Notification successNotification = new Notification(Messages.i18n().tr("Успех"), Notification.Type.SUCCESS);
        success.addClickHandler(createClickHandlerForNotification(successNotification, hideCheckbox));
        root.add(success);

        final SimpleButton failure = new SimpleButton(Messages.i18n().tr("Ошибка"));
        addCodeSample(failure, Messages.i18n().tr("Уведомление об ошибке"), ShowCase.RESOURCES.notificationFailure().getText());
        failure.getElement().getStyle().setMarginLeft(30, Style.Unit.PX);
        failure.getElement().getStyle().setMarginTop(30, Style.Unit.PX);
        final Notification failureNotification = new Notification(Messages.i18n().tr("Ошибка"),
                Arrays.asList(Messages.i18n().tr("Первая ошибка в уведомлении"),
                        Messages.i18n().tr("Вторая ошибка в уведомлении"),
                        Messages.i18n().tr("Ошибка в классе kz.arta.synergy.components.client.delayInput.date.repeat.MonthlyRepeatChooser")),
                Notification.Type.FAILURE);
        failure.addClickHandler(createClickHandlerForNotification(failureNotification, hideCheckbox));
        root.add(failure);

        SimpleButton question = new SimpleButton(Messages.i18n().tr("Вопрос"));
        addCodeSample(question, Messages.i18n().tr("Уведомление с вопросом"), ShowCase.RESOURCES.notificationQuestion().getText());
        question.getElement().getStyle().setMarginLeft(30, Style.Unit.PX);
        question.getElement().getStyle().setMarginTop(30, Style.Unit.PX);
        final NotificationWithResponse neutralQuestionNotification = new NotificationWithResponse(
                Messages.i18n().tr("Сохранить внесенные изменения?"),
                Notification.Type.QUESTION,
                false);
        neutralQuestionNotification.addYesClickHandler(createHideNotificationHandler(neutralQuestionNotification));
        neutralQuestionNotification.addNoClickHandler(createHideNotificationHandler(neutralQuestionNotification));
        question.addClickHandler(createClickHandlerForNotification(neutralQuestionNotification, false));
        root.add(question);

        SimpleButton warning = new SimpleButton(Messages.i18n().tr("Предупреждение"));
        addCodeSample(warning, Messages.i18n().tr("Уведомление-предупреждение"), ShowCase.RESOURCES.notificationWarning().getText());
        warning.getElement().getStyle().setMarginLeft(30, Style.Unit.PX);
        warning.getElement().getStyle().setMarginTop(30, Style.Unit.PX);
        final NotificationWithResponse warningNotification = new NotificationWithResponse(
                Messages.i18n().tr("Сохранить внесенные изменения?"),
                Notification.Type.WARNING,
                true);
        warningNotification.addYesClickHandler(createHideNotificationHandler(warningNotification));
        warningNotification.addNoClickHandler(createHideNotificationHandler(warningNotification));
        warningNotification.addCancelHandler(createHideNotificationHandler(warningNotification));
        warning.addClickHandler(createClickHandlerForNotification(warningNotification, false));
        root.add(warning);

        return root;
    }

    private ClickHandler createHideNotificationHandler(final NotificationWithResponse neutralQuestionNotification) {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                neutralQuestionNotification.hide();
            }
        };
    }

    /**
     * @return панель панели файлов
     */
    private Widget getFilesPanelPanel() {
        FlowPanel root = new FlowPanel();

        FilesPanel files = new FilesPanel();
        addCodeSample(files, Messages.i18n().tr("Панель файлов"), ShowCase.RESOURCES.filesPanel().getText());
        files.getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        files.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        files.getElement().getStyle().setWidth(400, Style.Unit.PX);
        files.getElement().getStyle().setHeight(400, Style.Unit.PX);
        root.add(files);

        return root;
    }

    private Widget getSlidersPanel() {
        FlowPanel root = new FlowPanel();

        final Slider slider1 = new Slider(true);
        addCodeSample(slider1, Messages.i18n().tr("Слайдер"), ShowCase.RESOURCES.slider().getText());
        slider1.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        slider1.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        slider1.getElement().getStyle().setWidth(120, Style.Unit.PX);
        slider1.getElement().getStyle().setMarginTop(25, Style.Unit.PX);
        slider1.getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        root.add(slider1);

        final Indicator indicator1 = new Indicator(" ");
        indicator1.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        indicator1.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        indicator1.getElement().getStyle().setMarginLeft(40, Style.Unit.PX);
        root.add(indicator1);

        root.add(new Br());

        final ProgressBar progress1 = new ProgressBar(ProgressBar.Type.POSITIVE);
        progress1.getElement().getStyle().setWidth(150, Style.Unit.PX);
        progress1.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        progress1.getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        progress1.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        root.add(progress1);

        slider1.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                progress1.setValue(slider1.getValue());
                indicator1.setText(Integer.toString((int) (slider1.getValue() * 100)));
            }
        });
        slider1.addCircleMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                indicator1.setText(Integer.toString((int) (slider1.getValue() * 100)));
            }
        });

        slider1.setOptionalValue(0.5);
        slider1.setValue(0.66);
        progress1.setOptionalValue(0.5);
        progress1.setValue(0.66);

        root.add(new Br());

        final Slider slider2 = new Slider(false);
        addCodeSample(slider2, Messages.i18n().tr("Слайдер (красный)"), ShowCase.RESOURCES.sliderRed().getText());
        slider2.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        slider2.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        slider2.getElement().getStyle().setWidth(120, Style.Unit.PX);
        slider2.getElement().getStyle().setMarginTop(25, Style.Unit.PX);
        slider2.getElement().getStyle().setMarginLeft(20, Style.Unit.PX);

        root.add(slider2);

        final Indicator indicator2 = new Indicator(" ");
        indicator2.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        indicator2.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        indicator2.getElement().getStyle().setMarginLeft(40, Style.Unit.PX);
        root.add(indicator2);


        root.add(new Br());

        List<ProgressBarCustomLabel> customLabels = new ArrayList<ProgressBarCustomLabel>();
        customLabels.add(new ProgressBarConstraintLabel(0, 0, Messages.i18n().tr("Получение")));
        customLabels.add(new ProgressBarConstraintLabel(1, 1, Messages.i18n().tr("Завершено")));
        customLabels.add(new ProgressBarConstraintLabel(0, 0.2, Messages.i18n().tr("Подготовка")));
        customLabels.add(new ProgressBarConstraintLabel(0.2, 0.7, Messages.i18n().tr("Исполнение сложного задания")));
        customLabels.add(new ProgressBarConstraintLabel(0.7, 1, Messages.i18n().tr("Завершение исполнения сложного задания")));

        final ProgressBar progress2 = new ProgressBar(ProgressBar.Type.NEGATIVE, customLabels);
        progress2.getElement().getStyle().setWidth(150, Style.Unit.PX);
        progress2.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        progress2.getElement().getStyle().setMarginLeft(20, Style.Unit.PX);
        progress2.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        root.add(progress2);

        slider2.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                progress2.setValue(slider2.getValue());
                indicator2.setText(Integer.toString((int) (slider2.getValue() * 100)));
            }
        });
        slider2.addCircleMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                indicator2.setText(Integer.toString((int) (slider2.getValue() * 100)));
            }
        });

        slider2.setOptionalValue(0.66);
        slider2.setValue(0.33);
        progress2.setOptionalValue(0.66);
        progress2.setValue(0.33);

        return root;
    }

    private Widget getCommentsPanel() {
        FlowPanel root = new FlowPanel();

        CommentsPanel commentsPanel = new CommentsPanel();
        addCodeSample(commentsPanel, Messages.i18n().tr("Панель комментариев"), ShowCase.RESOURCES.comments().getText());
        Style style = commentsPanel.getElement().getStyle();
        style.setPosition(Style.Position.ABSOLUTE);
        style.setTop(20, Style.Unit.PX);
        style.setBottom(20, Style.Unit.PX);
        style.setLeft(20, Style.Unit.PX);
        commentsPanel.setWidth("400px");
        root.add(commentsPanel);

        TextComment comment1 = new TextComment("Поле ввода текста комментария «растягивается» вниз при увеличении количества строк, но не более чем на 10 строк. После ввода 11-й строки появляется полоса прокрутки в поле ввода.",
                "John Doe", new Date(), CommentType.GENERAL);
        comment1.setDeletable(false);

        Comment comment2 = new TextComment("Все хорошо.\n--\n http://arta.pro", "John Doe", new Date(), CommentType.ACCEPT);
        Comment comment3 = new TextComment("Все плохо.", "Jane Doe", new Date(), CommentType.DECLINE);

        commentsPanel.getComments().addComment(comment1);
        commentsPanel.getComments().addComment(comment2);
        commentsPanel.getComments().addComment(comment3);

        Comment fileComment = new FileComment(ImageResources.IMPL.calendarIcon(), "calendar.png", "John Doe", new Date(), CommentType.ACCEPT);
        commentsPanel.getComments().addComment(fileComment);

        CommentsPanel darkCommentsPanel = new CommentsPanel(true);
        addCodeSample(darkCommentsPanel, Messages.i18n().tr("Панель заметок"), ShowCase.RESOURCES.commentsDark().getText());
        Style darkStyle = darkCommentsPanel.getElement().getStyle();
        darkStyle.setPosition(Style.Position.ABSOLUTE);
        darkStyle.setTop(20, Style.Unit.PX);
        darkStyle.setBottom(20, Style.Unit.PX);
        darkStyle.setLeft(500, Style.Unit.PX);
        darkCommentsPanel.setWidth("400px");
        root.add(darkCommentsPanel);

        darkCommentsPanel.getComments().addComment(new TextComment(comment1));

        return root;
    }

    /**
     * Кнопки с иконками
     */
    private Widget getIconButtonPanel() {
        int horizontalMargin = 20;

        FlowPanel iconButtonPanel = new FlowPanel();
        iconButtonPanel.getElement().getStyle().setPadding(10, Style.Unit.PX);

        SimpleButton iconButton = new SimpleButton(Messages.i18n().tr("Кнопка с иконкой"), ShowCase.IMAGES.zoom());
        addCodeSample(iconButton, iconButton.getText(), ShowCase.RESOURCES.iconButton().getText());
        iconButtonPanel.add(iconButton);
        iconButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        SimpleButton iconButton1 = new SimpleButton(Messages.i18n().tr("Кнопка с длинным текстом"), ShowCase.IMAGES.zoom());
        iconButtonPanel.add(iconButton1);
        iconButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(iconButton1, horizontalMargin);

        SimpleButton iconButton2 = new SimpleButton(Messages.i18n().tr("Кнопка с длинным текстом"), ShowCase.IMAGES.zoom(), ButtonBase.IconPosition.RIGHT);
        addCodeSample(iconButton2, Messages.i18n().tr("Кнопка с иконкой справа"), ShowCase.RESOURCES.iconButtonRight().getText());
        iconButton2.setWidth("150px");
        iconButtonPanel.add(iconButton2);
        iconButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(iconButton2, horizontalMargin);

        SimpleButton iconButton3 = new SimpleButton(Messages.i18n().tr("Кнопка неактивная"), ShowCase.IMAGES.zoom());
        iconButton3.setWidth("200px");
        iconButtonPanel.add(iconButton3);
        iconButton3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(iconButton3, 10);
        iconButton3.setEnabled(false);

        ImageButton iconButton4 = new ImageButton(ShowCase.IMAGES.zoom());
        addCodeSample(iconButton4, Messages.i18n().tr("Кнопка только с иконкой"), ShowCase.RESOURCES.onlyIconButton().getText());
        iconButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(iconButton4, horizontalMargin);
        iconButtonPanel.add(iconButton4);

        ImageButton iconButton5 = new ImageButton(ShowCase.IMAGES.zoom());
        iconButton5.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(iconButton5, horizontalMargin);
        iconButton5.setEnabled(false);
        iconButtonPanel.add(iconButton5);

        ContextMenu menu2 = createSimpleMenu();
        ContextMenuButton iconButton6 = new ContextMenuButton(Messages.i18n().tr("Кнопка с меню"), ShowCase.IMAGES.zoom());
        iconButton6.setWidth("150px");
        iconButtonPanel.add(iconButton6);
        iconButton6.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(iconButton6, horizontalMargin);
        iconButton6.setContextMenu(menu2);

        ContextMenu menu4 = createSimpleMenu();
        ContextMenuButton iconButton7 = new ContextMenuButton(Messages.i18n().tr("Кнопка с меню"), ShowCase.IMAGES.zoom(), ButtonBase.IconPosition.RIGHT);
        iconButton7.setWidth("150px");
        iconButtonPanel.add(iconButton7);
        iconButton7.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(iconButton7, horizontalMargin);
        iconButton7.setContextMenu(menu4);

        ContextMenu menu5 = createSimpleMenu();
        ContextMenuButton iconButton8 = new ContextMenuButton(Messages.i18n().tr("Кнопка с меню"), ShowCase.IMAGES.zoom(), ButtonBase.IconPosition.RIGHT);
        addCodeSample(iconButton8, Messages.i18n().tr("Широкая кнопка с меню и иконкой справа"), ShowCase.RESOURCES.iconButtonMenuWide().getText());
        iconButton8.setWidth("400px");
        iconButtonPanel.add(iconButton8);
        iconButton8.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(iconButton8, horizontalMargin);
        iconButton8.setContextMenu(menu5);
        return iconButtonPanel;
    }

    /**
     * Цветные кнопки
     */
    private Widget getColorButtonPanel() {
        int horizontalMargin = 20;
        FlowPanel colorButtonPanel = new FlowPanel();
        colorButtonPanel.getElement().getStyle().setPadding(10, Style.Unit.PX);
        colorButtonPanel.setHeight("2000px");
        colorButtonPanel.setWidth("2000px");

        SimpleButton colorButton = new SimpleButton((Messages.i18n().tr("Создать")), SimpleButton.Type.APPROVE);
        addCodeSample(colorButton, Messages.i18n().tr("Кнопка подтверждения"), ShowCase.RESOURCES.acceptButton().getText());
        colorButtonPanel.add(colorButton);
        colorButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);

        SimpleButton colorButton1 = new SimpleButton(Messages.i18n().tr("Удалить"), SimpleButton.Type.DECLINE);
        addCodeSample(colorButton1, Messages.i18n().tr("Кнопка отклонения"), ShowCase.RESOURCES.acceptButton().getText());
        colorButtonPanel.add(colorButton1);
        colorButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(colorButton1, horizontalMargin);

        SimpleButton colorButton2 = new SimpleButton(Messages.i18n().tr("Кнопка с длинным текстом"), SimpleButton.Type.DECLINE);
        colorButtonPanel.add(colorButton2);
        colorButton2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(colorButton2, horizontalMargin);

        SimpleButton colorButton3 = new SimpleButton(Messages.i18n().tr("Кнопка с длинным текстом"), SimpleButton.Type.DECLINE);
        colorButton3.setWidth("100px");
        colorButtonPanel.add(colorButton3);
        colorButton3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(colorButton3, horizontalMargin);

        SimpleButton colorButton4 = new SimpleButton(Messages.i18n().tr("Кнопка с длинным текстом"), SimpleButton.Type.APPROVE);
        colorButton4.setWidth("100px");
        colorButtonPanel.add(colorButton4);
        colorButton4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(colorButton4, horizontalMargin);

        SimpleButton colorButton5 = new SimpleButton(Messages.i18n().tr("Кнопка неактивная"), SimpleButton.Type.APPROVE);
        colorButton5.setEnabled(false);
        colorButtonPanel.add(colorButton5);
        colorButton5.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(colorButton5, horizontalMargin);

        SimpleButton colorButton6 = new SimpleButton(Messages.i18n().tr("Кнопка неактивная"), SimpleButton.Type.DECLINE);
        colorButton6.setEnabled(false);
        colorButtonPanel.add(colorButton6);
        colorButton6.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(colorButton6, horizontalMargin);

        ContextMenuButton colorButton7 = new ContextMenuButton(Messages.i18n().tr("Кнопка с меню"), SimpleButton.Type.APPROVE);
        addCodeSample(colorButton7, Messages.i18n().tr("Кнопка подтверждения с меню"), ShowCase.RESOURCES.acceptButtonMenu().getText());
        colorButtonPanel.add(colorButton7);
        colorButton7.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(Messages.i18n().tr("Кнопка с меню была нажата!"));
            }
        });
        colorButton7.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(colorButton7, horizontalMargin);

        ContextMenu menu3 = createSimpleMenu();
        menu3.add(new MenuItem<Command>(null, Messages.i18n().tr("Очень-очень длинный текст")));
        colorButton7.setContextMenu(menu3);

        ArtaScrollPanel scroll = new ArtaScrollPanel();
        scroll.setWidget(colorButtonPanel);
        scroll.setHeight("100%");
        return scroll;
    }

    /**
     * Групповые кнопки
     */
    private Widget getGroupButton() {
        int horizontalMargin = 20;

        FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setPadding(10, Style.Unit.PX);

        final SimpleToggleButton toggleButton = new SimpleToggleButton(Messages.i18n().tr("Кнопка с длинным текстом"));
        addCodeSample(toggleButton, Messages.i18n().tr("Групповая кнопка"), ShowCase.RESOURCES.toggleButton().getText());
        setMarginLeft(toggleButton, horizontalMargin);
        toggleButton.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        panel.add(toggleButton);

        final SimpleToggleButton toggleButton1 = new SimpleToggleButton(Messages.i18n().tr("Не нажата"));
        toggleButton1.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (toggleButton1.isPressed()) {
                    toggleButton1.setText(Messages.i18n().tr("Нажата"));
                } else {
                    toggleButton1.setText(Messages.i18n().tr("Не нажата"));
                }
            }
        });
        toggleButton1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(toggleButton1, horizontalMargin);
        panel.add(toggleButton1);


        GroupButtonPanel groupButtonPanel = new GroupButtonPanel(true);
        addCodeSample(groupButtonPanel, Messages.i18n().tr("Группа кнопок"), ShowCase.RESOURCES.buttonGroupToggle().getText());
        groupButtonPanel.addButton(Messages.i18n().tr("Первая кнопка длинная"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            }
        });
        groupButtonPanel.addButton(Messages.i18n().tr("Вторая"), new ClickHandler() {
            @Override
                public void onClick(ClickEvent event) {
                if (((SimpleToggleButton) event.getSource()).isPressed()) {
                    ((SimpleToggleButton) event.getSource()).setText(Messages.i18n().tr("Вторая нажата"));
                } else {
                    ((SimpleToggleButton) event.getSource()).setText(Messages.i18n().tr("Вторая"));
                }
            }
        });
        groupButtonPanel.addButton(Messages.i18n().tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (((SimpleToggleButton) event.getSource()).isPressed()) {
                    ((SimpleToggleButton) event.getSource()).setText(Messages.i18n().tr("Третья нажата"));
                } else {
                    ((SimpleToggleButton) event.getSource()).setText(Messages.i18n().tr("Третья"));
                }
            }
        });
        groupButtonPanel.buildPanel();
        groupButtonPanel.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        groupButtonPanel.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        panel.add(groupButtonPanel);

        GroupButtonPanel groupButtonPanel1 = new GroupButtonPanel(true, true);
        addCodeSample(groupButtonPanel1, Messages.i18n().tr("Группа кнопок"), ShowCase.RESOURCES.buttonGroupMultiToggle().getText());
        groupButtonPanel1.addButton(Messages.i18n().tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel1.addButton(Messages.i18n().tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel1.addButton(Messages.i18n().tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel1.buildPanel();
        groupButtonPanel1.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(groupButtonPanel1, horizontalMargin);
        panel.add(groupButtonPanel1);

        GroupButtonPanel groupButtonPanel2 = new GroupButtonPanel(true);
        groupButtonPanel2.setAllowEmptyToggle(false);
        groupButtonPanel2.addButton(Messages.i18n().tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.addButton(Messages.i18n().tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.addButton(Messages.i18n().tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel2.buildPanel();
        groupButtonPanel2.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(groupButtonPanel2, horizontalMargin);
        panel.add(groupButtonPanel2);

        GroupButtonPanel groupButtonPanel3 = new GroupButtonPanel(true, true);
        groupButtonPanel3.addButton(Messages.i18n().tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.addButton(Messages.i18n().tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.addButton(Messages.i18n().tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel3.buildPanel();
        groupButtonPanel3.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(groupButtonPanel3, horizontalMargin);
        panel.add(groupButtonPanel3);

        GroupButtonPanel groupButtonPanel4 = new GroupButtonPanel();
        addCodeSample(groupButtonPanel4, Messages.i18n().tr("Группа кнопок"), ShowCase.RESOURCES.buttonGroup().getText());
        groupButtonPanel4.addButton(Messages.i18n().tr("Первая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.addButton(Messages.i18n().tr("Вторая"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.addButton(Messages.i18n().tr("Третья"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });
        groupButtonPanel4.buildPanel();
        groupButtonPanel4.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        setMarginLeft(groupButtonPanel4, horizontalMargin);
        panel.add(groupButtonPanel4);

        return panel;
    }

    private ContextMenu createSimpleMenu() {
        ContextMenu menu = new ContextMenu();
        menu.add(new MenuItem<Command>(null, "Zoom", ImageResources.IMPL.zoom()));
        menu.add(new MenuItem<Command>(null, "Left", ImageResources.IMPL.navigationLeft()));
        menu.add(new MenuItem<Command>(null, "Right", ImageResources.IMPL.navigationRight()));
        menu.addSeparator(1);
        return menu;
    }

    private void setTheme(Theme theme) {
        if (currentTheme != theme) {
            currentTheme = theme;
            Cookies.setCookie(THEME_COOKIE, theme.name());
            Window.Location.reload();
        }
    }
}
