package kz.arta.synergy.components.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.useragent.client.UserAgent;

/**
 * User: vsl
 * Date: 24.07.14
 * Time: 17:59
 */
public class Utils {
    private static final RulerLabel ruler = GWT.create(RulerLabel.class);

    /**
     * Возвращает ширина текста с заданным стилем
     * @param text текст
     * @param style стиль
     * @return ширина
     */
    public static int getTextWidth(String text, String style) {
        return ruler.getTextWidth(text, style);
    }

    /**
     * Возвращает ширину текста элемента.
     * @param textWidget элемент с текстом и стилем
     * @return ширина текста
     */
    public static int getTextWidth(ArtaHasText textWidget) {
        return ruler.getTextWidth(textWidget);
    }

    public static void setRotate(Element element, int degrees) {

        String degreesStr = "rotate(" + degrees + ")";
        System.out.println(degreesStr);
        if (Window.Navigator.getUserAgent().toLowerCase().contains("msie")) {
            element.getStyle().setProperty("MsTransform", degreesStr);
        } else if (Navigator.isChrome) {
            element.getStyle().setProperty("WebkitTransform", degreesStr);
        } else {
            element.getStyle().setProperty("transform", degreesStr);
        }
    }
}
