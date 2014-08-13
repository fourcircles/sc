package kz.arta.synergy.components.client.input.date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.DateUtil;
import kz.arta.synergy.components.client.util.MouseStyle;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * User: user
 * Date: 01.08.14
 * Time: 17:26
 * Отображение дней месяца в календаре
 */
public class CalendarPanel extends Composite {


    FlowPanel panel;

    /**
     * Панель с названиями дней недели
     */
    FlowPanel dayNamePanel;

    /**
     * Список панелей с неделями
     */
    ArrayList<FlowPanel> weekPanels = new ArrayList<FlowPanel>();

    /**
     * Список дней
     */
    HashMap<String, DayLabel> dayLabels = new HashMap<String, DayLabel> ();

    ArtaDatePicker datePicker;

    Date today = DateUtil.currentDate;

    private DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");

    protected CalendarPanel(ArtaDatePicker datePicker) {
        this.datePicker = datePicker;
        init();
    }

    private void init() {
        panel = GWT.create(FlowPanel.class);
        dayNamePanel = GWT.create(FlowPanel.class);
        initWidget(panel);
        for (int i = 0; i < 7; i++) {
            dayNamePanel.add(new InlineLabel(DateUtil.getWeekDay(DateUtil.days.get(i))));
        }
        panel.add(dayNamePanel);
        initMonthDays();

        dayNamePanel.setStyleName(SynergyComponents.resources.cssComponents().dayWeekPanel());
        dayNamePanel.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
    }

    /**
     * Строим панель с названиями дней недели
     */
    private void initMonthDays() {

        for (FlowPanel week : weekPanels) {
            panel.remove(week);
        }
        weekPanels.clear();
        dayLabels.clear();

        /*Первый день в месяце*/
        Date firstDate = new Date(datePicker.currentDate.getTime());
        CalendarUtil.setToFirstDayOfMonth(firstDate);
        int weekDay = firstDate.getDay();
        int realWeekDay = DateUtil.weekDayNum.get(weekDay);

        int daysCount = DateUtil.getMonthDaysCount(firstDate.getMonth() + 1, firstDate.getYear());

        boolean newRow = true;
        FlowPanel week = null;
        for (int i = 0; i < daysCount; i ++){
            if (newRow) {
                week = new FlowPanel();
                weekPanels.add(week);
                panel.add(week);
                newRow = false;
            }

            /*дни предыдущего месяца на первой неделе*/
            if (i == 0) {
                for (int j = realWeekDay; j > 0; j--) {
                    Date beforeMonthDay = new Date(firstDate.getTime());
                    CalendarUtil.addDaysToDate(beforeMonthDay, -j);
                    DayLabel daylabel = new DayLabel(beforeMonthDay);
                    week.add(daylabel);
                    dayLabels.put(format.format(beforeMonthDay), daylabel);
                }
            }

            Date date = new Date(firstDate.getTime());
            CalendarUtil.addDaysToDate(date, i);
            DayLabel label = new DayLabel(date);
            week.add(label);
            dayLabels.put(format.format(date), label);

            /*дни последующего месяца на последней неделе*/
            if (i == daysCount - 1) {
                for (int j = 1; j <= 6 - realWeekDay; j++) {
                    Date afterMonthDay = new Date(date.getTime());
                    CalendarUtil.addDaysToDate(afterMonthDay, j);
                    DayLabel daylabel = new DayLabel(afterMonthDay);
                    week.add(daylabel);
                    dayLabels.put(format.format(afterMonthDay), daylabel);
                }
            }

            realWeekDay ++;
            if (realWeekDay == 7){
                realWeekDay = 0;
                newRow = true;
            }
            week.setStyleName(SynergyComponents.resources.cssComponents().daysPanel());
            week.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        }

    }

    /**
     * устанавливаем текущую дату
     * @param date  дата
     */
    public void setCurrentDate(Date date) {
        setCurrentDate(date, true, true);
    }

    /**
     * устанавливаем текущую дату
     * @param date  дата
     * @param selectDay  выбирать ли текущую дату
     * @param fireChange вызывать ли событие изменения
     */
    public void setCurrentDate(Date date, boolean selectDay, boolean fireChange) {
        if (date == null) {
            return;
        }
        if (datePicker.currentDate.getMonth() != date.getMonth() || datePicker.currentDate.getYear() != date.getYear()) {
            datePicker.currentDate = date;
            initMonthDays();
        } else {
            datePicker.currentDate = date;
        }
        datePicker.monthSelector.yearLabel.setText((datePicker.currentDate.getYear() + DateUtil.YEAR_OFFSET) + "");
        datePicker.monthSelector.monthLabel.setText(DateUtil.getMonth(datePicker.currentDate.getMonth()));
        if (selectDay) {
            if (fireChange) {
                ValueChangeEvent.fire(datePicker, date);
            }
            datePicker.selectedDate = datePicker.currentDate;
            dayLabels.get(format.format(date)).setSelected();
        }
    }

