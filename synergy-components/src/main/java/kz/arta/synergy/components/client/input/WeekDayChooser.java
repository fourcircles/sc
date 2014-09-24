package kz.arta.synergy.components.client.input;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.*;

/**
 * User: vsl
 * Date: 23.09.14
 * Time: 16:14
 *
 * Выбор дня недели
 */
public class WeekDayChooser extends Composite {
    private static final int WEEKDAYS = 7;
    private static final String FONT = SynergyComponents.resources.cssComponents().mainTextBold();

    /**
     * Дни недели
     */
    private List<Label> days;

    /**
     * Выбранные дни недели
     */
    private Set<Integer> selectedDays;

    public WeekDayChooser() {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.resources.cssComponents().weekChooser());
        root.addStyleName(FONT);

        days = new ArrayList<Label>();
        selectedDays = new HashSet<Integer>();

        for (int i = 0; i < WEEKDAYS; i++) {
            Label weekDay = createWeekDay(i);
            days.add(weekDay);
            root.add(weekDay);
        }
    }

    /**
     * При загрузке находим ячейку с самым длинным текстом,
     * все остальные ячейки становятся такой же длины.
     */
    @Override
    protected void onLoad() {
        super.onLoad();

        Label maxLabel = Collections.max(days, new Comparator<Label>() {
            @Override
            public int compare(Label label1, Label label2) {
                return Utils.getTextWidth(label1.getText(), FONT) -
                        Utils.getTextWidth(label2.getText(), FONT);
            }
        });
        int maxWidth = Utils.getTextWidth(maxLabel.getText(), FONT);
        for (Label day : days) {
            day.getElement().getStyle().setWidth(maxWidth, Style.Unit.PX);
        }
        int totalWidth = (maxWidth + Constants.WEEKDAY_CHOOSER_PADDING * 2) * WEEKDAYS + Constants.BORDER_WIDTH * 2;
        getElement().getStyle().setWidth(totalWidth, Style.Unit.PX);
    }

    /**
     * Создает день недели с заданным порядковым номером
     * @param index номер
     * @return день недели
     */
    private Label createWeekDay(final int index) {
        final Label day = new Label();
        day.setStyleName(SynergyComponents.resources.cssComponents().day());

        int dayNum = (index + LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().firstDayOfTheWeek()) % WEEKDAYS;
        String dayName = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().weekdaysShort()[dayNum];
        day.setText(dayName.substring(0, 1).toUpperCase() + dayName.substring(1));
        day.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    event.stopPropagation();
                    event.preventDefault();
                    return;
                }
                selectDay(index);
            }
        });

        return day;
    }

    /**
     * Выбрать день на заданной позиции
     * @param index позиция
     */
    public void selectDay(int index) {
        Label day = days.get(index);
        if (selectedDays.contains(index)) {
            day.removeStyleName(SynergyComponents.resources.cssComponents().selected());
            selectedDays.remove(index);
        } else {
            day.addStyleName(SynergyComponents.resources.cssComponents().selected());
            selectedDays.add(index);
        }
    }

    /**
     * Возвращает выбранные дни
     */
    public Set<Integer> getSelected() {
        return selectedDays;
    }
}