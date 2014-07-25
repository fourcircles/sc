package kz.arta.synergy.components.client.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

/**
 * User: user
 * Date: 24.07.14
 * Time: 15:41
 */
public class WidthUtil {

    /**
     * Получаем ширину элемента
     * @param element   элемент
     * @return  ширина элемента
     */
    public static int getWidth(Element element) {
        Element e = DOM.clone(element, true);
        e.getStyle().setVisibility(Style.Visibility.HIDDEN);
        e.getStyle().setFloat(Style.Float.LEFT);
        e.setClassName(element.getClassName());
        Document.get().getBody().appendChild(e);
        int width = e.getClientWidth();
        Document.get().getBody().removeChild(e);
        return width;
    }
}
