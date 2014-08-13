package kz.arta.synergy.components.client.util;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: vsl
 * Date: 24.07.14
 * Time: 17:59
 */
public class Utils {
    private static Label sizeLabel;

    /**
     * Возвращает ширина текста с заданным стилем
     * @param text текст
     * @param style стиль
     * @return ширина
     */
    public static int getTextWidth(String text, String style) {
        if (sizeLabel == null) {
            sizeLabel = new Label();
            sizeLabel.getElement().setId("ruler");
            Style labelStyle = sizeLabel.getElement().getStyle();
            labelStyle.setVisibility(Style.Visibility.HIDDEN);
            labelStyle.setPosition(Style.Position.FIXED);
            labelStyle.setTop(0, Style.Unit.PX);
            labelStyle.setLeft(0, Style.Unit.PX);
            labelStyle.setWhiteSpace(Style.WhiteSpace.NOWRAP);

            labelStyle.setPadding(0, Style.Unit.PX);
            labelStyle.setBorderWidth(0, Style.Unit.PX);
            RootPanel.get().add(sizeLabel);
        }
        sizeLabel.setText(text);
        sizeLabel.setStyleName(style);
        return sizeLabel.getOffsetWidth();
    }

    /**
     * Возвращает ширину текста элемента.
     * @param textWidget элемент с текстом и стилем
     * @return ширина текста
     */
    public static int getTextWidth(ArtaHasText textWidget) {
        return getTextWidth(textWidget.getText(), textWidget.getFontStyle());
    }
}
