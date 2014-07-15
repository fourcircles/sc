package kz.arta.synergy.components.client.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.client.util.MouseStyle;
import kz.arta.synergy.components.client.util.Selection;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: user
 * Date: 23.06.14
 * Time: 18:20
 * Базовый класс для кнопок
 */
public class ButtonBase extends FlowPanel implements HasClickHandlers, HasFocusHandlers, HasEnabled{

    private final IconPosition DEFAULT_ICON_POSITION = IconPosition.LEFT;

    /**
     * Активна ли кнопка
     */
    protected boolean enabled = true;

    /**
     * Панель для текста с иконкой
     */
    protected FlowPanel textPanel = GWT.create(FlowPanel.class);

    /**
     * Надпись кнопки
     */
    protected GradientLabel textLabel;

    /**
     * Текст кнопки
     */
    protected String text = Messages.i18n.tr("Кнопка");

    /**
     * Иконка
     */
    protected ImageResource iconResource;

    protected Image icon;

    private Integer customWidth;

    public enum IconPosition {
        LEFT, RIGHT;
    }

    protected IconPosition iconPosition = IconPosition.LEFT;

    protected ButtonBase() {
        textLabel = GWT.create(GradientLabel.class);
        icon = GWT.create(Image.class);
        buildButton();
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    public ButtonBase(String text) {
        this(text, null, null);
    }

    public ButtonBase(ImageResource iconResource) {
        this(null, iconResource, null);
    }

    public ButtonBase(String text, ImageResource iconResource) {
        this(text, iconResource, null);
    }

    public ButtonBase(String text, ImageResource iconResource, IconPosition iconPosition) {
        setTextInternal(text);
        setIconInternal(iconResource);
        if (iconPosition != null) {
            this.iconPosition = iconPosition;
        }
        buildButton();

        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    private void buildButton() {
        clear();
        if (iconPosition == IconPosition.RIGHT) {
            if (textLabel != null) {
                add(textLabel);
            }
            if (icon != null) {
                add(icon);
                icon.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
                icon.getElement().getStyle().setMarginRight(Constants.BUTTON_PADDING, Style.Unit.PX);
            }
        } else {
            if (icon != null) {
                add(icon);
                icon.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
                icon.getElement().getStyle().setMarginLeft(Constants.BUTTON_PADDING, Style.Unit.PX);
            }
            if (textLabel != null) {
                add(textLabel);
            }
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (icon != null) {
            icon.getElement().getStyle().setMarginTop(8, Style.Unit.PX);
        }
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                adjustMargins();
            }
        });
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addHandler(handler, FocusEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        }

        if (icon != null) {
            if (enabled) {
                icon.getElement().getStyle().setOpacity(1);
            } else {
                icon.getElement().getStyle().setOpacity(0.5);
            }
        }
    }

    public String getText() {
        return text;
    }

    private boolean setTextInternal(String text) {
        boolean needRebuild = false;
        if (text == null) {
            return false;
        }
        this.text = text;
        if (textLabel == null) {
            needRebuild = true;
            textLabel = GWT.create(GradientLabel.class);
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().buttonText());

            textLabel.getElement().getStyle().setMarginRight(Constants.BUTTON_PADDING, Style.Unit.PX);
            textLabel.getElement().getStyle().setMarginLeft(Constants.BUTTON_PADDING, Style.Unit.PX);

            Selection.disableTextSelectInternal(getElement());
        }
        textLabel.setText(text);
        return needRebuild;
    }

    public void setText(String text) {
        if (setTextInternal(text)) {
            buildButton();
        }
    }

    private boolean setIconInternal(ImageResource iconResource) {
        boolean needRebuild = false;
        if (iconResource == null) {
            return false;
        }
        this.iconResource = iconResource;
        if (icon == null) {
            needRebuild = true;
            icon = GWT.create(Image.class);
            icon.addDragStartHandler(new DragStartHandler() {
                @Override
                public void onDragStart(DragStartEvent event) {
                    event.preventDefault();
                }
            });
            icon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        }
        icon.setUrl(iconResource.getSafeUri());
        return needRebuild;
    }

    public void setIcon(ImageResource iconResource) {
        if (setIconInternal(iconResource)) {
            buildButton();
        }
    }

    public void setIconPosition(IconPosition position) {
        if (position == null) {
            return;
        }
        if (iconPosition != position) {
            iconPosition = position;
            buildButton();
        }
    }

    public void onBrowserEvent(Event event) {
        if (!enabled){
            return;
        }
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                MouseStyle.setPressed(this);
                break;
            case Event.ONMOUSEOVER:
                MouseStyle.setOver(this);
                break;
            case Event.ONMOUSEUP:
                MouseStyle.setOver(this);
                break;
            case Event.ONMOUSEOUT:
                MouseStyle.removeAll(this);
                break;
        }
        super.onBrowserEvent(event);
    }

    protected int getTextLabelWidth() {
        int textLabelWidth = getOffsetWidth() - 2 * Constants.BUTTON_PADDING;
        if (icon != null) {
            textLabelWidth -= icon.getWidth();
            textLabelWidth -= Constants.BUTTON_PADDING;
        }
        return textLabelWidth;
    }

    protected void adjustMargins() {
        if (!isAttached()) {
            return;
        }
        if (textLabel != null) {
            int width = getTextLabelWidth();
            if (width < textLabel.getOffsetWidth()) {
                textLabel.setWidth(width + "px");
            }
        }
    }

    @Override
    public void setWidth(String width) {
        if (!width.substring(width.length() - 2, width.length()).equals("px")) {
            throw new IllegalArgumentException();
        }
        int intWidth = Integer.parseInt(width.substring(0, width.length() - 2));
        customWidth = Math.max(getMinWidth(), intWidth);
        super.setWidth(customWidth + "px");
        adjustMargins();
    }

    protected int getMinWidth() {
        return Constants.BUTTON_MIN_WIDTH;
    }

    public HasClickHandlers getButton() {
        return this;
    }
}
