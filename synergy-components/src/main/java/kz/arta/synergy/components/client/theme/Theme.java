package kz.arta.synergy.components.client.theme;

/**
 * User: user
 * Date: 25.06.14
 * Time: 17:06
 * Цветовые схемы синерджи
 */
public enum  Theme {

    /*Стандартная цветовая схема*/
    standard,

    /*Темная цветовая схема*/
    dark;

    public static Theme getTheme(String themeName) {
        for (Theme theme : Theme.values()) {
            if (theme.name().equals(themeName)) {
                return theme;
            }
        }
        return standard;
    }
}
