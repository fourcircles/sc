package kz.arta.synergy.components.client.input.date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;

import java.util.Date;

/**
 * User: user
 * Date: 06.08.14
 * Time: 10:28
 * Компонент ввода даты и времени
 */
public class DateTimeInput extends Composite {

    private FlowPanel panel;

    /**
     * Компонент ввода времени
     */
    TimeInput timeInput;

    /**
     * Компонент ввода даты
     */
    DateInput dateInput;

    private boolean allowEmpty = true;

    public DateTimeInput() {
        this(false);
    }

    public  DateTimeInput(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        panel = GWT.create(FlowPanel.class);
        initWidget(panel);

        dateInput = new DateInput(allowEmpty);
        timeInput = new TimeInput(allowEmpty);

        panel.add(dateInput);
        panel.add(timeInput);

        dateInput.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            dateInput.getElement().getStyle().setMarginLeft(5, Style.Unit.PX);
        } else {
            dateInput.getElement().getStyle().setMarginRight(5, Style.Unit.PX);
        }
        timeInput.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        setStyleName(SynergyComponents.resources.cssComponents().dateTimeInput());
    }

    public boolean checkInput() {
        return dateInput.checkInput() && timeInput.checkInput();
    }

    public Date getDate() {
        Date date = dateInput.getDate();
        date.setHours(timeInput.getHours());
        date.setMinutes(timeInput.getMinutes());
        date.setSeconds(0);
        return date;
    }

    public void setDate(Date date) {
        dateInput.setDate(date);
        timeInput.setHours(date.getHours());
        timeInput.setMinutes(date.getMinutes());
    }
}
