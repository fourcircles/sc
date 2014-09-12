package kz.arta.synergy.components.client.input.date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.DateUtil;

import java.util.Date;

/**
 * User: user
 * Date: 01.08.14
 * Time: 9:57
 * Компонент выбора даты
 */
public class ArtaDatePicker extends Composite implements HasValueChangeHandlers<Date>, HasAllMouseHandlers {

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

    /**
     * Режим отображения, по умолчанию - день
     */
    CalendarMode calendarMode = CalendarMode.DAY;

    /**
     * Дата текущего отображаемого месяца
     */
    Date currentDate = new Date();

    /**
     * Выбранная дата
     */
    public Date selectedDate = null;

    /**
     * Основная панель
     */
    FlowPanel panel;

    /**
     * Верхняя панель
     */
    MonthSelector monthSelector;

    /**
     * Нижняя панель
     */
    TodaySelector todaySelector;

    /**
     * Панель с календарем
     */
    CalendarPanel calendarPanel;

    /**
     * Создает календарь с режимом отображения по умолчанию
     */
    public ArtaDatePicker() {
         init();
    }

    /**
     * Создает календарь с заданным режимом отображения
     * @param mode  режим отображения
     */
    public ArtaDatePicker(CalendarMode mode) {
        calendarMode = mode;
        init();
    }

    private void init() {
        panel = GWT.create(FlowPanel.class);
        initWidget(panel);

        monthSelector = new MonthSelector(this);
        panel.add(monthSelector);

        monthSelector.topBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Date month = new Date(currentDate.getTime());
                month.setMonth(currentDate.getMonth() - 1);
                setCurrentDate(month, false);
            }
        });

        monthSelector.topForward.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Date month = new Date(currentDate.getTime());
                month.setMonth(currentDate.getMonth() + 1);
                setCurrentDate(month, false);
            }
        });

        calendarPanel = new CalendarPanel(this);
        panel.add(calendarPanel);


        todaySelector = new TodaySelector();
        panel.add(todaySelector);
        todaySelector.todayLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setCurrentDate(DateUtil.currentDate);
            }
        });
        todaySelector.back.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Date month = new Date(currentDate.getTime());
                switch (calendarMode) {
                    case WEEK:
                        month.setDate(currentDate.getDate() - 7);
                        break;
                    case MONTH:
                        month.setMonth(currentDate.getMonth() - 1);
                    default:
                        month.setDate(currentDate.getDate() - 1);
                }
                setCurrentDate(month, true);
            }
        });
        todaySelector.forward.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Date month = new Date(currentDate.getTime());
                switch (calendarMode) {
                    case WEEK:
                        month.setDate(currentDate.getDate() + 7);
                        break;
                    case MONTH:
                        month.setMonth(currentDate.getMonth() + 1);
                    default:
                        month.setDate(currentDate.getDate() + 1);
                }
                setCurrentDate(month, true);
            }
        });

        panel.setStyleName(SynergyComponents.resources.cssComponents().datePicker());
        calendarPanel.setStyleName(SynergyComponents.resources.cssComponents().datePickerCalendar());

    }

    /**
     * Устанавливает дату
     * @param date  дата
     */
    public void setCurrentDate(Date date) {
        setCurrentDate(date, true);
    }

    public void setCurrentDate(Date date, boolean setSelected) {
        calendarPanel.setCurrentDate(date, setSelected, true);
    }

    /**
     * Метод устанавливает дату без вызова события изменения на datePicker
     * @param date  дата
     */
    void setCurrentDateWithoutFireChange(Date date) {
        calendarPanel.setCurrentDate(date, true, false);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public CalendarMode getCalendarMode() {
        return calendarMode;
    }

    public void setCalendarMode(CalendarMode calendarMode) {
        this.calendarMode = calendarMode;
        calendarPanel.reDrawDays();
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return null;
    }


}
