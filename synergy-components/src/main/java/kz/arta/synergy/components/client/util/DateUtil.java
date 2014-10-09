package kz.arta.synergy.components.client.util;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import kz.arta.synergy.components.client.resources.Messages;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * User: user
 * Date: 31.07.14
 * Time: 18:25
 * Утилита для работы с датой
 */
public class DateUtil {
    /**
     * Смещение года в Date
     */
    public static final int YEAR_OFFSET = 1900;

    private static final Date currentDate = new Date();

    /**
     * Список месяцев
     */
    public static final String[] FULL_MONTHS = new String[] {
            Messages.JANUARY_FULL,
            Messages.FEBRUARY_FULL,
            Messages.MARCH_FULL,
            Messages.APRIL_FULL,
            Messages.MAY_FULL,
            Messages.JUNE_FULL,
            Messages.JULY_FULL,
            Messages.AUGUST_FULL,
            Messages.SEPTEMBER_FULL,
            Messages.OCTOBER_FULL,
            Messages.NOVEMBER_FULL,
            Messages.DECEMBER_FULL,
    };

    /**
     * Список дней недели
     */
    public static final String[] WEEK_DAYS = new String[]{
            Messages.i18n().tr("Вс"),
            Messages.i18n().tr("Пн"),
            Messages.i18n().tr("Вт"),
            Messages.i18n().tr("Ср"),
            Messages.i18n().tr("Чт"),
            Messages.i18n().tr("Пт"),
            Messages.i18n().tr("Сб")
            };

    public static final int MONTHS = FULL_MONTHS.length;
    public static final int WEEKDAYS = WEEK_DAYS.length;

    /**
     * Соответствие порядка дней недели
     * Ключ - день недели
     * Значение - отображаемый день недели в календаре
     * Например, в арабской локали суббота - первый день недели => Ключ 6 - Значение 0
     */
    public static HashMap<Integer, Integer> weekDayNum = new HashMap<Integer, Integer>();

    /**
     * Номера дней в неделе в порядке, зависимом от локали
     */
    public static ArrayList<Integer> days = new ArrayList<Integer>();

    /*Делаем первый символ заглавным*/
    static {
        for (int i = 0; i < FULL_MONTHS.length; i++) {
            FULL_MONTHS[i] = Character.toUpperCase(FULL_MONTHS[i].charAt(0)) + FULL_MONTHS[i].substring(1);
        }
    }

    private DateUtil() {
        /*только статические методы*/
    }

    /**
     * Формат даты для отображения
     */
    public static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd.MM.yy");

    /**
     * Формат даты дд.мм
     */
    public static final DateTimeFormat DAY_MONTH_FORMAT = DateTimeFormat.getFormat("dd.MM");

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
     * Парсим дату в формате "dd.MM.yyyy"
     */
    public static Date parseDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Название месяца по номеру (0 - январь ... 11 - декабрь)
     */
    public static String getMonth(int month) {
        return FULL_MONTHS[month];
    }

    /**
     * Название короткое дня недели
     * @param day день недели (0 - ВС ... 6 - ПН)
     * @return название дня недели
     */
    public static String getWeekDay(int day) {
        return WEEK_DAYS[day];
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

    /**
     * Возвращает дату первого дня недели (локалезависимо)
     * @param date  дата
     * @return  дату первого дня недели
     */
    public static Date getWeekFirstDay(Date date){
        //День недели переданной даты
        int weekDayNumber = weekDayNum.get(date.getDay());
        int dayNumber = date.getDate();
        while (weekDayNumber != 0){
            dayNumber--;
            weekDayNumber --;
        }
        Date result = new Date(date.getTime());
        result.setDate(result.getDate() - (result.getDate() - dayNumber));
        return result;
    }

    /**
     * Возвращает дату последнего дня недели (локалезависимо)
     * @param date  дата
     * @return  дату последнего дня недели
     */
    public static Date getWeekLastDay(Date date){
        //День недели переданной даты
        int weekDayNumber = weekDayNum.get(date.getDay());

        int dayNumber = date.getDate();
        while (weekDayNumber != 6){
            dayNumber++;
            weekDayNumber ++;
        }
        Date result = new Date(date.getTime());
        result.setDate(result.getDate() - (result.getDate() - dayNumber));
        return result;
    }

    /**
     * Возвращает начальную и конечную даты недели в формате дд.мм - дд.мм
     * @param date  дата
     * @return строку в формате дд.мм - дд.мм
     */
    public static String getWeekDate(Date date) {
        return DateUtil.DAY_MONTH_FORMAT.format(DateUtil.getWeekFirstDay(date)) + " - " + DateUtil.DAY_MONTH_FORMAT.format(DateUtil.getWeekLastDay(date));
    }

    /**
     * Возвращает начальную и конечную даты недели в формате дд.мм - дд.мм
     * @param date  дата
     * @return строку в формате дд.мм - дд.мм
     */
    public static String getMonthDate(Date date) {
        CalendarUtil.setToFirstDayOfMonth(date);
        String first = DateUtil.DAY_MONTH_FORMAT.format(date);
        date.setDate(getMonthDaysCount(date.getMonth() + 1, date.getYear()));
        String last = DateUtil.DAY_MONTH_FORMAT.format(date);
        return first + " - " + last;
    }



    /**
     * Получаем номер недели в году
     * @param date  дата
     * @return номер недели
     */
    public static int getDateWeek(Date date) {
        /*алгоритм взят из интернета*/
        Date thisThursday = new Date(date.getYear(), date.getMonth(), date.getDate() - weekday(date) + 4);
        Date firstThursdayOfYear = new Date(thisThursday.getYear(), 0, 1);
        while (weekday(firstThursdayOfYear) != 4) {
            firstThursdayOfYear.setDate(firstThursdayOfYear.getDate() + 1);
        }
        Date firstMondayOfYear = new Date(firstThursdayOfYear.getYear(), 0, firstThursdayOfYear.getDate() - 3);
        return (int) ((thisThursday.getTime() - firstMondayOfYear.getTime()) / 1000 / 60 / 60 / 24 / 7 + 1);
    }

    /**
     * Получаем день недели (1 - понедельник ... 7 - воскресенье)
     * @param date  дата
     * @return  день недели
     */
    public static Integer weekday(Date date) {
        int weekday = date.getDay();
        if (weekday == 0) {
            weekday = 7;
        }
        return weekday;
    }

    /**
     * Текущая дата. В будущем  брать дату сервера
     */
    public static Date getCurrentDate() {
        return currentDate;
    }
}
