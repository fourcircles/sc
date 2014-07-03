package kz.arta.synergy.components.client.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
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

/**
 * User: user
 * Date: 23.06.14
 * Time: 18:20
 * Базовый класс для кнопок
 */
public class ButtonBase extends FlowPanel implements HasClickHandlers, HasFocusHandlers, HasEnabled {

    private static final int PADDING = 10;
    private final int MIN_WIDTH = 150;
    private final int MIN_WIDTH_NO_TEXT = 32;
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
    protected GradientLabel textLabel = GWT.create(GradientLabel.class);

    /**
     * Текст кнопки
     */
    protected String text = Messages.i18n.tr("Кнопка");

    /**
     * Иконка
     */
    protected ImageResource iconResource;

    protected Image icon;

    public enum IconPosition {
        LEFT, RIGHT;
    }

    protected IconPosition iconPosition = null;

    protected void init() {
        textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        textLabel.addStyleName(SynergyComponents.resources.cssComponents().buttonText());

        if (iconResource != null) {
            icon = new Image(iconResource.getSafeUri());
            icon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
            setIconPosition(iconPosition == null ? DEFAULT_ICON_POSITION : iconPosition, true);
        } else {
            add(textLabel);
        }
        textLabel.getElement().getStyle().setMarginRight(PADDING, Style.Unit.PX);
        textLabel.getElement().getStyle().setMarginLeft(PADDING, Style.Unit.PX);
        textLabel.setText(text);

        Selection.disableTextSelectInternal(getElement());
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    public void setIconPosition(IconPosition position) {
        setIconPosition(position, false);
    }

    private void setIconPosition(IconPosition position, boolean init) {
        if (init || (iconPosition != position & icon != null)) {
            iconPosition = position;
            clear();
            if (iconPosition == IconPosition.RIGHT) {
                add(textLabel);
                add(icon);
                icon.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
                icon.getElement().getStyle().setMarginRight(PADDING, Style.Unit.PX);
            } else {
                add(icon);
                add(textLabel);
                icon.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
                icon.getElement().getStyle().setMarginLeft(PADDING, Style.Unit.PX);
            }
        }
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

    public void setSizeCallback(Command callback) {
        textLabel.setSizeCallback(callback);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        textLabel.setText(text);
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
            default:
                super.onBrowserEvent(event);

        }
    }

    private void setWidth(int width) {
        int textLabelWidth = width;
        if (icon != null) {
            textLabelWidth -= icon.getWidth();
            textLabelWidth -= PADDING;
        }
        textLabel.setWidth(textLabelWidth + "px");
    }

    @Override
    public void setWidth(String width) {
        if (!width.substring(width.length() - 2, width.length()).equals("px")) {
            throw new IllegalArgumentException();
        }
        int intWidth = Integer.parseInt(width.substring(0, width.length() - 2));
        intWidth = Math.max(getMinWidth(), intWidth);
        super.setWidth(intWidth + "px");
        setWidth(intWidth - 2 * PADDING);
    }

    private int getMinWidth() {
        if (text == null){
            return MIN_WIDTH_NO_TEXT;
        } else {
            return MIN_WIDTH;
        }
    }
}