    /**
     * Перерисовать дни месяца
     */
    public void reDrawDays() {
        for (DayLabel label: dayLabels.values()) {
            label.setStyle();
        }
    }

    /**
     * Выбранные даты
     */
    private ArrayList<String> lastSelected = new ArrayList<String>();

    /**
     * Ячейка дня
     */
    public class DayLabel extends InlineLabel {

        /**
         * Дата ячейки
         */
        public Date date;

        /**
         * Выделена ли ячейка
         */
        private boolean selected = false;

        public DayLabel(Date currentDate) {
            this.date = currentDate;
            setText(currentDate.getDate() + "");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setCurrentDate(date);
                }
            });
            setStyle();
            sinkEvents(Event.MOUSEEVENTS);
            sinkEvents(Event.ONCLICK);
        }

        private void setStyle() {
            if (date.getMonth() == datePicker.currentDate.getMonth()) {
                setStyleName(SynergyComponents.resources.cssComponents().month());
            } else {
                setStyleName(SynergyComponents.resources.cssComponents().outMonth());
            }
            /*подчеркиваем сегодняшнюю дату*/
            if (CalendarUtil.isSameDate(date, today)) {
                getElement().getStyle().setTextDecoration(Style.TextDecoration.UNDERLINE);
            }
            switch (datePicker.calendarMode) {
                case MONTH:
                    if (today.getYear() == date.getYear() && today.getMonth() == date.getMonth()) {
                        setStyleName(SynergyComponents.resources.cssComponents().thisMonth());
                    }
                    break;
                case WEEK:
                    if (CalendarUtil.isSameDate(DateUtil.getWeekFirstDay(date), DateUtil.getWeekFirstDay(today))) {
                        setStyleName(SynergyComponents.resources.cssComponents().thisMonth());
                    }
                    break;
            }
            if (lastSelected.contains(format.format(date))) {
                selected = true;
                MouseStyle.setPressed(this);
            }
        }

        /**
         * Выбираем эту ячейку
         */
        public void setSelected() {
            if (!lastSelected.isEmpty()) {
                for (String label: lastSelected) {
                    DayLabel temp = dayLabels.get(label);
                    if (temp != null) {
                        MouseStyle.removeAll(temp);
                        temp.selected = false;
                    }
                }
                lastSelected.clear();
            }
            Date firstDate = new Date(date.getTime());
            switch (datePicker.calendarMode) {
                case DAY:
                    lastSelected.add(format.format(date));
                    selected = true;
                    MouseStyle.setPressed(this);
                    break;
                case WEEK:
                    firstDate = DateUtil.getWeekFirstDay(date);
                    for (int i = 0; i < 7; i++) {
                        if (i != 0) {
                            firstDate.setDate(firstDate.getDate() + 1);
                        }
                        DayLabel label = dayLabels.get(format.format(firstDate));
                        lastSelected.add(format.format(firstDate));
                        label.selected = true;
                        MouseStyle.setPressed(label);
                    }
                    break;
                case MONTH:
                    CalendarUtil.setToFirstDayOfMonth(firstDate);
                    for (int i = 0; i < DateUtil.getMonthDaysCount(firstDate.getMonth() + 1, firstDate.getYear()); i++) {
                        if (i != 0) {
                            firstDate.setDate(firstDate.getDate() + 1);
                        }
                        DayLabel label = dayLabels.get(format.format(firstDate));
                        lastSelected.add(format.format(firstDate));
                        label.selected = true;
                        MouseStyle.setPressed(label);
                    }
                    break;
            }
        }

        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEUP:
                    if (!selected) {
                        MouseStyle.removeAll(this);
                    }
                    break;
                case Event.ONMOUSEOUT:
                    if (!selected) {
                        MouseStyle.removeAll(this);
                    } else {
                        MouseStyle.setPressed(this);
                    }
                    break;
                case Event.ONMOUSEOVER:
                    MouseStyle.setOver(this);
                    break;
            }
            super.onBrowserEvent(event);
        }

    }

}
