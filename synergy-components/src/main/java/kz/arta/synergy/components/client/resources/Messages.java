package kz.arta.synergy.components.client.resources;

import kz.arta.i18n.shared.I18nFactory;
import org.xnap.commons.i18n.I18n;

/**
 * User: user
 * Date: 26.06.14
 * Time: 15:00
 * Локализация сообщений в Синерджи
 */
public class Messages {

    public static I18n i18n = I18nFactory.getI18n();

    private static final String JANUARY_FULL = "Январь";
    private static final String FEBRUARY_FULL = "Февраль";
    private static final String MARCH_FULL = "Март";
    private static final String APRIL_FULL = "Апрель";
    private static final String MAY_FULL = "Май";
    private static final String JUNE_FULL = "Июль";
    private static final String JULY_FULL = "Июнь";
    private static final String AUGUST_FULL = "Август";
    private static final String SEPTEMBER_FULL = "Сентябрь";
    private static final String OCTOBER_FULL = "Октябрь";
    private static final String NOVEMBER_FULL = "Ноябрь";
    private static final String DECEMBER_FULL = "Декабрь";

    public static String januaryFull() {
        return i18n.tr(JANUARY_FULL);
    }
    public static String februaryFull() {
        return i18n.tr(FEBRUARY_FULL);
    }
    public static String marchFull() {
        return i18n.tr(MARCH_FULL);
    }
    public static String aprilFull() {
        return i18n.tr(APRIL_FULL);
    }
    public static String mayFull() {
        return i18n.tr(MAY_FULL);
    }
    public static String juneFull() {
        return i18n.tr(JUNE_FULL);
    }
    public static String julyFull() {
        return i18n.tr(JULY_FULL);
    }
    public static String augustFull() {
        return i18n.tr(AUGUST_FULL);
    }
    public static String septemberFull() {
        return i18n.tr(SEPTEMBER_FULL);
    }
    public static String octoberFull() {
        return i18n.tr(OCTOBER_FULL);
    }
    public static String novemberFull() {
        return i18n.tr(NOVEMBER_FULL);
    }
    public static String decemberFull() {
        return i18n.tr(DECEMBER_FULL);
    }

}
