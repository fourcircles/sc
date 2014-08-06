package kz.arta.synergy.components.client.input.date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
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

/**
 * User: user
 * Date: 01.08.14
 * Time: 17:26
 * Отображение дней месяца в календаре
 */
public class DateSelector extends Composite {

    /**
     * Формат отображения календаря
     */
    public enum CalendarMode {
        /**
         * Режим отображения - день
         */
        DAY,
        /**
         * Режим отображения - неделя
         */
        WEEK,
        /**
         * Режим отображения - месяц
         */
        MONTH
    }

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
    ArrayList<DayLabel> dayLabels = new ArrayList<DayLabel>();

    ArtaDatePicker datePicker;

    Date today = new Date();

    public DateSelector(ArtaDatePicker datePicker) {
        this.datePicker = datePicker;
        init();
    }

    private void init() {
        panel = GWT.create(FlowPanel.class);
        dayNamePanel = GWT.create(FlowPanel.class);
        initWidget(panel);
        for (int i = 0; i < 7; i++) {
            dayNamePanel.add(new InlineLabel(DateUtil.getWeekDay(i)));
        }
        panel.add(dayNamePanel);
        initMonthDays();

//        /*Сразу выбираем сегодняшнюю дату*/
//        datePicker.selectedDate = datePicker.currentDate;
//        dayLabels.get(datePicker.selectedDate .getDate() - 1).setSelected();

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
        int daysCount = DateUtil.getMonthDaysCount(firstDate.getMonth() + 1, firstDate.getYear());

        if (weekDay == 0){
            weekDay = 7;
        }

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
                for (int j = weekDay - 1; j > 0; j--) {
                    Date beforeMonthDay = new Date(firstDate.getTime());
                    CalendarUtil.addDaysToDate(beforeMonthDay, -j);
                    week.add(new DayLabel(beforeMonthDay));
                }
            }

            Date date = new Date(firstDate.getTime());
            CalendarUtil.addDaysToDate(date, i);
            DayLabel label = new DayLabel(date);
            week.add(label);
            dayLabels.add(label);

            /*дни последующего месяца на последней неделе*/
            if (i == daysCount - 1) {
                for (int j = 1; j <= 7 - weekDay; j++) {
                    Date afterMonthDay = new Date(date.getTime());
                    CalendarUtil.addDaysToDate(afterMonthDay, j);
                    week.add(new DayLabel(afterMonthDay));
                }
            }

            weekDay ++;
            if (weekDay == 8){
                weekDay = 1;
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
        setCurrentDate(date, true);
    }

    /**
     * устанавливаем текущую дату
     * @param date  дата
     * @param selectDay  выбирать ли текущую дату
     */
    public void setCurrentDate(Date date, boolean selectDay) {
        if (datePicker.currentDate.getMonth() != date.getMonth() || datePicker.currentDate.getYear() != date.getYear()) {
            datePicker.currentDate = date;
            initMonthDays();
        } else {
            datePicker.currentDate = date;
        }
        datePicker.monthSelector.yearLabel.setText((datePicker.currentDate.getYear() + 1900) + "");
        datePicker.monthSelector.monthLabel.setText(DateUtil.getMonth(datePicker.currentDate.getMonth()));
        if (selectDay) {
            ValueChangeEvent.fire(datePicker, date);
            datePicker.selectedDate = datePicker.currentDate;
            dayLabels.get(date.getDate() - 1).setSelected();
        }
    }


    /**
     * Последняя выбранная дата
     */
    private DayLabel lastSelected;

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
            /*почеркиваем сегодняшнюю дату*/
            if (CalendarUtil.isSameDate(date, today)) {
                getElement().getStyle().setTextDecoration(Style.TextDecoration.UNDERLINE);
            }
        }

        /**
         * Выделяем эту ячейку
         */
        public void setSelected() {
            if (lastSelected != null) {
                MouseStyle.removeAll(lastSelected);
                lastSelected.selected = false;
            }
            lastSelected = this;
            selected = true;
            MouseStyle.setPressed(this);
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
