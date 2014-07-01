package kz.arta.synergy.components.client.button;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.MouseStyle;

/**
 * User: user
 * Date: 23.06.14
 * Time: 11:11
 * Кнопка простая
 */
public class SimpleButton extends ButtonBase {

    public SimpleButton() {
        super();
        init();
    }

    /**
     * Кпопка простая с текстом
     * @param text  текст кнопки
     */
    public SimpleButton(String text) {
        super();
        this.text = text;
        init();
    }

    /**
     * Кнопка с иконкой
     * @param text  текст кнопки
     * @param iconResource  иконка кнопки
     */
    public SimpleButton(String text, ImageResource iconResource) {
        super();
        this.text = text;
        this.iconResource = iconResource;
        init();
    }

    protected void init() {
        super.init();

        setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
        addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        gradient.setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleGradient());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        textLabel.setText(text);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        }
    }

    public void onBrowserEvent(Event event) {
        if (!enabled){
            return;
        }
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                MouseStyle.setPressed(this);
                MouseStyle.setPressed(gradient);
                break;
            case Event.ONMOUSEOVER:
                MouseStyle.setOver(this);
                MouseStyle.setOver(gradient);
                break;
            case Event.ONMOUSEUP:
                MouseStyle.setOver(this);
                MouseStyle.setOver(gradient);
                break;
            case Event.ONMOUSEOUT:
                MouseStyle.removeAll(this);
                MouseStyle.removeAll(gradient);
                break;
            default:
                super.onBrowserEvent(event);
        }
    }
}
