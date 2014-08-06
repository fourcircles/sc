package kz.arta.synergy.components.client.util;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import kz.arta.synergy.components.client.resources.Messages;

import java.util.Date;

/**
 * User: user
 * Date: 31.07.14
 * Time: 18:25
 * Утилита для работы с датой
 */
public class DateUtil {

    public static final int YEAR_OFFSET = 1900;

    public static Date currentDate = new Date();

    /**
     * Список месяцев
     */
    public static String[] months = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().monthsFullStandalone();

    /**
     * Список дней недели
     */
    public static String[] weekDays = new String[]{
            Messages.i18n.tr("Пн"),
            Messages.i18n.tr("Вт"),
            Messages.i18n.tr("Ср"),
            Messages.i18n.tr("Чт"),
            Messages.i18n.tr("Пт"),
            Messages.i18n.tr("Сб"),
            Messages.i18n.tr("Вс")};

    /**
     * Формат даты для отображения
     */
    public static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd.MM.yy");

    /**
     * Валидация даты
     * @param dateString  строка с датой
     * @return  true/false
     */
    public static boolean isDateValid(String dateString) {
        try {
            DATE_FORMAT.parse(dateString);
        } catch (Exception exc) {
            return false;
        }
        if (dateString.indexOf(".") != 2) {
            return false;
        }
        dateString = dateString.substring(3, dateString.length());
        if (dateString.indexOf(".") != 2) {
            return false;
        }
        dateString = dateString.substring(3, dateString.length());
        return dateString.length() == 2;
    }

    /**
     *
     */
    public static Date parseDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     */
    public static String getMonth(int month) {
        return months[month];
    }

    public static String getWeekDay(int day) {
        return weekDays[day];
    }

    /**
     * Метод определяет количество дней в месяце
     * @param month     Номер месяца (с 1 - январь до 12 - декабрь)
     * @param year      Год
     * @return          Количество дней в месяце
     */
    public static int getMonthDaysCount(int month, int year){
        if (month == 1 || month == 3 || month == 5 ||
                month == 7 || month == 8 || month == 10 || month == 12){
            return 31;
        }
        if (month == 2){
            if (year == 0){
                return 29;
            }
            if (year % 4 == 0){
                return 29;
            }
            return 28;
        }
        return 30;
    }


}
