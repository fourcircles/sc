package kz.arta.synergy.components.client.input.date;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.PopupPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.client.input.date.events.DateCheckHandler;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.DateUtil;
import kz.arta.synergy.components.style.client.Constants;

import java.util.Date;

/**
 * User: user
 * Date: 31.07.14
 * Time: 9:12
 * Компонент ввода даты
 */
public class DateInput extends Composite implements HasEnabled {

    /**
     * Основная панель
     */
    FlowPanel panel;

    /**
     * Введенная дата
     */
    private Date date;

    /**
     * Активно ли поле
     */
    private boolean enabled = true;

    /**
     * Компонент для ввода даты
     */
    private TextInput textInput;

    /**
     * Кнопка отображения календаря
     */
    private ImageButton calendarButton;

    /**
     * Обработчик ввода dateInput
     */
    private DateCheckHandler dateCheckHandler = new DateCheckHandler(this);

    /**
     * Сфокусировано ли поле ввода
     */
    private boolean focused = false;

    /**
     * DatePicker
     */
    private ArtaDatePicker datePicker;

    /**
     * Popup панель календаря
     */
    private PopupPanel calendarPopup = new PopupPanel(true);

    /**
     * Конструктор по умолчанию
     */
    public DateInput() {
        this(false);
    }

    /**
     * @param allowEmpty    можно ли оставлять поле ввода пустым
     */
    public DateInput(boolean allowEmpty) {
        textInput = new TextInput();
        textInput.setAllowEmpty(allowEmpty);
        init();
    }

    private void init() {
        panel = new FlowPanel();
        initWidget(panel);

        textInput.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        calendarButton = new ImageButton(ImageResources.IMPL.calendarIcon());
        calendarButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        panel.add(textInput);
        panel.add(calendarButton);

        datePicker = new ArtaDatePicker();
        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                date = event.getValue();
                setText(event.getValue());
                calendarPopup.hide();
            }
        });
        calendarPopup.setWidget(datePicker);
        calendarPopup.setStyleName(SynergyComponents.resources.cssComponents().calendarPopup());
        calendarButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                calendarPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    @Override
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int x = calendarButton.getAbsoluteLeft();
                        int y = calendarButton.getAbsoluteTop() + calendarButton.getOffsetHeight();

                        int lenX = calendarPopup.getOffsetWidth();
                        int lenY = calendarPopup.getOffsetHeight();

                        if (x + lenX > Window.getClientWidth()) {
                            x -= lenX;
                            x += calendarButton.getOffsetWidth();
                        }
                        if (y + lenY > Window.getClientHeight()) {
                            y -= lenY;
                        }
                        calendarPopup.setPopupPosition(x, y);
                    }
                });
            }
        });

        setStyleName(SynergyComponents.resources.cssComponents().dateInput());
        addStyleName(SynergyComponents.resources.cssComponents().mainText());
        textInput.setWidth(Constants.dateInputWidth());
        textInput.setMaxLength(8);
        textInput.addKeyDownHandler(dateCheckHandler);
        textInput.addKeyUpHandler(dateCheckHandler);
        textInput.addKeyPressHandler(dateCheckHandler);
        textInput.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                focused = true;
                addStyleName(SynergyComponents.resources.cssComponents().focus());
            }
        });
        textInput.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                focused = false;
                removeStyleName(SynergyComponents.resources.cssComponents().focus());
            }
        });
    }

    /**
     * Валидация введенной даты
     * @return  true/false
     */
    public boolean checkInput() {
        boolean correct = DateUtil.isDateValid(textInput.getText().trim()) && textInput.checkInput();
        if (correct) {
            removeStyleName(SynergyComponents.resources.cssComponents().invalid());
            if (focused) {
                addStyleName(SynergyComponents.resources.cssComponents().focus());
            }
            date = DateUtil.parseDate(textInput.getText().trim());
            datePicker.setCurrentDate(date);
        } else {
            addStyleName(SynergyComponents.resources.cssComponents().invalid());
            removeStyleName(SynergyComponents.resources.cssComponents().focus());
        }
        return correct;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Устанавливает дату в поле ввода и в DatePicker
     * @param date дата
     */
    //todo подумать datePicker.setCurrentDate()
    public void setDate(Date date) {
        this.date = date;
        setText(date);
        checkInput();
    }

    private void setText(Date date) {
        textInput.setText(DateUtil.DATE_FORMAT.format(date));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        textInput.setEnabled(enabled);
        calendarButton.setEnabled(enabled);
        if (!enabled) {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        }
    }
}
