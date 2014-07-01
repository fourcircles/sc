package kz.arta.synergy.components.client.button;

import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.Selection;

/**
 * User: user
 * Date: 30.06.14
 * Time: 16:33
 * Кнопка с пиктограммой без текста
 */
public class ImageButton extends ButtonBase {

    /**
     * Кнопка с пиктограммой без текста
     * @param iconResource  иконка кнопки
     */
    public ImageButton(ImageResource iconResource) {
        super();
        this.text = null;
        this.iconResource = iconResource;
        init();
    }

    protected void init() {
        width = 32;
        icon = new Image(iconResource.getSafeUri());
        icon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        add(icon);
        setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
        Selection.disableTextSelectInternal(getElement());
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    @Override
    public void onLoad() {
        setWidth(width);
    }

    public void onBrowserEvent(Event event) {
        if (!enabled){
            return;
        }
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                removeStyleName(SynergyComponents.resources.cssComponents().buttonSimpleOver());
                removeStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
                addStyleName(SynergyComponents.resources.cssComponents().buttonSimplePressed());
                break;
            case Event.ONMOUSEOVER:
                addStyleName(SynergyComponents.resources.cssComponents().buttonSimpleOver());
                removeStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
                removeStyleName(SynergyComponents.resources.cssComponents().buttonSimplePressed());
                break;
            case Event.ONMOUSEUP:
                addStyleName(SynergyComponents.resources.cssComponents().buttonSimpleOver());
                removeStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
                removeStyleName(SynergyComponents.resources.cssComponents().buttonSimplePressed());
                break;
            case Event.ONMOUSEOUT:
                removeStyleName(SynergyComponents.resources.cssComponents().buttonSimpleOver());
                removeStyleName(SynergyComponents.resources.cssComponents().buttonSimplePressed());
                addStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
                break;
            default:
                super.onBrowserEvent(event);

        }
    }

}
