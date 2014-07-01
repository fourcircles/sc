package kz.arta.synergy.components.client.util;

import com.google.gwt.user.client.ui.UIObject;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 30.06.14
 * Time: 16:53
 */
public enum MouseStyle {
    OVER(SynergyComponents.resources.cssComponents().pressed()),
    PRESSED(SynergyComponents.resources.cssComponents().over());

    private String styleName;

    MouseStyle(String styleName) {
        this.styleName = styleName;
    }

    public static void removeAll(UIObject o) {
        for (MouseStyle style : values()) {
            o.removeStyleName(style.styleName);
        }
    }

    public static void setPressed(UIObject o) {
        removeAll(o);
        o.addStyleName(PRESSED.styleName);
    }

    public static void setOver(UIObject o) {
        removeAll(o);
        o.addStyleName(OVER.styleName);
    }
}
