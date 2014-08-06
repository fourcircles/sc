package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

import java.util.HashMap;

//todo скроллить и выделять на выбранное значение при повторном открытии
/**
 * User: vsl
 * Date: 15.07.14
 * Time: 14:58
 * Комбо-бокс
 */
public class ComboBox<V> extends Composite implements HasEnabled, HasChangeHandlers, HasValueChangeHandlers<V>, HasText{
    /**
     * Основная панель
     */
    private FlowPanel panel;

    /**
     * Выпадающий список
     */
    private DropDownList list;

    /**
     * Текст
     */
    private TextInput textLabel;

    /**
     * Кнопка раскрытия списка
     */
    private ImageButton dropDownButton;

    /**
     * Отключен или включен комбобокс
     */
    private boolean isEnabled;

    /**
     * Можно ли вводит значения в комбобокс для поиска нужных значений
     */
    private boolean readOnly;

    /**
     * Обработка событий в зависимости от статуса read-only. При изменении статуса
     * прекращается обработка ненужных событий.
     */
    MouseOutHandler textLabelMouseOut;
    HandlerRegistration textLabelMouseOutRegistration;
    MouseDownHandler textLabelMouseDown;
    HandlerRegistration textLabelMouseDownRegistration;
    ClickHandler textLabelClick;
    HandlerRegistration textLabelClickRegistration;
    Timer textChangeTimer;
    KeyPressHandler textPressKey;
    HandlerRegistration textPressKeyRegistration;
    KeyUpHandler textUpKey;
    HandlerRegistration textUpKeyRegistration;

    private HashMap<DropDownList.Item, V> values;


