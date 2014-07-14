package kz.arta.synergy.components.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
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

    protected Widget content;

    private boolean dragging = false;
    private int dragStartX, dragStartY;
    private GradientLabel titleLabel;

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
        setModal(true);
        panel = GWT.create(FlowPanel.class);

        closeButton = makeTitleButton(ImageResources.IMPL.dialogCloseButton(), ImageResources.IMPL.dialogCloseButtonOver());
        closeButton.getElement().getStyle().setMarginRight(Constants.DIALOG_CLOSE_BUTTON_RIGHT_MARGIN, Style.Unit.PX);
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
        titleLabel.setWidth("10px");

        titlePanel.add(titleLabel);
        titlePanel.add(closeButton);
        titlePanel.add(collapseButton);

        titleLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
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
    }

    public DialogSimple(String title, Widget content) {
        this();
        setText(title);
        setContent(content);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        adjustTitleLabelWidth();
    }

    void adjustTitleLabelWidth() {
        titleLabel.setWidth((getWidth() -
                (Constants.DIALOG_CLOSE_BUTTON_SIZE + Constants.DIALOG_CLOSE_BUTTON_PADDING) * 2 -
                Constants.DIALOG_CLOSE_BUTTON_RIGHT_MARGIN) -
                Constants.DIALOG_TITLE_LEFT_MARGIN -
                Constants.DIALOG_TITLE_LABEL_RIGHT_PADDING -
                Constants.DIALOG_CONTENT_PADDING * 2
                + "px");
    }

    private void moveDialog(int mouseX, int mouseY) {
        int x = mouseX - dragStartX;
        int y = mouseY - dragStartY;

        x = Math.max(0, x);
        y = Math.max(0, y);

        setPopupPosition(x, y);
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent nativeEvent) {
        if (dragging && nativeEvent.getTypeInt() == Event.ONMOUSEMOVE) {
            Event event = Event.as(nativeEvent.getNativeEvent());
            moveDialog(event.getClientX(), event.getClientY());
        }
        super.onPreviewNativeEvent(nativeEvent);
    }

    private void setUpDragging() {
        MouseDownHandler down = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                dragging = true;
                dragStartX = event.getX();
                dragStartY = event.getY();
            }
        };
        MouseUpHandler up = new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                dragging = false;
            }
        };

        titlePanel.addDomHandler(down, MouseDownEvent.getType());
        titlePanel.addDomHandler(up, MouseUpEvent.getType());
    }

    @Override
    public void hide() {
        closeButton.setResource(ImageResources.IMPL.dialogCloseButton());
        super.hide();
    }

    protected void collapse() {
    }

    @Override
    public void show() {
        //reset картинок
        super.show();
        collapseButton.setResource(ImageResources.IMPL.dialogCollapseButton());
        closeButton.setResource(ImageResources.IMPL.dialogCloseButton());
        adjustTitleLabelWidth();
    }

    public int getWidth() {
        return Math.max(getOffsetWidth(), Constants.DIALOG_MIN_WIDTH);
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