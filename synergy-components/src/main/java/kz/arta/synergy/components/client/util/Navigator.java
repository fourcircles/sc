package kz.arta.synergy.components.client.util;

import com.google.gwt.user.client.Window;

/**
 * User: user
 * Date: 25.07.14
 * Time: 16:48
 */
public class Navigator {
    private static final String IE_TOKEN = "msie";
    private static final String IE11_TOKEN = "trident";
    private static final String CHROME_TOKEN = "chrome";

    private static final BrowserType browser;

    static {
        if ((Window.Navigator.getAppVersion()).toLowerCase().contains(IE_TOKEN)) {
            browser = BrowserType.IE;
        } else if (Window.Navigator.getAppVersion().toLowerCase().contains(IE11_TOKEN)) {
            browser = BrowserType.IE11;
        } else if ((Window.Navigator.getAppName() + " " + Window.Navigator.getUserAgent()).toLowerCase().contains(CHROME_TOKEN)) {
            browser = BrowserType.CHROME;
        } else {
            browser = BrowserType.FIREFOX;
        }
    }

    private Navigator() {
    }

    /**
     * IE 10 и хуже
     */
    public static boolean isIE() {
        return browser == BrowserType.IE;
    }

    public static boolean isIE11() {
        return browser == BrowserType.IE11;
    }

    public static boolean isChrome() {
        return browser == BrowserType.CHROME;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static boolean isFirefox() {
        return browser == BrowserType.FIREFOX;
    }

    private enum BrowserType {
        IE, IE11, CHROME, FIREFOX
    }
}
