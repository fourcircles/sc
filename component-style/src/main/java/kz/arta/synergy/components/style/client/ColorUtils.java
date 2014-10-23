package kz.arta.synergy.components.style.client;

/**
 * User: vsl
 * Date: 01.07.14
 * Time: 17:31
 */
public class ColorUtils {
    private static String rgb(String hex) {
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);

        return "" + r + ", " + g + ", " + b;
    }

    public static String toRgb(String hex) {
        return "rgb(" + rgb(hex) + ")";
    }

    public static String toRgba(String hex, String alpha) {
        return "rgba(" + rgb(hex) + ", " + alpha + ")";
    }
}
