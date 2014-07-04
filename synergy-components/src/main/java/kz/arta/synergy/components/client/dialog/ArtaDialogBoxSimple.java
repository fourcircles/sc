package kz.arta.synergy.components.client.dialog;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel;
import kz.arta.synergy.components.client.resources.ImageResources;

/**
 * User: vsl
 * Date: 27.06.14
 * Time: 12:26
 */
public class ArtaDialogBoxSimple extends PopupPanel {
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

    public ArtaDialogBoxSimple(String title, Widget content) {
        setModal(true);

        panel = new FlowPanel();

        closeButton = new Image(ImageResources.IMPL.dialogCloseButton());
        closeButton.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        closeButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                closeButton.setResource(ImageResources.IMPL.dialogCloseButtonOver());
            }
        });
        closeButton.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                closeButton.setResource(ImageResources.IMPL.dialogCloseButton());
            }
        });
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });


        collapseButton = new Image(ImageResources.IMPL.dialogCollapseButton());
        collapseButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                collapseButton.setResource(ImageResources.IMPL.dialogCollapseButtonOver());

            }
        });
        collapseButton.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                collapseButton.setResource(ImageResources.IMPL.dialogCollapseButton());
            }
        });
        collapseButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                collapse();
            }
        });

        titlePanel = new FlowPanel();
        titleLabel = new GradientLabel(title);
        titleLabel.setWidth("10px");

        titlePanel.add(titleLabel);
        titlePanel.add(closeButton);
        titlePanel.add(collapseButton);

        titleLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
        titlePanel.setWidth("100%");

        this.content = content;
        contentPanel = new FlowPanel();
        contentPanel.add(content);

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
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        //TODO remove magic numbers
        titleLabel.setWidth((getWidth() - 50 - 12 - 8 - 10) + "px");
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
        MouseMoveHandler move = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (dragging) {
                    int absX = event.getX() + getAbsoluteLeft();
                    int absY = event.getY() + getAbsoluteTop();

                    setPopupPosition(absX - dragStartX, absY - dragStartY);
                }
            }
        };
        MouseUpHandler up = new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                dragging = false;
            }
        };

        titlePanel.addDomHandler(down, MouseDownEvent.getType());
        titlePanel.addDomHandler(move, MouseMoveEvent.getType());
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
        collapseButton.setResource(ImageResources.IMPL.dialogCollapseButton());
        closeButton.setResource(ImageResources.IMPL.dialogCloseButton());
        super.show();
    }

    public int getWidth() {
        return contentPanel.getOffsetWidth();
    }
}
