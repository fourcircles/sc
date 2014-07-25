package kz.arta.synergy.components.client.input;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.handlers.PlaceHolderFocusHandler;

/**
 * User: user
 * Date: 15.07.14
 * Time: 10:52
 * Общий класс для однострочных полей ввода
 */
public class CommonInput extends TextBox {

    /**
     * Можно ли оставить пустым поле ввода
     */
    protected boolean allowEmpty = true;

    /**
     * Активно ли поле
     */
    protected boolean enabled = true;

    /**
     * Хэндлер для placeHolder
     */
    protected PlaceHolderFocusHandler placeHolderHandler;

    /**
     * Конструктор по умолчанию
     */
    public CommonInput() {
        this(true);
    }

    /**
     * Конструктор для поля ввода с указанием обязательного ввода текста
     * @param allowEmpty  можно ли оставить пустым поле ввода
     */
    public CommonInput(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        init();
    }

    protected void init() {
        sinkEvents(Event.KEYEVENTS);
        sinkEvents(Event.FOCUSEVENTS);
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONPASTE);
        setStyleName(SynergyComponents.resources.cssComponents().commonInput());
        addStyleName(SynergyComponents.resources.cssComponents().mainText());
        addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                checkInput();
            }
        });
        getElement().getStyle().setProperty("direction", LocaleInfo.getCurrentLocale().isRTL() ? Direction.RTL.name() : Direction.LTR.name());
    }

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public void setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if (readOnly) {
            getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        } else {
            getElement().getStyle().setCursor(Style.Cursor.AUTO);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setReadOnly(!enabled);
        this.enabled = enabled;
        if (enabled) {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        }

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void onBrowserEvent(Event event) {
        if (!enabled) {
            event.preventDefault();
            return;
        }
       super.onBrowserEvent(event);
    }

    /**
     * Устанавливаем placeHolder компонента
     * @param placeHolder   строка
     */
    public void setPlaceHolder(String placeHolder) {
        if (placeHolderHandler == null){
            placeHolderHandler = new PlaceHolderFocusHandler(this);
            addFocusHandler(placeHolderHandler);
            addBlurHandler(placeHolderHandler);
        }
        placeHolderHandler.setPlaceHolder(placeHolder);
    }

    /**
     * Метод проверяет корректно ли заполнено данное поле ввода.
     * Перед проверкой удаляются лишние начинающие и заключающие пробелы.
     * @return true/false
     */
    public boolean checkInput() {
        if (!enabled) {
            return true;
        }
        boolean correct = true;
        if (!allowEmpty) {
            correct = !getText().trim().isEmpty();
        }
        if (correct) {
            removeStyleName(SynergyComponents.resources.cssComponents().invalid());
        } else {
            addStyleName(SynergyComponents.resources.cssComponents().invalid());
        }
        return correct;
    }

    @Override
    public String getText() {
        if (placeHolderHandler != null) {
            if (super.getText().equals(placeHolderHandler.getPlaceHolder())) {
                return "";
            }
        }
        return super.getText();
    }

}
