package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.DateUtil;

/**
 * User: vsl
 * Date: 24.09.14
 * Time: 18:12
 *
 * Компонент выбора периода повторения ежегодно
 */
public class YearlyRepeatChooser extends MonthlyRepeatChooser {
    /**
     * Текущий месяц
     */
    int currentMonth = 0;

    /**
     * Элемент для отображения названия месяца
     */
    private final InlineLabel monthLabel;

    public YearlyRepeatChooser() {
        FlowPanel monthSelector = new FlowPanel();
        monthSelector.setStyleName(SynergyComponents.resources.cssComponents().datePickerTop());

        Image previous = GWT.create(Image.class);
        previous.setResource(ImageResources.IMPL.navigationLeft());
        Image next = GWT.create(Image.class);
        next.setResource(ImageResources.IMPL.navigationRight());

        monthLabel = GWT.create(InlineLabel.class);
        monthLabel.setStyleName(SynergyComponents.resources.cssComponents().bigText());

        monthSelector.add(previous);
        monthSelector.add(monthLabel);
        monthSelector.add(next);

        root.insert(monthSelector, 0);

        previous.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                previousMonth();
            }
        });
        next.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                nextMonth();
            }
        });

        updateMonth();
    }

    /**
     * Изменяет вид в соответствии с текущим месяцем (обычно вызывается после изменения текущего месяца).
     * Изменяет количество выбираемых дней и выбранные даты.
     */
    void updateMonth() {
        setDaysCount(DateUtil.getMonthDaysCount(currentMonth + 1, 0));
        monthLabel.setText(DateUtil.getMonth(currentMonth));

        //снятие выделения дней
        for (InlineLabel day : days) {
            day.removeStyleName(SynergyComponents.resources.cssComponents().pressed());
        }

        //выделение добавленых ранее дней в текущем месяце
        for (RepeatDate date : getSelectedDates()) {
            if (date.getMonth() == currentMonth) {
                days.get(date.getDay()).addStyleName(SynergyComponents.resources.cssComponents().pressed());
            }
        }
    }

    /**
     * Следующий месяц
     */
    private void nextMonth() {
        currentMonth = (currentMonth + 1) % DateUtil.MONTHS;
        updateMonth();
    }

    /**
     * Предыдущий месяц
     */
    private void previousMonth() {
        currentMonth--;
        if (currentMonth < 0) {
            currentMonth = DateUtil.MONTHS - 1;
        }
        updateMonth();
    }

    @Override
    protected RepeatDate createRepeatDate(InlineLabel label) {
        return new RepeatDate(days.indexOf(label), currentMonth, RepeatChooser.MODE.YEAR);
    }


}
