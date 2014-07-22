package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.MenuBase;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

import java.util.HashMap;

//todo mousedown on textfield -> moveout - dropdownbutton is still pressed
/**
 * User: vsl
 * Date: 15.07.14
 * Time: 14:58
 * Комбо-бокс
 */
public class ComboBox<V> extends Composite implements HasEnabled, HasChangeHandlers, HasValueChangeHandlers<V>, HasText{
    /**
     * Промежуток времени между изменением текста и изменением списка
     */
    private static final int DELAY = 800;

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
    MouseDownHandler textLabelMouseDown;
    HandlerRegistration textLabelMouseDownRegistration;
    ClickHandler textLabelClick;
    HandlerRegistration textLabelClickRegistration;
    Timer textChangeTimer;
    KeyPressHandler textPressKey;
    HandlerRegistration textPressKeyRegistration;
    KeyUpHandler textUpKey;
    HandlerRegistration textUpKeyRegistration;

    private HashMap<MenuBase.MenuItem, V> values;


    public ComboBox() {
        panel = new FlowPanel();
        values = new HashMap<MenuBase.MenuItem, V>();

        initWidget(panel);

        isEnabled = true;

        list = new DropDownList(this) {
            @Override
            protected void itemSelected(MenuItem item) {
                super.itemSelected(item);
                showItem(item);
            }
        };
        list.setRelativeWidget(this);

        textLabel = new TextInput();
        // -1 потому что правая граница кнопки перекрывает границу комбобокса
        textLabel.setWidth(Constants.COMBO_MIN_WIDTH - Constants.BUTTON_MIN_WIDTH -
                Constants.COMMON_INPUT_PADDING * 2 - 1 + "px");
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
                        list.showUnderParent();
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


        textChangeTimer = new Timer() {
            @Override
            public void run() {
                String prefix = textLabel.getText();
                if (prefix.isEmpty()) {
                    list.removePrefix();
                } else {
                    String firstItemText = list.applyPrefix(prefix);
                    if (firstItemText != null) {
                        textLabel.setText(firstItemText);
                        textLabel.setCursorPos(prefix.length());
                        textLabel.setSelectionRange(prefix.length(), firstItemText.length() - prefix.length());
                    }
                    list.showUnderParent();
                }
            }
        };

        textPressKey = new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                textChangeTimer.cancel();
                textChangeTimer.schedule(DELAY);
            }
        };

        textUpKey = new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_BACKSPACE:
                    case KeyCodes.KEY_DELETE:
                        textChangeTimer.cancel();
                        textChangeTimer.schedule(DELAY);
                        break;
                    case KeyCodes.KEY_DOWN:
                        list.showUnderParent();
                        textChangeTimer.cancel();
                        textChangeTimer.schedule(DELAY);
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
        setWidth(Constants.COMBO_MIN_WIDTH + "px");
    }

    /**
     * Показать элемент списка в комбобоксе
     * @param item элемент списка
     */
    private void showItem(MenuBase.MenuItem item) {
        textLabel.setText(item.getText());
        ValueChangeEvent.fire(this, values.get(item));
    }

    /**
     * Добавить элемент в список комбобокса
     * @param text текст элемента
     */
    public void addItem(String text, V value) {
        MenuBase.MenuItem item = list.addItem(text);
        values.put(item, value);
    }

    /**
     * Добавить элемент в список комбобокса.
     * @param text текст элемента
     * @param iconResource иконка элемента в списке
     */
    public void addItem(String text, ImageResource iconResource, V value) {
        MenuBase.MenuItem item = list.addItem(text, iconResource);
        values.put(item, value);
    }

    /**
     * Возвращает значение выбранного элемента комбобокса
     * @return выбранное значение
     */
    public V getSelectedValue() {
        MenuBase.MenuItem item = list.getSelectedItem();
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
            if (readOnly) {
                textLabelClickRegistration = textLabel.addClickHandler(textLabelClick);
                textLabelMouseDownRegistration = textLabel.addMouseDownHandler(textLabelMouseDown);
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
}

