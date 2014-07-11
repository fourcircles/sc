package kz.arta.synergy.components.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.util.Selection;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 09.07.14
 * Time: 15:14
 */
public class ContextMenu extends PopupPanel {
    protected FlowPanel panel;

    protected ArrayList<ContextMenuItem> items;

    protected Widget relativeWidget;

    public ContextMenu() {
        super(true);
        panel = GWT.create(FlowPanel.class);
        setStyleName(SynergyComponents.resources.cssComponents().contextMenu());
        items = new ArrayList<ContextMenuItem>();
        setWidget(panel);
    }

    public ContextMenu(Widget relativeWidget) {
        this();
        this.relativeWidget = relativeWidget;
    }

    private void addItem(ContextMenuItem item) {
        items.add(item);
        panel.add(item.asWidget());
    }

    public void addItem(String text, Command command) {
        addItem(new ContextMenuItem(text, command));
    }

    public void addItem(String text, ImageResource imageResource, Command command) {
        addItem(new ContextMenuItem(text, imageResource, command));
    }

    public void addSeparator() {
        addItem(ContextMenuItem.getSeparator());
    }

    public void insertItem(int index, String text, Command command) {
        items.add(index, new ContextMenuItem(text, command));
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public String getText(int index) {
        return items.get(index).getText();
    }

    public void setItem(int index, String text, Command command) {
        items.set(index, new ContextMenuItem(text, command));
    }

    public void clearItems() {
        items.clear();
        panel.clear();
    }

    public void setRelativeWidget(Widget widget) {
        if (relativeWidget != null) {
            removeAutoHidePartner(relativeWidget.getElement());
        }
        relativeWidget = widget;
        addAutoHidePartner(relativeWidget.getElement());

        getElement().getStyle().setProperty("borderTop", "0px");
    }

    public void smartShow(int posX, int posY) {
        show();
        int lenX = getOffsetWidth();
        int lenY = getOffsetHeight();

        if (posX + lenX > Window.getClientWidth()) {
            posX -= lenX;
        }
        if (posY + lenY > Window.getClientHeight()) {
            posY -= lenY;
        }

        setPopupPosition(posX, posY);
    }

    public void showUnderParent() {
        if (relativeWidget != null && relativeWidget.isAttached()) {
            getElement().getStyle().setProperty("minWidth", relativeWidget.getOffsetWidth() - 8 + "px");

            int x = relativeWidget.getAbsoluteLeft() + 4;
            int y = relativeWidget.getAbsoluteTop() + relativeWidget.getOffsetHeight();
            setPopupPosition(x, y);
        } else {
            getElement().getStyle().setProperty("minWidth", "");
        }
        show();
    }

    private static class ContextMenuItem implements IsWidget{
        private String text;
        private Command command;
        private ImageResource imageResource;

        protected FlowPanel itemPanel;

        public static ContextMenuItem getSeparator() {
            FlowPanel separatorPanel = new FlowPanel();
            separatorPanel.setStyleName(SynergyComponents.resources.cssComponents().menuSeparator());
            return new ContextMenuItem(separatorPanel) {
                @Override
                public Widget asWidget() {
                    return this.itemPanel;
                }
            };
        }

        private ContextMenuItem(FlowPanel itemPanel) {
            this.itemPanel = itemPanel;
        }

        public ContextMenuItem(String text, Command command) {
            this.text = text;
            this.command = command;
        }

        public ContextMenuItem(String text, ImageResource imgResource, Command command) {
            this(text, command);
            this.imageResource = imgResource;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Command getCommand() {
            return command;
        }

        public void setCommand(Command command) {
            this.command = command;
        }

        @Override
        public Widget asWidget() {
            if (itemPanel == null) {
                itemPanel = new FlowPanel();
                MouseOverHandler over = new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        itemPanel.addStyleName(SynergyComponents.resources.cssComponents().over());
                    }
                };
                MouseOutHandler out = new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        itemPanel.removeStyleName(SynergyComponents.resources.cssComponents().over());
                    }
                };
                itemPanel.addDomHandler(over, MouseOverEvent.getType());
                itemPanel.addDomHandler(out, MouseOutEvent.getType());

                Label label = new Label(text);
                Selection.disableTextSelectInternal(label.getElement());
                label.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
                label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

                if (imageResource != null) {
                    Image image = GWT.create(Image.class);
                    image.setResource(imageResource);
                    image.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
                    itemPanel.add(image);
                    image.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
                }

                itemPanel.add(label);
                itemPanel.addStyleName(SynergyComponents.resources.cssComponents().contextMenuItem());
            }

            return itemPanel;
        }
    }


    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        Event nativeEvent = Event.as(event.getNativeEvent());
        if (nativeEvent.getTypeInt() == Event.ONMOUSEWHEEL) {
            hide();
        }
        super.onPreviewNativeEvent(event);
    }
}
