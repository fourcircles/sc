package kz.arta.synergy.components.client.input;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SimplePanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.comments.events.InputChangeEvent;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;
import kz.arta.synergy.components.client.menu.filters.ListTextFilter;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.style.client.Constants;

//todo написать тесты

/**
 * User: vsl
 * Date: 07.08.14
 * Time: 14:12
 *
 * Поле с результатами поиска
 */
public class SearchResultInput<V> extends Composite implements HasClickHandlers, HasValue<V>{
    private final String placeholderText = Messages.i18n().tr("Поиск");

    /**
     * Кнопка
     */
    private ImageButton button = null;

    /**
     * Контейнер с отступами для поля ввода
     */
    private SimplePanel inputContainer;

    /**
     * Поле ввода
     */
    private TextInput input;

    /**
     * Список
     */
    private DropDownList<V> list;

    /**
     * Фильтр для списка
     */
    private ListTextFilter filter = ListTextFilter.createPrefixFilter();

    /**
     * Выбранный элемент списка
     */
    private MenuItem<V> selectedItem;

    public SearchResultInput() {
        this(true);
    }

    /**
     * @param hasButton есть ли индикатор
     */
    public SearchResultInput(boolean hasButton) {
        FlowPanel root = new FlowPanel();
        initWidget(root);

        setStyleName(SynergyComponents.getResources().cssComponents().searchResult());
        addStyleName(SynergyComponents.getResources().cssComponents().mainText());

        input = new TextInput();
        inputContainer = new SimplePanel(input);
        inputContainer.getElement().getStyle().setPaddingLeft(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
        inputContainer.getElement().getStyle().setPaddingRight(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
        inputContainer.getElement().getStyle().setProperty("boxSizing", "border-box");
        inputContainer.getElement().getStyle().setProperty("MozBoxSizing", "border-box");
        inputContainer.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        inputContainer.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        //ie 9 не отображает курсор если этого не сделать
        if (LocaleInfo.getCurrentLocale().isRTL() && Navigator.isIE()) {
            input.getElement().getStyle().setPaddingRight(1, Style.Unit.PX);
            inputContainer.getElement().getStyle().setPaddingRight(Constants.COMMON_INPUT_PADDING - 1, Style.Unit.PX);
        }

        root.add(inputContainer);

        if (hasButton) {
            button = new ImageButton(ImageResources.IMPL.zoomTransparent());
            root.add(button);
        }

        InputChangeEvent.addInputHandler(input.getElement(), new InputChangeEvent.Handler() {
            @Override
            public void onInputChange(InputChangeEvent event) {
                filter.setText(input.getValue());
                if (list.isShowing()) {
                    list.showUnder(SearchResultInput.this);
                }
            }
        });

        input.addStyleName(SynergyComponents.getResources().cssComponents().placeHolder());
        input.setValue(Messages.i18n().tr("Поиск"), false);

        input.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN && !list.isShowing()) {
                    filter.setText("");
                    list.showUnder(SearchResultInput.this);
                }
            }
        });

        input.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (selectedItem != null) {
                    input.setValue(selectedItem.getText(), false);
                } else {
                    input.setValue(placeholderText, false);
                    input.addStyleName(SynergyComponents.getResources().cssComponents().placeHolder());
                }
            }
        });

        input.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                if (selectedItem == null) {
                    input.setText("");
                }
                input.removeStyleName(SynergyComponents.getResources().cssComponents().placeHolder());
            }
        });

        setWidth(Constants.FIELD_WITH_BUTTON_MIN_WIDTH);
    }

    /**
     * Задает список
     * @param list список
     */
    public void setList(final DropDownList<V> list) {
        this.list = list;
        list.setFilter(filter);

        list.addItemSelectionHandler(new MenuItemSelection.Handler<V>() {
            @Override
            public void onItemSelection(final MenuItemSelection<V> event) {
                input.setFocus(true);
                selectedItem = event.getItem();
                if (Navigator.isIE()) {
                    new Timer() {
                        @Override
                        public void run() {
                            input.setText(event.getItem().getText());
                            list.hide();
                        }
                    }.schedule(50);
                } else {
                    input.setText(event.getItem().getText());
                    list.hide();
                }
                ValueChangeEvent.fire(SearchResultInput.this, event.getItem().getUserValue());
            }
        });
    }

    /**
     * Задает полную ширину в пикселях
     * @param width ширина
     */
    public void setWidth(int width) {
        int innerWidth = width - Constants.BORDER_WIDTH * 2;

        if (button != null) {
            //не включаем сюда padding поля ввода, потому что border-box
            inputContainer.setWidth(innerWidth - Constants.IMAGE_BUTTON_WIDTH + "px");
        } else {
            inputContainer.setWidth(innerWidth + "px");
        }

        super.setWidth(width + "px");
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("ширина должна задаваться в пикселях");
    }

    /**
     * Включает/выключает поле
     */
    public void setEnabled(boolean enabled) {
        if (button != null) {
            button.setEnabled(enabled);
        }
        input.setEnabled(enabled);
        if (enabled) {
            removeStyleName(SynergyComponents.getResources().cssComponents().disabled());
        } else {
            addStyleName(SynergyComponents.getResources().cssComponents().disabled());
        }
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return button.addClickHandler(handler);
    }

    @Override
    public V getValue() {
        if (selectedItem != null) {
            return selectedItem.getUserValue();
        }
        return null;
    }

    @Override
    public void setValue(V value) {
        setValue(value, true);
    }

    @Override
    public void setValue(V value, boolean fireEvents) {
        MenuItem<V> item = list.get(value);
        if (fireEvents) {
            item.setValue(true, true);
        } else {
            item.setValue(true, false);
            input.setText(item.getText());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
