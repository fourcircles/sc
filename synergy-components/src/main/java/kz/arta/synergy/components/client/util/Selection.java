package kz.arta.synergy.components.client.util;


import com.google.gwt.dom.client.Element;

/**
 * User: user
 * Date: 24.06.14
 * Time: 18:35
 */
public class Selection {

    private Selection() {
    }

    /**
     * Запрет на выделение текста в элементе
     * @param e     элемент
     */
    public native static void disableTextSelectInternal(Element e)
    /*-{
        if (typeof e.onselectstart!="undefined")
            e.onselectstart=function(){return false}
        else if (typeof e.style.MozUserSelect!="undefined")
            e.style.MozUserSelect="none"
        else //All other route (ie: Opera)
            e.onmousedown=function(){
                return false;
            }
    }-*/;

    /**
     * Разрешение на выделение текства в элементе
     * @param e     элемент
     */
    public native static void enableTextSelectInternal(Element e)
    /*-{
        if (typeof e.onselectstart!="undefined")
            e.onselectstart=null;
        else if (typeof e.style.MozUserSelect!="undefined")
            e.style.MozUserSelect="auto"
        else //All other route (ie: Opera)
            e.onmousedown=null;
    }-*/;

    public native static void focus(Element e)
    /*-{
        e.focus();
    }-*/;
}
