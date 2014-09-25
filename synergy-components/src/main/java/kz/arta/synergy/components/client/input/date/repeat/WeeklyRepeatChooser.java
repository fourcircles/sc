package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.DateUtil;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: vsl
 * Date: 24.09.14
 * Time: 17:24
 *
 * Компонент выбора периода повторения еженедельно
 */
public class WeeklyRepeatChooser extends BaseRepeatChooser {
    /**
     * Начало недели
     */
    private static final int WEEK_START = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().firstDayOfTheWeek();

    /**
     * Шрифт для дней
     */
    private static final String FONT = SynergyComponents.resources.cssComponents().mainTextBold();

    /**
     * UI-элементы для дней
     */
    private List<Label> labels;

    /**
     * Дни
     */
    private List<RepeatDate> days;

    /**
     * Корневая панель
     */
    private final FlowPanel root;

    public WeeklyRepeatChooser() {
        super();

        root = new FlowPanel();
        root.setStyleName(SynergyComponents.resources.cssComponents().weekChooser());
        root.addStyleName(FONT);

        popupPanel.setWidget(root);

        days = new ArrayList<RepeatDate>(DateUtil.WEEKDAYS);
        labels = new ArrayList<Label>(DateUtil.WEEKDAYS);

        for (int i = 0; i < DateUtil.WEEKDAYS; i++) {
            RepeatDate day = new RepeatDate((i + WEEK_START) % DateUtil.WEEKDAYS, RepeatChooser.MODE.WEEK);
            days.add(day);

            Label label = createLabel(day);
            labels.add(label);
            root.add(label);
        }

        initWidths();
    }

    /**
     * Находим ячейку с самым длинным текстом,
     * все остальные ячейки становятся такой же ширины.
     */
    private void initWidths() {
        Label maxLabel = Collections.max(labels, new Comparator<Label>() {
            @Override
            public int compare(Label label1, Label label2) {
                return Utils.getTextWidth(label1.getText(), FONT) -
                        Utils.getTextWidth(label2.getText(), FONT);
            }
        });
        int maxWidth = Utils.getTextWidth(maxLabel.getText(), FONT);
        for (Label day : labels) {
            day.getElement().getStyle().setWidth(maxWidth, Style.Unit.PX);
        }
        int totalWidth = (maxWidth + Constants.WEEKDAY_CHOOSER_PADDING * 2) * DateUtil.WEEKDAYS + Constants.BORDER_WIDTH * 2;

        root.getElement().getStyle().setWidth(totalWidth, Style.Unit.PX);
    }

    @Override
    public void select(RepeatDate date, boolean fireEvents) {
        super.select(date, true);
        Label dayLabel = labels.get(days.indexOf(date));
        dayLabel.addStyleName(SynergyComponents.resources.cssComponents().selected());
    }

    @Override
    protected void deselect(RepeatDate date, boolean fireEvents) {
        super.deselect(date, fireEvents);
        Label dayLabel = labels.get(days.indexOf(date));
        dayLabel.removeStyleName(SynergyComponents.resources.cssComponents().selected());
    }

    /**
     * Создает {@link com.google.gwt.user.client.ui.Label} для даты
     * @param date дата
     */
    private Label createLabel(final RepeatDate date) {
        final Label dayLabel = new Label();
        dayLabel.setStyleName(SynergyComponents.resources.cssComponents().day());
        dayLabel.setText(date.toString());
        dayLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                changeSelection(date);
            }
        });
        return dayLabel;
    }
}
