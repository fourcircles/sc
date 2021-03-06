package kz.arta.synergy.components.style.client;

/**
 * User: vsl
 * Date: 04.07.14
 * Time: 9:48
 */
public class Constants {
    private static final String PX = "px";

    public static final int BORDER_WIDTH = 1;
    public static final int BORDER_RADIUS = 4;
    public static final int STD_SCROLL_WIDTH = 17;

    public static final int BUTTON_HEIGHT = 30;
    public static final int BUTTON_MIN_WIDTH = 30;
    public static final int BUTTON_APPROVE_MIN_WIDTH = 150;
    public static final int BUTTON_PADDING = 10;

    public static final int DIALOG_MIN_WIDTH = 116;
    public static final int DIALOG_TITLE_HEIGHT = 38;
    public static final int DIALOG_CONTENT_PADDING = 8;
    public static final int DIALOG_NAV_BUTTON_HMARGIN = 20;
    public static final int DIALOG_BUTTON_PANEL_HEIGHT = 72;
    public static final int DIALOG_BORDER_WIDTH = 1;
    public static final double DIALOG_NAV_BUTTON_VMARGIN = (DIALOG_BUTTON_PANEL_HEIGHT - BUTTON_HEIGHT - BORDER_WIDTH * 2) / 2;
    public static final int DIALOG_CLOSE_BUTTON_SIZE = 16;
    public static final int DIALOG_CLOSE_BUTTON_PADDING = 2;
    public static final int DIALOG_CLOSE_BUTTON_RIGHT_MARGIN = 10;
    public static final int DIALOG_TITLE_LEFT_MARGIN = 12;

    public static final int IMAGE_BUTTON_WIDTH = 30;
    public static final int STD_ICON_WIDTH = 16;

    public static final int COMMON_INPUT_PADDING = 8;

    /**
     * расстояние между заголовком и кнопкой "свернуть"
     */
    public static final int DIALOG_TITLE_LABEL_RIGHT_PADDING = 10;

    public static final int SCROLL_BAR_WIDTH = 17;

    public static final int SCROLL_BAR_HEIGHT = 17;

    public static final int COMBO_LIST_DEFAULT_HEIGHT = 200;
    public static final int FIELD_WITH_BUTTON_MIN_WIDTH = 200;

    public static final int DATE_INPUT_WIDTH = 57;

    public static final int WEEK_INPUT_WIDTH = 85;

    public static final int YEAR_LIST_WIDTH = 116;

    public static final int LIST_MAX_HEIGHT = 200;

    /**
     * Минимальная ширина текстового поля для ввода в поле с тэгами
     */
    public static final int TAG_INPUT_MIN_TEXT_WIDTH = 40;
    /**
     * Максимальная ширина тэга
     */
    public static final int TAG_INPUT_MAX_TAG_WIDTH = 100;
    public static final int TAG_INTERVAL = 2;
    public static final int HINTERVAL_BETWEEN_TAGS = 1;
    public static final int TAG_PADDING = 8;
    public static final int TAG_HEIGHT = 26;
    public static final int TAG_MAX_WIDTH = 100;
    /**
     * Максимальная ширина тега в индикаторе
     */
    public static final int INDICATOR_TAG_MAX_WIDTH = 200;
    public static final int TAG_INPUT_NO_BUTTON_MIN_WIDTH = 104;
    public static final int TAG_INDICATOR_PADDING = 6;

    /**
     * Максимальная ширина вкладки
     */
    public static final int TAB_MAX_WIDTH = 200;
    public static final int TAB_HEIGHT = 31;
    public static final int TAB_SCROLL_BUTTON_WIDTH = 19;

    public static final int STACK_HEIGHT = 48;

    public static final int TABLE_DIVIDER_WIDTH = 7;

    public static final int PAGER_BUTTON_WIDTH = 34;
    public static final int PAGER_TEXT_WIDTH = 91;
    public static final int PAGER_TEXT_PADDING = 5;
    public static final int PAGER_HEIGHT = 32;

    public static final int WEEKDAY_CHOOSER_PADDING = 8;

    public static final int COMMENT_INPUT_LINE_HEIGHT = 16;

    public static final int TASKBAR_ITEM_PADDING = 5;
    public static final int TASKBAR_IMAGE_MARGIN = 10;

    public static final int LINE_HEIGHT = 19;

    public static final int SLIDER_OUTERCIRCLE_RADIUS = 9;

    public static final int NOTIFICATION_ICON_SIZE = 26;
    public static final int NOTIFICATION_OFFSET = 16;

    public static String borderWidth() {
        return BORDER_WIDTH + PX;
    }
    public static String borderRadius() {
        return BORDER_RADIUS + PX;
    }

