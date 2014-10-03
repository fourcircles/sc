package kz.arta.synergy.components.client.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.handlers.PlaceHolderFocusHandler;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.util.WidthUtil;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: user
 * Date: 15.07.14
 * Time: 17:04
 * Поле ввода для многострочного текста
 */
public class ArtaTextArea extends Composite {

    /**
     * Сам компонент для ввода текста
     */
    private StretchyTextArea textArea = GWT.create(StretchyTextArea.class);

    /**
     * Скролл
     */
    private ArtaScrollPanel verticalScroll;


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
    public ArtaTextArea() {
        this(true);
    }

    /**
     * Конструктор для поля ввода с указанием обязательного ввода текста
     * @param allowEmpty  можно ли оставить пустым поле ввода
     */
    public ArtaTextArea(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        init();
    }

    private void init() {
        textArea.setMinVisibleLines(3);
        verticalScroll = new ArtaScrollPanel();
        verticalScroll.setWidget(textArea);
        setPixelSize(200, 59);
        initWidget(verticalScroll);

        textArea.sinkEvents(Event.KEYEVENTS);
        textArea.sinkEvents(Event.FOCUSEVENTS);
        textArea.sinkEvents(Event.MOUSEEVENTS);

        textArea.setStyleName(SynergyComponents.resources.cssComponents().artaText());
        textArea.addStyleName(SynergyComponents.resources.cssComponents().mainText());
        textArea.getElement().getStyle().setProperty("resize", "none");
        textArea.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                checkInput();
            }
        });
        setStyleName(SynergyComponents.resources.cssComponents().artaTextPanel());
        textArea.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                addStyleName(SynergyComponents.resources.cssComponents().focus());
            }
        });
        textArea.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                removeStyleName(SynergyComponents.resources.cssComponents().focus());
            }
        });

    }

    public void onLoad() {
        try {
            textArea.getElement().getParentElement().getStyle().setWidth(100, Style.Unit.PCT);
        } catch (Exception ignore) {}
    }

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public void setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    public void setEnabled(boolean enabled) {
        textArea.setEnabled(enabled);
        textArea.setReadOnly(!enabled);
        this.enabled = enabled;
        if (enabled) {
            textArea.removeStyleName(SynergyComponents.resources.cssComponents().disabled());
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            textArea.addStyleName(SynergyComponents.resources.cssComponents().disabled());
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
            placeHolderHandler = new PlaceHolderFocusHandler(textArea);
            textArea.addFocusHandler(placeHolderHandler);
            textArea.addBlurHandler(placeHolderHandler);
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

    public String getText() {
        if (placeHolderHandler != null) {
            if (textArea.getText().equals(placeHolderHandler.getPlaceHolder())) {
                return "";
            }
        }
        return textArea.getText();
    }

    public void setPixelSize(int width, int height) {
        verticalScroll.setPixelSize(width, height);
        textArea.setMinVisibleLines(height/ Constants.LINE_HEIGHT);
    }

    public void setSize(String width, String height) {
        verticalScroll.setSize(width, height);
        textArea.setMinVisibleLines(WidthUtil.getPXValue(height)/ Constants.LINE_HEIGHT);
    }

    public void setWidth(String width) {
        verticalScroll.setWidth(width);
    }

}
