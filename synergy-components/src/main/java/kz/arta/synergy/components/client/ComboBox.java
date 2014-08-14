package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.InputWithEvents;
import kz.arta.synergy.components.client.input.events.TextChangedEvent;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.menu.filters.ListTextFilter;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 15.07.14
 * Time: 14:58
 * Комбо-бокс
 */
public class ComboBox<V> extends Composite implements HasEnabled, HasValueChangeHandlers<V>, HasText{

    /**
     * Выпадающий список
     */
    private DropDownList<V> list;

    /**
     * Текст
     */
    private InputWithEvents input;

    /**
     * Отключен или включен комбобокс
     */
    private boolean isEnabled;

    /**
     * Можно ли вводить значения в комбобокс для поиска нужных значений
     */
    private boolean isReadOnly;

    /**
     * Фильтр для списка
     */
    private ListTextFilter filter = ListTextFilter.createPrefixFilter();

    private DropDownList<V>.Item selectedItem;
    private final EventBus bus;

    public ComboBox() {
        final ArtaFlowPanel root = new ArtaFlowPanel();

        initWidget(root);

        root.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (isEnabled && isReadOnly) {
                    root.addStyleName(SynergyComponents.resources.cssComponents().pressed());
                }
            }
        });
        root.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (isEnabled) {
                    root.removeStyleName(SynergyComponents.resources.cssComponents().pressed());
                    if (!list.isShowing()) {
                        filter.setText("");
                        list.show(selectedItem);
                    } else {
                        list.hide();
                    }
                }
            }
        });
        root.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                root.removeStyleName(SynergyComponents.resources.cssComponents().pressed());
            }
        });

        isEnabled = true;

        bus = new SimpleEventBus();

        list = new DropDownList<V>(this, bus);
        list.setRelativeWidget(this);
        list.setFilter(filter);

        ListSelectionEvent.register(bus, new ListSelectionEvent.Handler<V>() {
            @Override
            public void onSelection(ListSelectionEvent<V> event) {
                selectItem(event.getItem());
                list.hide();
            }
        });

        input = new InputWithEvents(bus);

        input.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        input.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                if (!isReadOnly) {
                    addStyleName(SynergyComponents.resources.cssComponents().focus());
                } else {
                    //этот хак нужен для firefox
                    //firefox показывает курсор при фокусировке readonly элемента,
                    //поэтому надо предотвращать фокусировку
                    input.setFocus(false);
                }
            }
        });
        //при blur поля ввода - возвращаем выбранное значение
        input.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (selectedItem != null) {
                    input.setText(selectedItem.getText(), false);
                }
                removeStyleName(SynergyComponents.resources.cssComponents().focus());
            }
        });
        input.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_DOWN:
                        if (!list.isShowing()) {
                            list.show(selectedItem);
                        }
                        break;
                }
            }
        });

        TextChangedEvent.register(bus, new TextChangedEvent.Handler() {
            @Override
            public void onTextChanged(TextChangedEvent event) {
                textChanged();
            }
        });

        ImageButton dropDownButton = new ImageButton(ImageResources.IMPL.comboBoxDropDown());
        dropDownButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        root.add(input);
        root.add(dropDownButton);

        setStyleName(SynergyComponents.resources.cssComponents().comboBox());
        addStyleName(SynergyComponents.resources.cssComponents().mainText());
        setWidth(Constants.FIELD_WITH_BUTTON_MIN_WIDTH);
    }

    /**
     * Метод вызывается при изменении текста комбобокса и выполняет действия для
     * изменения контента выпадающего списка
     */
    private void textChanged() {
        filter.setText(input.getText());
        if (!list.isShowing()) {
            list.show(selectedItem);
        }
    }

    /**
     * Выбирает элемент списка
     * @param item элемент списка
     */
    private void selectItem(DropDownList<V>.Item item) {
        selectItem(item, true);
    }

    /**
     * Выбирает элемент списка
     * @param item элемент списка
     * @param fireEvents создавать ли события о выборе элемента
     */
    private void selectItem(DropDownList<V>.Item item, boolean fireEvents) {
        if (item == null) {
            return;
        }

        selectedItem = item;
        input.setText(item.getText());
        if (fireEvents) {
            ValueChangeEvent.fire(this, item.getValue());
        }
    }

    /**
     * Выбирает элемент с заданным значением
     * @param value значение
     * @param fireEvents создавать ли события о выборе элемента
     */
    public void selectValue(V value, boolean fireEvents) {
        selectItem(list.get(value), fireEvents);
    }

    /**
     * Добавить элемент в список комбобокса
     * @param text текст элемента
     */
    public void addItem(String text, V value) {
        list.addItem(text, value);
    }

    /**
     * Добавить элемент в список комбобокса.
     * @param text текст элемента
     * @param iconResource иконка элемента в списке
     */
    public void addItem(String text, ImageResource iconResource, V value) {
        list.addItem(text, iconResource, value);
    }

    /**
     * Удаляет все элементы
     */
    public void clear() {
        list.clear();
        selectedItem = null;
    }
    /**
     * Удалить элемент с заданным значением
     * @param value значение
     */
    public void remove(V value) {
        list.remove(value);
    }

    /**
     * Содержит ли комбобокс это значение
     * @param value значение
     */
    public boolean contains(V value) {
        return list.contains(value);
    }

    /**
     * Возвращает значение выбранного элемента комбобокса
     * @return выбранное значение
     */
    public V getSelectedValue() {
        DropDownList<V>.Item item = list.getFocusedItem();
        if (item != null) {
            return item.getValue();
        } else {
            return null;
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Отключает или включаем комбобокс
     */
    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        input.setEnabled(enabled);
        if (!enabled) {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        }
    }

    /**
     * Изменяет состояние read-only комбобокса.
     * @param readOnly true - нельзя вводить значения, false - можно.
     */
    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        input.setReadOnly(readOnly);

    }

    /**
     * Возвращает текст в комбобоксе. Обычно это текст выбранного элемента, но
     * может быть промежуточный текст в случае editable комбобокса.
     * @return текст в комбобоксе
     */
    @Override
    public String getText() {
        return input.getText();
    }

    /**
     * Задает текст поля ввода в комбобоксе.
     * Возможно создание событий изменения текста.
     * @param text новый текст комбобокса
     */
    @Override
    public void setText(String text) {
        input.setText(text, true);
    }

    /**
     * Ширина должна задаваться в пикселях через этот метод.
     * Изменяется только ширина textbox.
     */
    public void setWidth(int width) {
        width = Math.max(Constants.FIELD_WITH_BUTTON_MIN_WIDTH, width);
        // -1 потому что правая граница кнопки перекрывает границу комбобокса
        input.setWidth(width - Constants.BUTTON_MIN_WIDTH -
                Constants.COMMON_INPUT_PADDING * 2 - 1 + "px");
        super.setWidth(width + "px");
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("ширина должна задаваться в пикселях");
    }

    /**
     * Добавляет хэндлер, который вызывается при выборе значения
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler) {
        return bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
    }
}