    public static String buttonHeight() {
        return BUTTON_HEIGHT + PX;
    }
    public static String buttonMinWidth() {
        return BUTTON_MIN_WIDTH + PX;
    }
    public static String buttonApproveMinWidth() {
        return BUTTON_APPROVE_MIN_WIDTH + PX;
    }
    public static String buttonPadding() {
        return BUTTON_PADDING + PX;
    }
    public static String gradientTopMargin() {
        return "-" + BUTTON_HEIGHT + PX;
    }


    public static String dialogNavigationButtonMargin() {
        return DIALOG_NAV_BUTTON_HMARGIN + PX;
    }
    public static String dialogBorderWidth() {
        return DIALOG_BORDER_WIDTH + PX;
    }
    public static String dialogButtonPanelHeight() {
        return DIALOG_BUTTON_PANEL_HEIGHT + PX;
    }
    public static String dialogNavigationButtonVMargin() {
        return DIALOG_NAV_BUTTON_VMARGIN + PX;
    }
    public static String dialogTitleHeight() {
        return DIALOG_TITLE_HEIGHT + PX;
    }
    public static String dialogCloseButtonSize() {
        return DIALOG_CLOSE_BUTTON_SIZE + PX;
    }
    public static String dialogCloseButtonPadding() {
        return DIALOG_CLOSE_BUTTON_PADDING + PX;
    }
    public static String dialogCloseButtonRightMargin() {
        return DIALOG_CLOSE_BUTTON_RIGHT_MARGIN + PX;
    }
    public static String dialogTitleLeftMargin() {
        return DIALOG_TITLE_LEFT_MARGIN + PX;
    }
    public static String dialogTitleLabelRightPadding() {
        return DIALOG_TITLE_LABEL_RIGHT_PADDING + PX;
    }
    public static String dialogContentPadding() {
        return DIALOG_CONTENT_PADDING + PX;
    }
    public static String dialogMinWidth() {
        return DIALOG_MIN_WIDTH + PX;
    }

    public static String imageButtonWidth() {
        return IMAGE_BUTTON_WIDTH + PX;
    }

    public static String scrollBarWidth() {
        return SCROLL_BAR_WIDTH + PX;
    }

    public static String commonInputPadding() {
        return COMMON_INPUT_PADDING + PX;
    }

    public static String comboMinWidth() {
        return FIELD_WITH_BUTTON_MIN_WIDTH + PX;
    }

    public static String listMaxHeight() {
        return LIST_MAX_HEIGHT + PX;
    }

    public static String tagHeight() {
        return TAG_HEIGHT + PX;
    }
    public static String tagMaxWidth() {
        return TAG_MAX_WIDTH + PX;
    }
    public static String tagInterval() {
        return TAG_INTERVAL + PX;
    }
    public static String tagIndicatorPadding() {
        return TAG_INDICATOR_PADDING + PX;
    }

    public static String dateInputWidth() {
        return DATE_INPUT_WIDTH + PX;
    }

    public static String yearListWidth() {
        return YEAR_LIST_WIDTH + PX;
    }

    public static String weekInputWidth() {
        return WEEK_INPUT_WIDTH + PX;
    }

    public static String tabMaxWidth() {
        return TAB_MAX_WIDTH + PX;
    }
    public static String tabHeight() {
        return TAB_HEIGHT + PX;
    }
    public static String tabScrollButtonWidth() {
        return TAB_SCROLL_BUTTON_WIDTH + PX;
    }

    public static String stackHeight() {
        return STACK_HEIGHT + PX;
    }

    public static String tableDividerWidth() {
        return TABLE_DIVIDER_WIDTH + PX;
    }

    public static String pagerButtonWidth() {
        return PAGER_BUTTON_WIDTH + PX;
    }
    public static String pagerTextWidth() {
        return PAGER_TEXT_WIDTH + PX;
    }
    public static String pagerTextPadding() {
        return PAGER_TEXT_PADDING + PX;
    }
    public static String pagerHeight() {
        return PAGER_HEIGHT + PX;
    }

    public static String weekdayChooserPadding() {
        return WEEKDAY_CHOOSER_PADDING + PX;
    }

    public static String commentInputCommentLineHeight() {
        return COMMENT_INPUT_LINE_HEIGHT + PX;
    }

    public static String taskbarItemPadding() {
        return TASKBAR_ITEM_PADDING + PX;
    }
    public static String taskbarImageMargin() {
        return TASKBAR_IMAGE_MARGIN + PX;
    }

    public static String notificationIconSize() {
        return NOTIFICATION_ICON_SIZE + PX;
    }

    public static String notificationOffset() {
        return NOTIFICATION_OFFSET + PX;
    }
}
