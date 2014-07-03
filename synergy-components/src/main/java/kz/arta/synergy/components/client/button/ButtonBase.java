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

    protected void init() {
        textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        textPanel.addStyleName(SynergyComponents.resources.cssComponents().buttonText());

        if (iconResource != null) {
            icon = new Image(iconResource.getSafeUri());
            icon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
            textPanel.add(icon);
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().paddingElement());
        }
        textLabel.setText(text);

        textPanel.add(textLabel);
        add(textPanel);

        Selection.disableTextSelectInternal(getElement());
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
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
}
