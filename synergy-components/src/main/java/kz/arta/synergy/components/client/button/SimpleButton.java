package kz.arta.synergy.components.client.button;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import kz.arta.synergy.components.client.SynergyComponents;

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
        gradient.setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleGradient());
    }



    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
        } else {
            setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleDisabled());
        }
    }

    public void onBrowserEvent(Event event) {
        if (!enabled){
            return;
        }
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                setStyleName(SynergyComponents.resources.cssComponents().buttonSimplePressed());
                addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
                gradient.setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleGradientPressed());
                break;
            case Event.ONMOUSEOVER:
                setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleOver());
                addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
                gradient.setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleGradientOver());
                break;
            case Event.ONMOUSEUP:
                setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleOver());
                addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
                gradient.setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleGradientOver());
                break;
            case Event.ONMOUSEOUT:
                setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
                addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
                gradient.setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleGradient());
                break;
            default:
                super.onBrowserEvent(event);

        }
    }


}
