package kz.arta.synergy.components.client.util;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: user
 * Date: 06.10.14
 * Time: 10:54
 */
public class HeightRuler extends Label {

    HeightRuler() {
        super();
        Style labelStyle = getElement().getStyle();
        labelStyle.setVisibility(Style.Visibility.HIDDEN);
        labelStyle.setPosition(Style.Position.FIXED);
        labelStyle.setTop(0, Style.Unit.PX);
        labelStyle.setLeft(0, Style.Unit.PX);
        labelStyle.setProperty("wordWrap", "break-word");
        labelStyle.setPaddingTop(0, Style.Unit.PX);
        labelStyle.setPaddingBottom(0, Style.Unit.PX);
        labelStyle.setBorderWidth(0, Style.Unit.PX);
        RootPanel.get().add(this);
    }

    /**
     * получаем высоту
     * @param text  текст
     * @param fontStyle  стиль
     * @param width  ширина компонента
     * @return высота
     */
    public int getTextHeight(String text, String fontStyle, int width) {
        setWidth(width + "px");
        getElement().setInnerHTML(text.replaceAll("\n", "<br>"));
        setStyleName(fontStyle);
        return getOffsetHeight();
    }
}
