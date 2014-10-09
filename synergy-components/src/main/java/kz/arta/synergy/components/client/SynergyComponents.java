package kz.arta.synergy.components.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import kz.arta.synergy.components.client.theme.Theme;
import kz.arta.synergy.components.client.util.DateUtil;
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

        /*инициализируем порядковые номера дней недели, поскольку они зависимы от локали*/
        int weekFirstDay = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().firstDayOfTheWeek();
        for (int i = 0; i <= 6; i ++) {
            if (weekFirstDay == 7) {
                weekFirstDay = 0;
            }
            DateUtil.weekDayNum.put(weekFirstDay, i);
            DateUtil.days.add(weekFirstDay);
            weekFirstDay++;
        }
    }

    private String getThemeName() {
        String themeName = Cookies.getCookie("theme");
        return themeName == null ? Theme.STANDARD.name() : themeName;
    }

    /**
     * Инициализация темы
     * @param themeName тема
     */
    private void initTheme(String themeName) {
        Theme theme = Theme.getTheme(themeName);
        switch (theme) {
            case STANDARD:
                resources = GWT.create(ComponentResources.class);
                break;
            case DARK:
                resources = GWT.create(ComponentResourcesDark.class);
                break;
            default:
                resources = GWT.create(ComponentResources.class);
        }
        resources.cssComponents().ensureInjected();
    }

}
