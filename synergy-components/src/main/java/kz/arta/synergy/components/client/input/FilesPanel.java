package kz.arta.synergy.components.client.input;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.input.events.NewFilesEvent;
import kz.arta.synergy.components.client.menu.ContextMenu;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.client.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: vsl
 * Date: 03.11.14
 * Time: 12:38
 *
 * Панель комментариев
 */
public class FilesPanel extends Composite {
    /**
     * Кнопка для открытия контекстного меню
     */
    private final ImageButton downButton;

    /**
     * Контекстное меню. {@link #getMenu()} {@link #setMenu(kz.arta.synergy.components.client.menu.ContextMenu)}
     */
    private ContextMenu menu;

    /**
     * Элемент для аплода файлов
     */
    private final FileUpload upload;

    /**
     * Список добавленных элементов
     */
    private List<String> addedFiles = new ArrayList<String>();

    /**
     * Панель в скролле для отображения файлов
     */
    private final FlowPanel filesPanel;

    public FilesPanel() {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().filesPanel());
        root.addStyleName(SynergyComponents.getResources().cssComponents().mainText());

        upload = new FileUpload();
        upload.getElement().getStyle().setDisplay(Style.Display.NONE);
        upload.getElement().setAttribute("multiple", "true");
        root.add(upload);

        final SimpleButton button = new SimpleButton(Messages.i18n().tr("Нажмите для добавления файлов"), ImageResources.IMPL.zoom());
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Utils.impl().click(upload.getElement());
            }
        });
        root.add(button);

        Command showUpload = new Command() {
            @Override
            public void execute() {
                Utils.impl().click(upload.getElement());
            }
        };

        menu = new ContextMenu();
        menu.add(new MenuItem<Command>(null, Messages.i18n().tr("Из хранилища"), ImageResources.IMPL.zoom()));
        menu.add(new MenuItem<Command>(showUpload, Messages.i18n().tr("Из компьютера"), ImageResources.IMPL.zoom()));
        menu.add(new MenuItem<Command>(null, Messages.i18n().tr("Создать новый"), ImageResources.IMPL.zoom()));
        menu.add(new MenuItem<Command>(null, Messages.i18n().tr("Сканировать"), ImageResources.IMPL.zoom()));

        downButton = new ImageButton(ImageResources.IMPL.whiteButtonDropdown());
        downButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                imageButtonClick();
            }
        });
        root.add(downButton);
        menu.addAutoHidePartner(downButton.getElement());

        // правильное отображение границ кнопок при наведении
        downButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                button.getElement().getStyle().clearZIndex();
                downButton.getElement().getStyle().setZIndex(1);
            }
        });
        button.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                downButton.getElement().getStyle().clearZIndex();
                button.getElement().getStyle().setZIndex(1);
            }
        });

        filesPanel = new FlowPanel();
        filesPanel.setStyleName(SynergyComponents.getResources().cssComponents().files());
        ArtaScrollPanel scroll = new ArtaScrollPanel();
        scroll.setWidget(filesPanel);
        root.add(scroll);

        Style scrollStyle = scroll.getElement().getStyle();
        scrollStyle.setPosition(Style.Position.ABSOLUTE);
        scrollStyle.setTop(31, Style.Unit.PX);
        scrollStyle.setLeft(0, Style.Unit.PX);
        scrollStyle.setRight(0, Style.Unit.PX);
        scrollStyle.setBottom(0, Style.Unit.PX);

        upload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                filesSelected();
            }
        });
    }

    /**
     * Действия при выборе новых файлов пользователем
     */
    private void filesSelected() {
        boolean newFiles = false;
        for (String fileName : getSelectedFiles()) {
            if (!addedFiles.contains(fileName)) {
                addFile(fileName);
                newFiles = true;
            }
        }

        if (newFiles) {
            fireEvent(new NewFilesEvent());
        }
    }

    /**
     * Отображает новый файл
     * @param fileName имя файла
     */
    private void addFile(String fileName) {
        addedFiles.add(fileName);

        FlowPanel filePanel = new FlowPanel();
        filePanel.setStyleName(SynergyComponents.getResources().cssComponents().file());

        Image icon = new Image();
        icon.setResource(ImageResources.IMPL.folder());
        filePanel.add(icon);

        InlineLabel fileNameElement = new InlineLabel();
        fileNameElement.setText(fileName);
        filePanel.add(fileNameElement);

        filesPanel.add(filePanel);
    }

    /**
     * @param fullName абсолютный путь предоставленный браузером
     * @return непосредственное имя файла без пути
     */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    private String getFileName(String fullName) {
        if (fullName.contains(":")) {
            return fullName.substring(fullName.lastIndexOf("/") + 1);
        } else {
            return fullName.substring(fullName.lastIndexOf("\\") + 1);
        }
    }

    /**
     * @return выбранные пользователем файлы
     */
    private List<String> getSelectedFiles() {
        if (Navigator.isIE()) {
            return Arrays.asList(getFileName(upload.getFilename()));
        } else {
            List<String> result = new ArrayList<String>();
            for (int i = 0; i < getFilesCount(upload.getElement()); i++) {
                result.add(getFileName(getFileNameAt(upload.getElement(), i)));
            }
            return result;
        }
    }

    /**
     * @param upload элемент input
     * @return количество выбранных файлов
     */
    private native int getFilesCount(Element upload) /*-{
        "use strict";
        return upload.files.length;
    }-*/;

    /**
     * @param upload элемент input
     * @param index позиция файла
     * @return имя файла на заданной позиции
     */
    private native String getFileNameAt(Element upload, int index) /*-{
        "use strict";
        return upload.files[index].name;
    }-*/;

    /**
     * Действия при клике раскрытия контекстного меню
     */
    private void imageButtonClick() {
        if (menu.isShowing()) {
            menu.hide();
        } else {
            menu.show(downButton.getAbsoluteLeft(), downButton.getAbsoluteTop() + downButton.getOffsetHeight());
        }
    }

    /**
     * @return контекстное меню панели файлов
     */
    public ContextMenu getMenu() {
        return menu;
    }

    /**
     * Задает новое контекстное меню
     * @param menu контекстное меню
     */
    public void setMenu(ContextMenu menu) {
        this.menu = menu;
    }

    /**
     * @return файлы добавленные в панель файлов
     */
    @SuppressWarnings("UnusedDeclaration")
    public List<String> getAddedFiles() {
        return addedFiles;
    }

    @SuppressWarnings("UnusedDeclaration")
    public HandlerRegistration addNewFilesHandler(NewFilesEvent.Handler handler) {
        return addHandler(handler, NewFilesEvent.TYPE);
    }
}
