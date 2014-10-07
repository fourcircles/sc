package kz.arta.synergy.components.client.util;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: vsl
 * Date: 15.08.14
 * Time: 12:04
 *
 * Используется для измерения ширины текста
 */
public class RulerLabel extends Label {

    RulerLabel() {
        super();
        Style labelStyle = getElement().getStyle();
        labelStyle.setVisibility(Style.Visibility.HIDDEN);
        labelStyle.setPosition(Style.Position.FIXED);
        labelStyle.setTop(0, Style.Unit.PX);
        labelStyle.setLeft(0, Style.Unit.PX);
        labelStyle.setWhiteSpace(Style.WhiteSpace.NOWRAP);

        labelStyle.setPadding(0, Style.Unit.PX);
        labelStyle.setBorderWidth(0, Style.Unit.PX);
        RootPanel.get().add(this);
    }

    private void updateLabel(String text, String fontStyle) {
        setText(text);
        setStyleName(fontStyle);
    }

    public int getTextWidth(ArtaHasText textElement) {
        return getTextWidth(textElement.getText(), textElement.getFontStyle());
    }

    public int getTextWidth(String text, String fontStyle) {
        updateLabel(text, fontStyle);
        return getOffsetWidth();
    }

    public double getPresiceTextWidth(String text, String fontStyle) {
        updateLabel(text, fontStyle);
        return Utils.impl().getPreciseWidth(getElement());
    }

    public double getPreciseTextWidth(ArtaHasText textElement) {
        return getPresiceTextWidth(textElement.getText(), textElement.getFontStyle());
    }
}
