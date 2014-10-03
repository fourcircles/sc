package kz.arta.synergy.components.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;

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

    public static double getPreciseTextWidth(String text, String style) {
        return ruler.getPresiceTextWidth(text, style);
    }

    public static double getPreciseTextWidth(ArtaHasText textWidget) {
        return ruler.getPreciseTextWidth(textWidget);
    }

    /**
     * Предотвращает клик всеми кнопками мыши кроме левой.
     * Причина использования - некорректная работа метода getButton для {@link com.google.gwt.user.client.ui.RadioButton}
     * @param element элемент
     */
    public static native void cancelNonLeftButtons(com.google.gwt.dom.client.Element element) /*-{
        element.addEventListener('click', function(e) {
            var cancel = false;
            if (e.which != null) {
                if (e.which > 1) {
                    cancel = true;
                }
            } else {
                if (e.button > 0) {
                    cancel = true;
                }
            }
            if (cancel) {
                e.preventDefault();
                e.stopPropagation();
            }
        });
    }-*/;


    /**
     * Возвращает точную ширину (double) для элемента
     * @param element элемент
     * @return ширина
     */
    public static native double getPreciseWidth(Element element) /*-{
        return element.getBoundingClientRect().width;
    }-*/;

    /**
     * Заменяет все ссылки в тексте на элемент a.
     * @param text текст
     * @return html с добавленными элементами ссылок
     */
    public static native String parseComment(String text) /*-{
        var _link = document.createElement('a');
        return text.replace(/((http|https|ftp|ftps|smb|webdav|dav|notes):\/\/|#)[\w\/\.\?\:\=&]+/gim, function (match) {
            _link.href = match;
            _link.innerHTML = match;
            _link.target = "_blank";
            return _link.outerHTML;
        });
    }-*/;
}
