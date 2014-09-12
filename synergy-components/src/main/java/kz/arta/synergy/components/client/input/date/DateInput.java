package kz.arta.synergy.components.client.input.date;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
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
public class DateInput extends Composite implements HasEnabled, HasValueChangeHandlers<Date> {

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
     * Режим отображения календаря
     */
    private ArtaDatePicker.CalendarMode calendarMode = ArtaDatePicker.CalendarMode.DAY;

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
    private PopupPanel calendarPopup;

    /**
     * Находится ли мышь над выпадающим календарем
     */
    private boolean mouseOver = false;

    private ResizeHandler resizeHandler;

    private HandlerRegistration resizeRegistration;

    /**
     * Можно ли оставлять поле ввода пустым
     */
    private boolean allowEmpty = false;

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
        this.allowEmpty = allowEmpty;
        init();
    }

    /**
     *
     * @param calendarMode режим отображения календаря
     */
    public DateInput(ArtaDatePicker.CalendarMode calendarMode) {
        this.calendarMode = calendarMode;
        init();
    }

    /**
     *
     * @param allowEmpty можно ли оставлять поле ввода пустым
     * @param calendarMode режим отображения календаря
     */
    public DateInput(boolean allowEmpty, ArtaDatePicker.CalendarMode calendarMode) {
        this.allowEmpty = allowEmpty;
        this.calendarMode = calendarMode;
        init();
    }

    private void init() {
        panel = new FlowPanel();
        initWidget(panel);

        textInput = new TextInput();
        if (calendarMode != ArtaDatePicker.CalendarMode.DAY) {
            textInput.setReadOnly(true);
            textInput.setWidth(Constants.weekInputWidth());
        } else {
            textInput.setMaxLength(8);
            textInput.setWidth(Constants.dateInputWidth());
        }
        textInput.setAllowEmpty(allowEmpty);
        textInput.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        calendarButton = new ImageButton(ImageResources.IMPL.calendarIcon());
        calendarButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        panel.add(textInput);
        panel.add(calendarButton);

        datePicker = new ArtaDatePicker(calendarMode);

        calendarPopup = new PopupPanel(true) {
            public void hide() {
                if (resizeRegistration != null) {
                    resizeRegistration.removeHandler();
                    resizeRegistration = null;
                }
                mouseOver = false;
                super.hide();
            }

            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (event.getTypeInt() == Event.ONMOUSEWHEEL) {
                    if (!mouseOver) {
                        calendarPopup.hide();
                    }
                }
            }
        };

        datePicker.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                mouseOver = true;
            }
        });

        datePicker.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                mouseOver = false;
            }
        });

        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                setDate(event.getValue());
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
                        if (resizeRegistration == null) {
                            resizeRegistration = Window.addResizeHandler(resizeHandler);
                        }
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

        resizeHandler = new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                calendarPopup.hide();
            }
        };
    }

    /**
     * Валидация введенной даты
     * @return  true/false
     */
    public boolean checkInput() {
        boolean correct = true;
        if (textInput.getText().trim().isEmpty() && allowEmpty) {
            return true;
        }
        if (calendarMode == ArtaDatePicker.CalendarMode.DAY) {
            correct = DateUtil.isDateValid(textInput.getText().trim()) && textInput.checkInput();
        }
        if (correct) {
            removeStyleName(SynergyComponents.resources.cssComponents().invalid());
            if (focused) {
                addStyleName(SynergyComponents.resources.cssComponents().focus());
            }
            if (calendarMode == ArtaDatePicker.CalendarMode.DAY) {
                date = DateUtil.parseDate(textInput.getText().trim());
            }
            datePicker.setCurrentDateWithoutFireChange(date);
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
    public void setDate(Date date) {
        this.date = date;
        setText(date);
        datePicker.setCurrentDateWithoutFireChange(date);
        ValueChangeEvent.fire(this, this.date);
    }

    /**
     * Записываем дату в textInput
     * @param date  дата
     */
    private void setText(Date date) {
        switch (calendarMode) {
            case WEEK:
                textInput.setText(DateUtil.getWeekDate(date));
                break;
            case MONTH:
                textInput.setText(DateUtil.getMonthDate(date));
                break;
            default:
                textInput.setText(DateUtil.DATE_FORMAT.format(date));
                break;
        }
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

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public void setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    /**
     * Добавляем хэндлер изменения даты
     * @param handler
     * @return
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
