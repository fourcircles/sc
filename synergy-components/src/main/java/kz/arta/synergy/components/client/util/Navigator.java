package kz.arta.synergy.components.client.util;

import com.google.gwt.user.client.Window;

/**
 * User: user
 * Date: 25.07.14
 * Time: 16:48
 */
public class Navigator {

    private static boolean isIE = false;
    private static boolean isIE11 = false;
    private static boolean isChrome = false;
    private static boolean isFirefox = false;

    static {
        if ((Window.Navigator.getUserAgent()).toLowerCase().contains("msie")){
            isIE = true;
        }
        if (Window.Navigator.getAppVersion().toLowerCase().contains("trident")) {
            isIE11 = true;
        }
        if ((Window.Navigator.getAppName() + " " + Window.Navigator.getUserAgent()).toLowerCase().contains("chrome")){
            isChrome = true;
        }
        if ((Window.Navigator.getAppName() + " " + Window.Navigator.getUserAgent()).toLowerCase().contains("firefox")){
            isFirefox = true;
        }
    }

    public static boolean isIE() {
        return isIE;
    }

    public static boolean isIE11() {
        return isIE11;
    }

    public static boolean isChrome() {
        return isChrome;
    }

    public static boolean isFirefox() {
        return isFirefox;
    }
}
