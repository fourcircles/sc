package kz.arta.synergy.components.client.util;

import com.google.gwt.core.client.GWT;

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
}
