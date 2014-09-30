package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
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
     * Максимальное количество дней
     */
    private static final int DAYS = 31;

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


    public MonthlyRepeatChooser() {
        this(DAYS);
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

        for (int i = 0; i < DateUtil.WEEKDAYS; i++) {
            InlineLabel weekDay = new InlineLabel();
            weekDay.setText(DateUtil.weekDays[(LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().firstDayOfTheWeek() + i) % DateUtil.WEEKDAYS]);
            weekDaysPanel.add(weekDay);
        }
        root.add(weekDaysPanel);

        List<FlowPanel> weeks = new ArrayList<FlowPanel>();
        for (int i = 0; i < WEEKS; i++) {
            FlowPanel week = new FlowPanel();
            week.setStyleName(SynergyComponents.resources.cssComponents().daysPanel());
            weeks.add(week);
            root.add(week);
        }

        days = new ArrayList<InlineLabel>();
        for (int i = 0; i < WEEKS * DateUtil.WEEKDAYS; i++) {
            InlineLabel day = createDay();
            day.setText(Integer.toString(i + 1));
            days.add(day);
            weeks.get(i / DateUtil.WEEKDAYS).add(day);
        }

        setDaysCount(daysCount);
    }

    @Override
    public void select(RepeatDate date, boolean fireEvents) {
        super.select(date, fireEvents);
        InlineLabel day = days.get(date.getDay());
        if (day != null) {
            day.addStyleName(SynergyComponents.resources.cssComponents().pressed());
        }
    }

    @Override
    protected void deselect(RepeatDate date, boolean fireEvents) {
        super.deselect(date, fireEvents);
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
     * @return новый элемент для отображения дней
     */
    private InlineLabel createDay() {
        final InlineLabel day = new InlineLabel();
        day.setStyleName(SynergyComponents.resources.cssComponents().month());

        day.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                if (days.indexOf(day) < daysCount) {
                    changeSelection(createRepeatDate(day));
                }
            }
        });
        return day;
    }

    /**
     * Изменяет количество выбираемых дней
     */
    public void setDaysCount(int daysCount) {
        for (int i = this.daysCount; i < DateUtil.WEEKDAYS * WEEKS; i++) {
            days.get(i).removeStyleName(SynergyComponents.resources.cssComponents().outMonth());
            days.get(i).setText(Integer.toString(i + 1));
        }
        int newMonthStart = 1;
        for (int i = daysCount; i < DateUtil.WEEKDAYS * WEEKS; i++) {
            days.get(i).addStyleName(SynergyComponents.resources.cssComponents().outMonth());
            days.get(i).setText(Integer.toString(newMonthStart++));
        }
        this.daysCount = daysCount;
    }
}
