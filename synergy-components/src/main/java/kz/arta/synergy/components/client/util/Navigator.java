package kz.arta.synergy.components.client.util;

import com.google.gwt.user.client.Window;

/**
 * User: user
 * Date: 25.07.14
 * Time: 16:48
 */
public class Navigator {

    public static boolean isIE = false;
    public static boolean isChrome = false;
    public static boolean isFirefox = false;

    static {
        if ((Window.Navigator.getUserAgent()).toLowerCase().contains("msie")){
            isIE = true;
        }
        if ((Window.Navigator.getAppName() + " " + Window.Navigator.getUserAgent()).toLowerCase().contains("chrome")){
            isChrome = true;
        }
        if ((Window.Navigator.getAppName() + " " + Window.Navigator.getUserAgent()).toLowerCase().contains("firefox")){
            isFirefox = true;
        }
    }
}
