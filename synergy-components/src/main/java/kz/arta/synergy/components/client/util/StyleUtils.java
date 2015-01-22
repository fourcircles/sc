package kz.arta.synergy.components.client.util;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: vsl
 * Date: 22.01.15
 * Time: 14:40
 */
public class StyleUtils {

    private static final String LINE_HEIGHT = "lineHeight";
    private static final String WHITE_SPACE = "whiteSpace";
    private static final String TEXT_ALIGN = "textAlign";

    private static String unitToString(Style.Unit unit) {
        switch (unit) {
            case PX :
                return "px";
            case PCT:
                return "%";
            case EM:
                return "em";
            default:
                return "px";
        }
    }
    
    public static void setLineHeight(Element element, int lineHeight, Style.Unit unit) {
//        getElement().getStyle().setLineHeight(lineHeight, Style.Unit.PX);
        element.getStyle().setProperty(LINE_HEIGHT, lineHeight + unitToString(unit));
    }
    
    public static void clearLineHeight(Element element) {
        element.getStyle().clearProperty(LINE_HEIGHT);
    }
    
    public static void setWhiteSpace(Element element, WhiteSpace whiteSpace) {
        //        getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        element.getStyle().setProperty(WHITE_SPACE, whiteSpace.toString());
    }
    
    public static void setTextAlign(Element element, TextAlign align) {
        element.getStyle().setProperty(TEXT_ALIGN, align.toString());
    }
    
    public static enum WhiteSpace {
        NOWRAP;

        @Override
        public String toString() {
            switch (this) {
                case NOWRAP:
                    return "nowrap";
                default:
                    return "inherit";
            }
        }
    }
    
    public static enum TextAlign {
        LEFT, RIGHT, CENTER, INITIAL;

        @Override
        public String toString() {
            switch (this) {
                case LEFT:
                    return "left";
                case RIGHT:
                    return "right";
                case CENTER:
                    return "center";
                case INITIAL:
                    return "initial";
                default:
                    return "initial";
            }
        }


    }
}
