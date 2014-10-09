package kz.arta.synergy.components.client.input.date.repeat;

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
     * Максимальное значение дня
     */
    public static final int MAX_DAYS = 31;

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
        this.mode = mode;
        switch (mode) {
            case WEEK:
                initWeekDate(day);
                break;
            case MONTH:
                initMonthDate(day);
                break;
            case YEAR:
                initYearDate(day, month);
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

    private void initWeekDate(int day) {
        if (day < 0 || day >= DateUtil.WEEKDAYS) {
            throw new IllegalArgumentException();
        }
        this.day = day;
        month = -1;
        text = DateUtil.WEEK_DAYS[day];
    }

    public void initMonthDate(int day) {
        if (day < 0 || day >= RepeatDate.MAX_DAYS) {
            throw new IllegalArgumentException();
        }
        this.day = day;
        month = -1;
        text = Integer.toString(day + 1);
    }

    public void initYearDate(int day, int month) {
        if (day < 0 || day >= RepeatDate.MAX_DAYS) {
            throw new IllegalArgumentException();
        }
        if (month < 0 || month >= DateUtil.MONTHS) {
            throw new IllegalArgumentException();
        }
        this.day = day;
        this.month = month;

        text = (day < 9 ? "0" : "") + (day + 1) + '.' + (month + 1);
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
        boolean result = day == date.getDay();
        if (mode == RepeatChooser.MODE.YEAR) {
            result = result && (month == date.getMonth());
        }
        return result;
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
