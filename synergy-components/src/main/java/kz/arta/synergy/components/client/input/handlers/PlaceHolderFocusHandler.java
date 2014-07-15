package kz.arta.synergy.components.client.input.handlers;

/**
 * User: user
 * Date: 15.07.14
 * Time: 17:19
 */

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.TextBoxBase;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * Хэндлер для placeHolder
 */
public class PlaceHolderFocusHandler implements FocusHandler, BlurHandler {

    private String placeHolder = null;

    protected boolean containsPlaceHolder = false;

    protected TextBoxBase textWidget;

    public PlaceHolderFocusHandler(TextBoxBase textWidget) {
        this.textWidget = textWidget;
    }

    @Override
    public void onFocus(FocusEvent focusEvent) {
        if (placeHolder == null) {
            return;
        }
        removePlaceHolderText();
    }

    @Override
    public void onBlur(BlurEvent blurEvent) {
        if (placeHolder == null) {
            return;
        }
        setPlaceHolderText();
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
        if (placeHolder != null && placeHolder.length() > 0) {
            setPlaceHolderText();
        } else {
            removePlaceHolderText();
        }

    }

    protected void setPlaceHolderText() {
        if (textWidget.getText() == null || textWidget.getText().length() == 0) {
            containsPlaceHolder = true;
            textWidget.setText(placeHolder);
            textWidget.addStyleName(SynergyComponents.resources.cssComponents().placeHolder());
        }
    }

    private void removePlaceHolderText() {
        if (containsPlaceHolder) {
            containsPlaceHolder = false;
            textWidget.setText("");
            textWidget.removeStyleName(SynergyComponents.resources.cssComponents().placeHolder());
        }
    }

    public String getPlaceHolder() {
        return placeHolder;
    }
}