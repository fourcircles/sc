package kz.arta.synergy.components.client.button;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.resources.client.ImageResource;
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
        setWidth("32px");
        icon = new Image(iconResource.getSafeUri());
        icon.addDragStartHandler(new DragStartHandler() {
            @Override
            public void onDragStart(DragStartEvent event) {
                event.preventDefault();
            }
        });
        icon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        add(icon);
        setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
        Selection.disableTextSelectInternal(getElement());
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }
}
