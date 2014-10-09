package kz.arta.synergy.components.client.input;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Event;

/**
 * User: vsl
 * Date: 24.07.14
 * Time: 10:52
 *
 * Числовое поле ввода
 */
public class NumberInput extends TextInput {

    /**
     * Ввод всего, кроме цифр и разделителя запрещается.
     * Количество разделителей не больше 1.
     */
    @Override
    protected void init() {
        addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                Character ch = event.getCharCode();
                if (ch == 0) {
                    return;
                }
                if (Character.isDigit(ch)) {
                    return;
                }
                String separator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();
                if (separator.charAt(0) == ch && !getText().contains(separator)) {
                    return;
                }
                event.preventDefault();
            }
        });
        super.init();
    }

    /**
     * Вставка текста (Ctrl-V) запрещается, потому что тогда можно вставить не число.
     * @param event
     */
    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONPASTE) {
            event.preventDefault();
        }
    }

    public Number getNumber() {
        NumberFormat nf = NumberFormat.getDecimalFormat();
        return nf.parse(getText());
    }
}
