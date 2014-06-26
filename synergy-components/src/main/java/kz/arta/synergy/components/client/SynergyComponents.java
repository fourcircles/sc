package kz.arta.synergy.components.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import kz.arta.synergy.components.client.theme.Theme;
import kz.arta.synergy.components.style.client.resources.ComponentResources;
import kz.arta.synergy.components.style.client.resources.ComponentResourcesDark;

/**
 * User: user
 * Date: 23.06.14
 * Time: 11:10
 */
public class SynergyComponents implements EntryPoint {

    public static ComponentResources resources;

    @Override
    public void onModuleLoad() {
        String theme = getThemeName();
        initTheme(theme);
    }

    private String getThemeName() {
        String themeName = Cookies.getCookie("theme");
        return themeName == null ? Theme.standard.name() : themeName;
    }

    /**
     * Инициализация темы
     * @param themeName тема
     */
    private void initTheme(String themeName) {
        System.out.println(themeName);
        Theme theme = Theme.getTheme(themeName);
        switch (theme) {
            case standard:
                resources = GWT.create(ComponentResources.class);
                break;
            case dark:
                resources = GWT.create(ComponentResourcesDark.class);
                break;
            default:
                resources = GWT.create(ComponentResources.class);
        }
        resources.cssComponents().ensureInjected();
    }

}
