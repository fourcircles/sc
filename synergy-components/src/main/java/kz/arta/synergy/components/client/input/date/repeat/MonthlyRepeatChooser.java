package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 24.09.14
 * Time: 17:12
 *
 * Компонент выбора периода повторения ежемесячно
 */
public class MonthlyRepeatChooser extends BaseRepeatChooser {
    /**
     * Количество недель в месяце
     */
    private static final int WEEKS = 5;

    /**
     * Корневая панель
     */
    protected FlowPanel root;

    /**
     * Текущее количество дней
     */
    private int daysCount;

    /**
     * Дни
     */
    protected List<InlineLabel> days;

    /**
     * Хендлер для элементов дней
     */
    private ClickHandler dayClickHandler;


    public MonthlyRepeatChooser() {
        this(RepeatDate.MAX_DAYS);
    }

    /**
     * @param daysCount количество выбираемых дней
     */
    public MonthlyRepeatChooser(int daysCount) {
        super();
        root = new FlowPanel();
        root.setStyleName(SynergyComponents.resources.cssComponents().datePicker());
        root.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        popupPanel.setWidget(root);

        FlowPanel weekDaysPanel = new FlowPanel();
        weekDaysPanel.setStyleName(SynergyComponents.resources.cssComponents().dayWeekPanel());

        FlowPanel datePicker = new FlowPanel();
        datePicker.setStyleName(SynergyComponents.resources.cssComponents().datePickerCalendar());

        List<FlowPanel> weeks = new ArrayList<FlowPanel>();
        for (int i = 0; i < WEEKS; i++) {
            FlowPanel week = new FlowPanel();
            week.setStyleName(SynergyComponents.resources.cssComponents().daysPanel());
            weeks.add(week);
            datePicker.add(week);
        }

        root.add(datePicker);

        dayClickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                dayClick((InlineLabel) event.getSource());
            }
        };

        days = new ArrayList<InlineLabel>();
        for (int i = 0; i < WEEKS * DateUtil.WEEKDAYS; i++) {
            InlineLabel day = createDay();
            day.setText(Integer.toString(i % RepeatDate.MAX_DAYS + 1));
            days.add(day);
            weeks.get(i / DateUtil.WEEKDAYS).add(day);
        }

        this.daysCount = days.size();
        setDaysCount(daysCount);
    }

    @Override
    public void add(RepeatDate date, boolean fireEvents) {
        super.add(date, fireEvents);
        InlineLabel day = days.get(date.getDay());
        if (day != null) {
            day.addStyleName(SynergyComponents.resources.cssComponents().pressed());
        }
    }

    @Override
    protected void remove(RepeatDate date, boolean fireEvents) {
        super.remove(date, fireEvents);
        InlineLabel day = days.get(date.getDay());
        if (day != null) {
            day.removeStyleName(SynergyComponents.resources.cssComponents().pressed());
        }
    }

    /**
     * @param label элемент дня
     * @return дата представляющая этот элемент
     */
    protected RepeatDate createRepeatDate(InlineLabel label) {
        return new RepeatDate(days.indexOf(label), RepeatChooser.MODE.MONTH);
    }

    /**
     * Метод вызывается при клике по элементу дня
     * @param day элемент дня
     */
    void dayClick(InlineLabel day) {
        if (days.indexOf(day) < daysCount) {
            changeSelection(createRepeatDate(day));
        }
    }

    /**
     * @return новый элемент для отображения дней
     */
    InlineLabel createDay() {
        final InlineLabel day = new InlineLabel();
        day.setStyleName(SynergyComponents.resources.cssComponents().month());

        day.addClickHandler(dayClickHandler);
        return day;
    }

    /**
     * Изменяет количество выбираемых дней
     */
    public void setDaysCount(int daysCount) {
        int validDaysCount = Math.min(daysCount, days.size());
        int valueDaysCount = Math.max(validDaysCount, 0);

        for (int i = validDaysCount; i < this.daysCount; i++) {
            days.get(i).addStyleName(SynergyComponents.resources.cssComponents().outMonth());
        }
        for (int i = this.daysCount; i < validDaysCount; i++) {
            days.get(i).removeStyleName(SynergyComponents.resources.cssComponents().outMonth());
        }
        this.daysCount = daysCount;
    }
}
