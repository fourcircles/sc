package kz.arta.synergy.components.client.dialog;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 27.06.14
 * Time: 12:26
 */
public class ArtaDialogBoxSimple extends PopupPanel {
    public static final int TITLE_HEIGHT = 60;

    public FlowPanel panel;

    protected FlowPanel titlePanel;
    protected Button closeButton;

    protected FlowPanel contentPanel;

    private boolean dragging = false;
    private int dragStartX, dragStartY;

    public ArtaDialogBoxSimple(String title, Widget content) {
        setModal(true);

        panel = new FlowPanel();

        titlePanel = new FlowPanel();
        Label titleLabel = new Label(title);

        closeButton = new Button("X");
        titlePanel.add(titleLabel);
        titlePanel.add(closeButton);

        titleLabel.getElement().getStyle().setFloat(Style.Float.LEFT);
        closeButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
        titlePanel.setWidth("100%");

        contentPanel = new FlowPanel();
        contentPanel.add(content);

        panel.add(titlePanel);
        panel.add(contentPanel);

        setWidget(panel);

        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        this.setStyleName(SynergyComponents.resources.cssComponents().popupPanel());
        panel.setStyleName(SynergyComponents.resources.cssComponents().dialog());
        titlePanel.setStyleName(SynergyComponents.resources.cssComponents().dialogTitle());
        titleLabel.setStyleName(SynergyComponents.resources.cssComponents().dialogTitleLabel());
        closeButton.setStyleName(SynergyComponents.resources.cssComponents().dialogTitleButton());
        contentPanel.setStyleName(SynergyComponents.resources.cssComponents().dialogContent());

        setUpDragging();
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

}
