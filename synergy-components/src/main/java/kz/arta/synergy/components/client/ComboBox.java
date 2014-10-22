package kz.arta.synergy.components.client;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.comments.events.InputChangeEvent;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.client.menu.filters.ListTextFilter;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 15.07.14
 * Time: 14:58
 * Комбо-бокс
 */
public class ComboBox<V> extends Composite implements HasEnabled, HasValueChangeHandlers<V>, HasText, HasValue<V> {

    /**
     * Выпадающий список
     */
    private DropDownList<V> list;

    /**
     * Текст
     */
    private TextInput input;

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

    private MenuItem<V> selectedItem;

    public ComboBox() {
        final ArtaFlowPanel root = new ArtaFlowPanel();

        initWidget(root);

        root.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                if (isEnabled && isReadOnly) {
                    root.addStyleName(SynergyComponents.getResources().cssComponents().pressed());
                }
            }
        });
        root.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                if (isEnabled) {
                    root.removeStyleName(SynergyComponents.getResources().cssComponents().pressed());
                    if (!list.isShowing()) {
                        filter.setText("");
                        list.showUnder(ComboBox.this);
                    } else {
                        list.hide();
                    }
                }
            }
        });
        root.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                root.removeStyleName(SynergyComponents.getResources().cssComponents().pressed());
            }
        });

        isEnabled = true;

        list = new DropDownList<V>();
        list.setLeftRightNavigation(false);
        list.setFilter(filter);
        list.addAutoHidePartner(getElement());
        list.addItemSelectionHandler(new MenuItemSelection.Handler<V>() {
            @Override
            public void onItemSelection(MenuItemSelection<V> event) {
                selectItem(event.getItem(), true);
            }
        });

        input = new TextInput();

        input.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        input.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                if (!isReadOnly) {
                    addStyleName(SynergyComponents.getResources().cssComponents().focus());
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
                    input.setValue(selectedItem.getText(), false);
                }
                removeStyleName(SynergyComponents.getResources().cssComponents().focus());
            }
        });
        InputChangeEvent.addInputHandler(input.getElement(), new InputChangeEvent.Handler() {
            @Override
            public void onInputChange(InputChangeEvent event) {
                textChanged();
            }
        });
        input.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
                    if (!list.isShowing()) {
                        filter.setText("");
                        list.showUnder(ComboBox.this);
                    }
                }
            }
        });

        ImageButton dropDownButton = new ImageButton(ImageResources.IMPL.comboBoxDropDown());
        dropDownButton.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        root.add(input);
        root.add(dropDownButton);

        setStyleName(SynergyComponents.getResources().cssComponents().comboBox());
        addStyleName(SynergyComponents.getResources().cssComponents().mainText());
        setWidth(Constants.FIELD_WITH_BUTTON_MIN_WIDTH);
    }

    /**
     * Метод вызывается при изменении текста комбобокса и выполняет действия для
     * изменения контента выпадающего списка
     */
    private void textChanged() {
        filter.setText(input.getText());
        if (!list.isShowing()) {
            list.showUnder(this);
        }
    }

    /**
     * Выбирает элемент списка
     * @param item элемент списка
     */
    private void selectItem(MenuItem<V> item) {
        selectItem(item, true);
    }

    /**
     * Выбирает элемент списка
     * @param item элемент списка
     * @param fireEvents создавать ли события о выборе элемента
     */
    private void selectItem(MenuItem<V> item, boolean fireEvents) {
        if (item == null) {
            return;
        }
        if (selectedItem != null) {
            selectedItem.setValue(false, false);
        }
        item.setValue(true, false);
        selectedItem = item;
        input.setText(item.getText());
        if (fireEvents) {
            ValueChangeEvent.fire(this, item.getUserValue());
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
        list.add(new MenuItem<V>(value, text));
    }

    /**
     * Добавить элемент в список комбобокса.
     * @param text текст элемента
     * @param iconResource иконка элемента в списке
     */
    public void addItem(String text, ImageResource iconResource, V value) {
        list.add(new MenuItem<V>(value, text, iconResource));
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
        return selectedItem.getUserValue();
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
            addStyleName(SynergyComponents.getResources().cssComponents().disabled());
        } else {
            removeStyleName(SynergyComponents.getResources().cssComponents().disabled());
        }
    }

    /**
     * Изменяет состояние read-only комбобокса.
     * @param readOnly true - нельзя вводить значения, false - можно.
     */
    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        input.setReadOnly(readOnly);
        if (readOnly) {
            list.setLeftRightNavigation(true);
        } else {
            list.setLeftRightNavigation(false);
        }
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
        input.setValue(text, true);
    }

    /**
     * Ширина должна задаваться в пикселях через этот метод.
     * Изменяется только ширина textbox.
     */
    public void setWidth(int newWidth) {
        int width = Math.max(Constants.FIELD_WITH_BUTTON_MIN_WIDTH, newWidth);
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
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public V getValue() {
        return selectedItem.getUserValue();
    }

    @Override
    public void setValue(V value) {
        setValue(value, true);
    }

    @Override
    public void setValue(V value, boolean fireEvents) {
        if (list.contains(value)) {
            selectValue(value, fireEvents);
        }
    }
}

