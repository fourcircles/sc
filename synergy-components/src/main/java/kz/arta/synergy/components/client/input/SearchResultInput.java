package kz.arta.synergy.components.client.input;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.events.TextChangedEvent;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.events.HasSelectionEventHandlers;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.menu.events.SelectionEvent;
import kz.arta.synergy.components.client.menu.filters.ListTextFilter;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 07.08.14
 * Time: 14:12
 *
 * Поле с результатами поиска
 */
public class SearchResultInput<V> extends Composite implements HasSelectionEventHandlers<V>, HasClickHandlers{
    private final String placeholderText = Messages.i18n().tr("Поиск");

    private EventBus innerBus;
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
    private InputWithEvents input;

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
    private DropDownList<V>.Item selectedItem;

    public SearchResultInput() {
        this(true);
    }

    public SearchResultInput(boolean hasButton) {
        FlowPanel root = new FlowPanel();
        initWidget(root);

        setStyleName(SynergyComponents.getResources().cssComponents().searchResult());
        addStyleName(SynergyComponents.getResources().cssComponents().mainText());

        innerBus = new SimpleEventBus();

        input = new InputWithEvents(innerBus);
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

        //Выбор элемента списка
        ListSelectionEvent.register(innerBus, new ListSelectionEvent.Handler<V>() {
            @Override
            public void onSelection(final ListSelectionEvent<V> event) {
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
            }
        });

        //Ввод текста
        TextChangedEvent.register(innerBus, new TextChangedEvent.Handler() {
            @Override
            public void onTextChanged(TextChangedEvent event) {
                filter.setText(event.getNewText());
                if (!list.isShowing()) {
                    list.show(selectedItem);
                }
            }
        });

        input.addStyleName(SynergyComponents.getResources().cssComponents().placeHolder());
        input.setText(Messages.i18n().tr("Поиск"), false);

        input.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN && !list.isShowing()) {
                    filter.setText("");
                    list.show(selectedItem);
                }
            }
        });

        input.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (selectedItem != null) {
                    input.setText(selectedItem.getText(), false);
                } else {
                    input.setText(placeholderText, false);
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
    public void setList(DropDownList<V> list) {
        this.list = list;
        list.setBus(innerBus);
        list.setFilter(filter);
        list.setRelativeWidget(this);
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
    public HandlerRegistration addSelectionHandler(SelectionEvent.Handler<V> handler) {
        return innerBus.addHandlerToSource(SelectionEvent.TYPE, this, handler);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return button.addClickHandler(handler);
    }
}
