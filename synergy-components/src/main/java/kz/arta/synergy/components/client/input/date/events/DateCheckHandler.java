package kz.arta.synergy.components.client.input.date.events;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.TextBox;
import kz.arta.synergy.components.client.input.date.DateInput;

/**
 * User: user
 * Date: 31.07.14
 * Time: 15:13
 */
public class DateCheckHandler implements KeyDownHandler, KeyUpHandler, KeyPressHandler {

    DateInput dateInput;

    boolean cancel = false;

    public DateCheckHandler(DateInput dateInput) {
        this.dateInput = dateInput;
    }

    public void onKeyDown(KeyDownEvent event) {
        cancel = !((event.getNativeKeyCode() == KeyCodes.KEY_TAB) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_DELETE) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_HOME) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_END) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_UP) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) ||
                (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT));
    }

    public void onKeyPress(KeyPressEvent event) {
        char c = event.getCharCode();
        if (!Character.isDigit(c) && cancel && c != '.') {
            ((TextBox) event.getSource()).cancelKey();
        }
    }

    public void onKeyUp(KeyUpEvent event) {
        dateInput.checkInput();
    }

}