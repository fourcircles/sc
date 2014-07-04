package kz.arta.synergy.components.style.client;

/**
 * User: vsl
 * Date: 04.07.14
 * Time: 9:48
 */
public class Constants {
    public static final int BUTTON_HEIGHT = 32;
    public static final int BUTTON_MIN_WIDTH = 32;
    public static final int BUTTON_APPROVE_MIN_WIDTH = 150;
    public static final int BUTTON_PADDING = 10;

    public static final int DIALOG_TITLE_HEIGHT = 32;
    public static final int DIALOG_CONTENT_PADDING = 8;
    public static final int DIALOG_NAV_BUTTON_HMARGIN = 20;
    public static final int DIALOG_BUTTON_PANEL_HEIGHT = 72;
    public static final int DIALOG_BORDER_WIDTH = 1;
    public static final double DIALOG_NAV_BUTTON_VMARGIN = (DIALOG_BUTTON_PANEL_HEIGHT - BUTTON_HEIGHT) / 2;
    public static final int DIALOG_CLOSE_BUTTON_SIZE = 16;
    public static final int DIALOG_CLOSE_BUTTON_PADDING = 4;
    public static final int DIALOG_CLOSE_BUTTON_RIGHT_MARGIN = 10;
    public static final int DIALOG_TITLE_LEFT_MARGIN = 12;

    /**
     * расстояние между заголовком и кнопкой "свернуть"
     */
    public static final int DIALOG_TITLE_LABEL_RIGHT_PADDING = 10;
    
    
    public static String buttonHeight() {
        return BUTTON_HEIGHT + "px";
    }
    public static String buttonMinWidth() {
        return BUTTON_MIN_WIDTH + "px";
    }
    public static String buttonApproveMinWidth() {
        return BUTTON_APPROVE_MIN_WIDTH + "px";
    }
    public static String buttonPadding() {
        return BUTTON_PADDING + "px";
    }
    public static String gradientTopMargin() {
        return "-" + BUTTON_HEIGHT + "px";
    }


    public static String dialogNavigationButtonMargin() {
        return DIALOG_NAV_BUTTON_HMARGIN + "px";
    }
    public static String dialogBorderWidth() {
        return DIALOG_BORDER_WIDTH + "px";
    }
    public static String dialogButtonPanelHeight() {
        return DIALOG_BUTTON_PANEL_HEIGHT + "px";
    }
    public static String dialogNavigationButtonVMargin() {
        return DIALOG_NAV_BUTTON_VMARGIN + "px";
    }
    public static String dialogTitleHeight() {
        return DIALOG_TITLE_HEIGHT + "px";
    }
    public static String dialogCloseButtonSize() {
        return DIALOG_CLOSE_BUTTON_SIZE + "px";
    }
    public static String dialogCloseButtonPadding() {
        return DIALOG_CLOSE_BUTTON_PADDING + "px";
    }
    public static String dialogCloseButtonRightMargin() {
        return DIALOG_CLOSE_BUTTON_RIGHT_MARGIN + "px";
    }
    public static String dialogTitleLeftMargin() {
        return DIALOG_TITLE_LEFT_MARGIN + "px";
    }
    public static String dialogTitleLabelRightPadding() {
        return DIALOG_TITLE_LABEL_RIGHT_PADDING + "px";
    }
    public static String dialogContentPadding() {
        return DIALOG_CONTENT_PADDING + "px";
    }
}
