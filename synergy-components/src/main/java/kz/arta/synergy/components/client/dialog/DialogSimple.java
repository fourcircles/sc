package kz.arta.synergy.components.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 27.06.14
 * Time: 12:26
 */
public class DialogSimple extends PopupPanel {
    /**
     * панель для диалога
     */
    protected FlowPanel panel;

    /**
     * панель для заголовка
     */
    protected FlowPanel titlePanel;

    /**
     * кнопка закрытия диалога
     */
    protected Image closeButton;

    /**
     * кнопка сворачивания диалога
     */
    protected Image collapseButton;


    /**
     * панель для контента
     */
    protected FlowPanel contentPanel;

    private boolean modal = true;

    protected Widget content;

    /**
     * флаг для определения происходит ли в данный момент drag
     */
    private boolean dragging = false;

    /**
     * координаты (относительно заголовка диалога) начала drag
     */
    private int dragStartX, dragStartY;

    /**
     * Текст заголовка
     */
    private GradientLabel titleLabel;

    /**
     * Производит кнопки для верхнего правого угла диалога, присваивая хэндлеры для событий мыши.
     * Также предотвращает перетаскивание картинки кнопки (например в адресную строку браузера).
     * @param simple картинка для кнопки
     * @param over картинка для кнопки при наведении мыши
     * @return кнопка
     */
    protected Image makeTitleButton(final ImageResource simple, final ImageResource over) {
        final Image button = GWT.create(Image.class);
        button.setResource(simple);
        button.addDragStartHandler(new DragStartHandler() {
            @Override
            public void onDragStart(DragStartEvent event) {
                event.preventDefault();
            }
        });
        button.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                button.setResource(over);
            }
        });
        button.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                button.setResource(simple);
            }
        });
        return button;
    }

    public DialogSimple() {
        this(true);
    }

    public DialogSimple(boolean modal) {

        this.modal = modal;
        setModal(this.modal);

        if (this.modal) {
            setGlassEnabled(true);
        }
        panel = GWT.create(FlowPanel.class);

        closeButton = makeTitleButton(ImageResources.IMPL.dialogCloseButton(), ImageResources.IMPL.dialogCloseButtonOver());
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        collapseButton = makeTitleButton(ImageResources.IMPL.dialogCollapseButton(), ImageResources.IMPL.dialogCollapseButtonOver());
        collapseButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                collapse();
            }
        });

        titlePanel = GWT.create(FlowPanel.class);
        titleLabel = GWT.create(GradientLabel.class);
        titleLabel.setWidth(10);

        titlePanel.add(titleLabel);
        titlePanel.add(closeButton);
        titlePanel.add(collapseButton);

        titlePanel.setWidth("100%");

        contentPanel = GWT.create(FlowPanel.class);

        panel.add(titlePanel);
        panel.add(contentPanel);

        setWidget(panel);

        this.setStyleName(SynergyComponents.resources.cssComponents().popupPanel());
        panel.setStyleName(SynergyComponents.resources.cssComponents().dialog());
        titlePanel.setStyleName(SynergyComponents.resources.cssComponents().dialogTitle());
        titleLabel.setStyleName(SynergyComponents.resources.cssComponents().dialogTitleLabel());
        closeButton.setStyleName(SynergyComponents.resources.cssComponents().dialogTitleButton());
        collapseButton.setStyleName(SynergyComponents.resources.cssComponents().dialogTitleButton());
        contentPanel.setStyleName(SynergyComponents.resources.cssComponents().dialogContent());

        setUpDragging();

        setPreviewingAllNativeEvents(true);

        getGlassElement().getStyle().setZIndex(1000);
        getElement().getStyle().setZIndex(2000);
    }

    public DialogSimple(String title, Widget content) {
        this();
        setText(title);
        setContent(content);
    }

    /**
     * Считается ширина для текста заголовка.
     */
    void adjustTitleLabelWidth() {
        int textWidth = getWidth();
        textWidth -= 2 * (Constants.DIALOG_CLOSE_BUTTON_SIZE + Constants.DIALOG_CLOSE_BUTTON_PADDING * 2);
        textWidth -= Constants.DIALOG_CLOSE_BUTTON_RIGHT_MARGIN;
        textWidth -= Constants.DIALOG_TITLE_LEFT_MARGIN + Constants.DIALOG_TITLE_LABEL_RIGHT_PADDING;

        if (Window.Navigator.getAppVersion().contains("MSIE") || Window.Navigator.getAppVersion().contains("Trident")) {
            textWidth -= 2;
        }
        titleLabel.setWidth(textWidth);
    }

    /**
     * Перемещает диалог, учитывая текущее положение мыши и координаты относительно левого-верхнего угла заголовка,
     * по которым был начат drag.
     * @param mouseX x-координата мыши
     * @param mouseY y-координата мыши
     */
    private void moveDialog(int mouseX, int mouseY) {
        int x = mouseX - dragStartX;
        int y = mouseY - dragStartY;

        x = Math.max(0, x);
        y = Math.max(0, y);

        setPopupPosition(x, y);
    }

    /**
     * Создание обработчиков событий мыши для начала и конца drag-n-drop диалога.
     */
    private void setUpDragging() {
        MouseDownHandler down = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                dragging = true;
                dragStartX = event.getX();
                dragStartY = event.getY();
                Event.setCapture(titlePanel.getElement());
            }
        };
        MouseUpHandler up = new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                dragging = false;
                Event.releaseCapture(titlePanel.getElement());
            }
        };
        MouseMoveHandler move = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (dragging) {
                    event.preventDefault();
                    moveDialog(event.getClientX(), event.getClientY());
                }
            }
        };

        titlePanel.addDomHandler(down, MouseDownEvent.getType());
        titlePanel.addDomHandler(up, MouseUpEvent.getType());
        titlePanel.addDomHandler(move, MouseMoveEvent.getType());
    }

    protected void collapse() {
    }

    @Override
    public void show() {
        super.show();
        //reset картинок
        collapseButton.setResource(ImageResources.IMPL.dialogCollapseButton());
        closeButton.setResource(ImageResources.IMPL.dialogCloseButton());
        adjustTitleLabelWidth();
    }

    public int getWidth() {
        return Math.max(getOffsetWidth() - Constants.BORDER_WIDTH * 2, Constants.DIALOG_MIN_WIDTH);
    }

    public String getText() {
        return titleLabel.getText();
    }

    public void setText(String text) {
        titleLabel.setText(text);
    }

    public void setContent(Widget content) {
        contentPanel.clear();
        contentPanel.add(content);
        adjustTitleLabelWidth();
    }


}
