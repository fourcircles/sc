package kz.arta.synergy.components.client.input.date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.events.SelectionEvent;
import kz.arta.synergy.components.client.util.DateUtil;

import java.util.Date;

/**
 * User: user
 * Date: 01.08.14
 * Time: 9:57
 * Компонент выбора даты
 */
public class ArtaDatePicker extends Composite implements HasValueChangeHandlers<Date> {

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
    DateSelector calendarPanel;

    public ArtaDatePicker() {
         init();
    }

    private void init() {
        panel = GWT.create(FlowPanel.class);
        initWidget(panel);

        monthSelector = new MonthSelector();
        panel.add(monthSelector);
        monthSelector.monthList.addSelectionHandler(new SelectionEvent.Handler<DropDownList<java.lang.Integer>.ListItem>() {
            @Override
            public void onSelection(SelectionEvent<DropDownList<Integer>.ListItem> event) {
                Date month = new Date(currentDate.getTime());
                month.setMonth(event.getValue().getValue());
                setCurrentDate(month, false);
            }
        });

        monthSelector.yearsList.addSelectionHandler(new SelectionEvent.Handler<DropDownList<java.lang.Integer>.ListItem>() {
            @Override
            public void onSelection(SelectionEvent<DropDownList<Integer>.ListItem> event) {
                Date year = new Date(currentDate.getTime());
                year.setYear(event.getValue().getValue() - 1900);
                setCurrentDate(year, false);
            }
        });

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

        calendarPanel = new DateSelector(this);
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
                month.setDate(currentDate.getDate() - 1);
                setCurrentDate(month, true);
            }
        });
        todaySelector.forward.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Date month = new Date(currentDate.getTime());
                month.setDate(currentDate.getDate() + 1);
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
        calendarPanel.setCurrentDate(date, true);
    }

    public void setCurrentDate(Date date, boolean setSelected) {
        calendarPanel.setCurrentDate(date, setSelected);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
