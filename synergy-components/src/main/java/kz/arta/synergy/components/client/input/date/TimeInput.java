package kz.arta.synergy.components.client.input.date;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.i18n.client.NumberFormat;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.MaskTextInput;

/**
 * User: user
 * Date: 30.07.14
 * Time: 18:06
 * Поле ввода времени
 * //todo ValueChangeHandler
 */
public class TimeInput extends MaskTextInput {

    /**
     * Часы
     */
    private int hours;

    /**
     * Минуты
     */
    private int minutes;

    public TimeInput() {
        this(false);
    }

    public TimeInput(boolean allowEmpty) {
        super("## : ##");
        this.allowEmpty = allowEmpty;
        setWidth("50px");
        setAlignment(TextAlignment.CENTER);
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setTime(int hours, int minutes){
        this.hours = hours;
        this.minutes = minutes;
        updateTextFromValue();
    }

    private void updateTextFromValue(){
        setText(NumberFormat.getFormat("00").format(hours) + " : "
                + NumberFormat.getFormat("00").format(minutes));
    }

    public void onKeyPress(KeyPressEvent fe) {
        super.onKeyPress(fe);
        checkInput();
    }

    @Override
    public void onKeyDown(KeyDownEvent fe) {
        super.onKeyDown(fe);
        checkInput();
    }

    public boolean checkInput() {
        boolean valid = super.checkInput();
        if (!valid) {
            return false;
        }
        String text = getText();
        int dotsIndex = text.indexOf(":");
        int lineIndex = text.indexOf("_");
        if (dotsIndex == -1 || lineIndex != -1) {
            valid = false;
        }
        String hours = text.substring(0, dotsIndex);
        String minutes = text.substring(dotsIndex + 1, text.length());
        int newHours = 0;
        int newMinutes = 0;

        try {
            newHours = (int) NumberFormat.getFormat("00").parse(hours.trim());
            newMinutes = (int) NumberFormat.getFormat("00").parse(minutes.trim());
            if ((newHours < 0 || newHours > 23) || (newMinutes < 0 || newMinutes > 59)) {
                throw new Exception("Invalid minute or hour value!");
            }
            valid = true;
            this.hours = newHours;
            this.minutes = newMinutes;
        } catch (Exception exc) {
            valid = false;
        }
        if (valid) {
            removeStyleName(SynergyComponents.resources.cssComponents().invalid());
        } else {
            addStyleName(SynergyComponents.resources.cssComponents().invalid());
        }
        return valid;
    }
}
