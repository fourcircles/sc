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

    private static final String januaryFull = "Январь";
    private static final String februaryFull = "Февраль";
    private static final String marchFull = "Март";
    private static final String aprilFull = "Апрель";
    private static final String mayFull = "Май";
    private static final String juneFull = "Июль";
    private static final String julyFull = "Июнь";
    private static final String augustFull = "Август";
    private static final String septemberFull = "Сентябрь";
    private static final String octoberFull = "Октябрь";
    private static final String novemberFull = "Ноябрь";
    private static final String decemberFull = "Декабрь";

    public static String januaryFull() {
        return i18n.tr(januaryFull);
    }
    public static String februaryFull() {
        return i18n.tr(februaryFull);
    }
    public static String marchFull() {
        return i18n.tr(marchFull);
    }
    public static String aprilFull() {
        return i18n.tr(aprilFull);
    }
    public static String mayFull() {
        return i18n.tr(mayFull);
    }
    public static String juneFull() {
        return i18n.tr(juneFull);
    }
    public static String julyFull() {
        return i18n.tr(julyFull);
    }
    public static String augustFull() {
        return i18n.tr(augustFull);
    }
    public static String septemberFull() {
        return i18n.tr(septemberFull);
    }
    public static String octoberFull() {
        return i18n.tr(octoberFull);
    }
    public static String novemberFull() {
        return i18n.tr(novemberFull);
    }
    public static String decemberFull() {
        return i18n.tr(decemberFull);
    }

}
