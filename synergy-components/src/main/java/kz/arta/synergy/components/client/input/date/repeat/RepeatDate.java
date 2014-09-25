package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.i18n.client.LocaleInfo;
import kz.arta.synergy.components.client.util.DateUtil;

/**
* User: vsl
* Date: 24.09.14
* Time: 14:24
 *
 * Дата повторения.
 * Не относится к конкретному времени или дате.
*/
public class RepeatDate {
    /**
     * Номер дня в месяце
     */
    private int day = -1;

    /**
     * Номер месяца
     */
    private int month = -1;

    /**
     * Режим выбора
     */
    private RepeatChooser.MODE mode;

    /**
     * Текст, который представляет эту дату
     */
    private String text;

    public RepeatDate(int day, int month, RepeatChooser.MODE mode) {
        this.day = day;
        this.month = month;
        this.mode = mode;

        switch (mode) {
            case WEEK:
                int first = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().firstDayOfTheWeek();
                text = DateUtil.weekDays[day];
                break;
            case YEAR:
                text = "" + (day < 9 ? "0" : "") + (day + 1) + "." + (month + 1);
                break;
            case MONTH:
                text = Integer.toString(day + 1);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public RepeatDate(int day, RepeatChooser.MODE mode) {
        this(day, -1, mode);
    }

    public RepeatChooser.MODE getMode() {
        return mode;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof RepeatDate)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        RepeatDate date = (RepeatDate) o;
        if (mode != date.getMode()) {
            return false;
        }
        switch (mode) {
            case WEEK:
            case MONTH:
                return day == date.getDay();
            case YEAR:
                return day == date.getDay() && month == date.getMonth();
            default:
                return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 41;
        result = result * 37 + mode.hashCode();
        result = result * 37 + day;
        if (mode == RepeatChooser.MODE.YEAR) {
            result = result * 37 + month;
        }
        return result;
    }
}