    public ComboBox() {
        panel = new FlowPanel();
        values = new HashMap<DropDownList.Item, V>();

        initWidget(panel);

        isEnabled = true;

        EventBus bus = new SimpleEventBus();
        list = new DropDownList(this, bus);
        list.setRelativeWidget(this);

        ListSelectionEvent.register(bus, new ListSelectionEvent.Handler<Object>() {
            @Override
            public void onSelection(ListSelectionEvent<Object> event) {
                showItem(event.getItem());
                list.hide();
            }
        });

        textLabel = new TextInput();

        textLabel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        dropDownButton = new ImageButton(ImageResources.IMPL.comboBoxDropDown());
        dropDownButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        textLabelClick = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isEnabled) {
                    event.stopPropagation();
                    removeStyleName(SynergyComponents.resources.cssComponents().pressed());
                    if (list.isShowing()) {
                        list.hide();
                    } else {
                        list.removePrefix();
                        list.show();
                    }
                }
            }
        };
        textLabelMouseDown = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                //firefox показывает курсор даже если readonly, поэтому надо preventDefault
                event.preventDefault();
                addStyleName(SynergyComponents.resources.cssComponents().pressed());
            }
        };
        textLabelMouseOut = new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                removeStyleName(SynergyComponents.resources.cssComponents().pressed());
            }
        };

        textPressKey = new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() != KeyCodes.KEY_ENTER) {
                    new Timer() {
                        @Override
                        public void run() {
                            changeList();
                        }
                    }.schedule(20);
                }
            }
        };

        textUpKey = new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_BACKSPACE:
                    case KeyCodes.KEY_DELETE:
                    case KeyCodes.KEY_SPACE:
                        changeList();
                        break;
                    case KeyCodes.KEY_DOWN:
                        if (!list.isShowing()) {
                            list.show();
                            list.selectFirst();
                        }
                        break;
                }
            }
        };

        dropDownButton.addClickHandler(textLabelClick);

        panel.add(textLabel);
        panel.add(dropDownButton);

        setReadOnly(true);

        setStyleName(SynergyComponents.resources.cssComponents().comboBox());
        addStyleName(SynergyComponents.resources.cssComponents().mainText());
        setWidth(Constants.FIELD_WITH_BUTTON_MIN_WIDTH);
    }

    /**
     * Метод вызывается при изменении текста комбобокса и выполняет действия для
     * изменения контента выпадающего списка
     */
    private void changeList() {
        String prefix = textLabel.getText();
        if (prefix.isEmpty()) {
            list.removePrefix();
        } else {
            list.applyPrefix(prefix);
        }
        if (!list.isShowing()) {
            list.show();
        }
    }

    /**
     * Показать элемент списка в комбобоксе
     * @param item элемент списка
     */
    private void showItem(DropDownList.Item item) {
        textLabel.setText(item.getText());
        ValueChangeEvent.fire(this, values.get(item));
    }

    /**
     * Добавить элемент в список комбобокса
     * @param text текст элемента
     */
    public void addItem(String text, V value) {
        DropDownList.Item item = list.addItem(text, null);
        values.put(item, value);
    }

    /**
     * Добавить элемент в список комбобокса.
     * @param text текст элемента
     * @param iconResource иконка элемента в списке
     */
    public void addItem(String text, ImageResource iconResource, V value) {
        DropDownList.Item item = list.addItem(text, iconResource, null);
        values.put(item, value);
    }

    /**
     * Возвращает значение выбранного элемента комбобокса
     * @return выбранное значение
     */
    public V getSelectedValue() {
        DropDownList.Item item = list.getSelectedItem();
        if (item != null) {
            return values.get(list.getSelectedItem());
        } else {
            return null;
        }
    }

    /**
     * Возвращает текст выбранного пункта меню
     * @return выбранный текст
     */
    public String getSelectedText() {
        return list.getSelectedItem().getText();
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        if (!enabled) {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        }
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return textLabel.addChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Изменяет состояние read-only комбобокса.
     * @param readOnly true - нельзя вводить значения, false - можно.
     */
    public void setReadOnly(boolean readOnly) {
        if (readOnly != this.readOnly) {
            this.readOnly = readOnly;
            textLabel.setReadOnly(readOnly);
            //кнопки влево-вправо выделяют первый-последний элементы только для
            //readonly комбобокса
            list.setLeftRightKeysEnabled(readOnly);
            if (readOnly) {
                textLabelClickRegistration = textLabel.addClickHandler(textLabelClick);
                textLabelMouseDownRegistration = textLabel.addMouseDownHandler(textLabelMouseDown);
                textLabelMouseOutRegistration = textLabel.addMouseOutHandler(textLabelMouseOut);
                if (textPressKeyRegistration != null) {
                    textPressKeyRegistration.removeHandler();
                }
                if (textUpKeyRegistration != null) {
                    textUpKeyRegistration.removeHandler();
                }

            } else {
                textPressKeyRegistration = textLabel.addKeyPressHandler(textPressKey);
                textUpKeyRegistration = textLabel.addKeyUpHandler(textUpKey);

                if (textLabelClickRegistration != null) {
                    textLabelClickRegistration.removeHandler();
                }
                if (textLabelMouseDownRegistration != null) {
                    textLabelMouseDownRegistration.removeHandler();
                }
                if (textLabelMouseOutRegistration != null) {
                    textLabelMouseOutRegistration.removeHandler();
                }
            }
        }
    }

    @Override
    public String getText() {
        return getSelectedText();
    }

    @Override
    public void setText(String text) {
        textLabel.setText(text);
    }

    /**
     * Ширина должна задаваться в пикселях через этот метод.
     * Изменяется только ширина textbox.
     */
    public void setWidth(int width) {
        width = Math.max(Constants.FIELD_WITH_BUTTON_MIN_WIDTH, width);
        // -1 потому что правая граница кнопки перекрывает границу комбобокса
        textLabel.setWidth(width - Constants.BUTTON_MIN_WIDTH -
                Constants.COMMON_INPUT_PADDING * 2 - 1 + "px");
        super.setWidth(width + "px");
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("ширина должна задаваться в пикселях");
    }
}

