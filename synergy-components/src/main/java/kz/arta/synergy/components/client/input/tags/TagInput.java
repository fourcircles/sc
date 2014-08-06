package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.InputWithEvents;
import kz.arta.synergy.components.client.input.events.TextChangedEvent;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.DropDownListMulti;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.menu.filters.ListFilter;
import kz.arta.synergy.components.client.menu.filters.ListTextFilter;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: vsl
 * Date: 24.07.14
 * Time: 16:20
 *
 * Поле с тегами
 */
public class TagInput<V> extends Composite implements HasText,
        TagAddEvent.HasHandler, TagRemoveEvent.HasHandler, HasEnabled {
    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Контейнер для поля ввода. Нужен для сохранения отступов в IE9
     */
    private SimplePanel inputBox;

    /**
     * Элемент для ввода текста
     */
    private InputWithEvents input;

    /**
     * Кнопка поля
     */
    private ImageButton button;

    /**
     * Панель для отображения тегов, которые не скрыты в индикаторе
     */
    private TagsPanel tagsPanel;

    /**
     * Список всех добавленных тегов
     */
    private ArrayList<Tag> tags;

    /**
     * Ширина элемента для ввода текста (без всех отступов и границ)
     */
    private int inputWidth;

    /**
     * Шинира элемента вместе с границей
     */
    private int offsetWidth;

    /**
     * Сдвиг поля ввода от левого края.
     * Изменяется при добавлении тега и вводе длинного текста.
     */
    private int inputOffset;

    /**
     * Сдвиг панели тегов от левого края поля.
     * Может быть отрицательным при наборе длинного текста в поле ввода.
     */
    private int tagsPanelOffset = 0;

    /**
     * Выпадающий список для поля
     */
    private DropDownListMulti<V> dropDownList;

    private ListTextFilter filter = ListTextFilter.createPrefixFilter();

    private EventBus innerBus;
    private HandlerRegistration buttonRegistration;

    /**
     * Имеет ли поле кнопку
     */
    private boolean hasButton;

    /**
     * Хэндлер для клика кнопки
     */
    private ClickHandler buttonClick;

    /**
     * Мультикомбобокс отличается от поля с тегами у которого есть список и кнопка двумя вещами
     * 1. Картинкой кнопки
     * 2. После выбора значения из списка список не закрывается
     */
    private boolean isMultiComboBox;

    private HashMap<Tag<V>, DropDownList<V>.Item> tagsToItems;
    private HashMap<DropDownList<V>.Item, Tag<V>> itemsToTags;

    public TagInput() {
        this(true, true);
    }

    /**
     * @param hasIndicator имеет ли индикатор скрытых тегов
     */
    public TagInput(boolean hasIndicator) {
        this(hasIndicator, true);
    }

    /**
     * @param hasIndicator имеет ли индикатор скрытых тегов
     * @param hasButton имеел ли кнопку
     */
    public TagInput(boolean hasIndicator, boolean hasButton) {
        root = new FlowPanel();
        initWidget(root);

        innerBus = new SimpleEventBus();
        tags = new ArrayList<Tag>();

        this.hasButton = hasButton;

        addStyleName(SynergyComponents.resources.cssComponents().mainText());
        addStyleName(SynergyComponents.resources.cssComponents().tagInput());
        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        input = new InputWithEvents(innerBus);

        inputBox = new SimplePanel(input);
        inputBox.getElement().getStyle().setPaddingLeft(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
        inputBox.getElement().getStyle().setPaddingRight(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);

        root.add(inputBox);

        if (hasButton) {
            button = new ImageButton(ImageResources.IMPL.zoom());

            buttonClick = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (dropDownList != null) {
                        if (dropDownList.isShowing()) {
                            dropDownList.hide();
                        } else {
                            dropDownList.show();
                        }
                    }
                }
            };
            buttonRegistration = button.addClickHandler(buttonClick);
            root.add(button);
        }

        tagsPanel = new TagsPanel(innerBus, 0, true);
        tagsPanel.setHasIndicator(hasIndicator);

        if (hasButton) {
            setWidth(Constants.FIELD_WITH_BUTTON_MIN_WIDTH);
        } else {
            setWidth(Constants.TAG_INPUT_NO_BUTTON_MIN_WIDTH);
        }

        root.add(tagsPanel);

        TextChangedEvent.register(innerBus, new TextChangedEvent.Handler() {
            @Override
            public void onTextChanged(TextChangedEvent event) {
                textChanged();
            }
        });
        input.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_ENTER:
                        keyEnter();
                        break;
                    case KeyCodes.KEY_DOWN:
                        if (dropDownList != null && !dropDownList.isShowing()) {
                            dropDownList.show();
                        }
                }
            }
        });

        itemsToTags = new HashMap<DropDownList<V>.Item, Tag<V>>();
        tagsToItems = new HashMap<Tag<V>, DropDownList<V>.Item>();

        TagRemoveEvent.register(innerBus, new TagRemoveEvent.Handler() {
            @Override
            public void onTagRemove(TagRemoveEvent event) {
                if (dropDownList != null) {
                    DropDownListMulti<V>.Item item = (DropDownListMulti.Item) tagsToItems.get(event.getTag());
                    item.setSelected(false, false);

                    tagsToItems.remove(event.getTag());
                    itemsToTags.remove(item);
                }

                new Timer() {
                    @Override
                    public void run() {
                        input.setText("");
                        setInputOffset(Math.min(tagsPanel.getOffsetWidth(), getAvailableSpace()));
                        input.setFocus(true);
                    }
                }.schedule(20);
            }
        });

        ListSelectionEvent.register(innerBus, new ListSelectionEvent.Handler<V>() {
            @Override
            public void onSelection(final ListSelectionEvent<V> selectionEvent) {
                Tag<V> tag = new Tag<V>(selectionEvent.getItem().getText(), selectionEvent.getItem().getValue());
                tag.setBus(innerBus);

                tagsToItems.put(tag, selectionEvent.getItem());
                itemsToTags.put(selectionEvent.getItem(), tag);

                innerBus.fireEvent(new TagAddEvent(tag));

                input.setText("");
                setInputOffset(Math.min(tagsPanel.getOffsetWidth(), getAvailableSpace()));
                input.setFocus(true);
            }

            @Override
            public void onDeselection(ListSelectionEvent<V> event) {
                innerBus.fireEvent(new TagRemoveEvent(itemsToTags.get(event.getItem())));
                setInputOffset(tagsPanel.getOffsetWidth());
                input.setFocus(true);
            }
        });
    }

    /**
     * Действия при нажатии клавиши "Enter".
     * Если у поля нет списка, то это приводит к добавлению тега.
     */
    private void keyEnter() {
        if (dropDownList == null && !input.getText().isEmpty()) {
            Tag tag = new Tag(input.getText());
            tag.setBus(innerBus);
            innerBus.fireEvent(new TagAddEvent(tag));
            input.setText("");
            setInputOffset(tagsPanel.getOffsetWidth());

            if (Window.Navigator.getAppVersion().contains("MSIE")) {
                new Timer() {
                    @Override
                    public void run() {
                        setInputOffset(tagsPanel.getOffsetWidth());
                        input.setFocus(true);
                    }
                }.schedule(50);
            }
        }
    }

    /**
     * Метод вызывается при изменении текста в поле ввода.
     * Если текст слишком длинный - теги сдвигаются влево и поле ввода увеличивается.
     */
    private void textChanged() {
        int textWidth = Utils.getTextWidth(input);

        int startTextWidth = getAvailableSpace() - tagsPanel.getTagsWidth();
        if (textWidth > startTextWidth) {
            textWidth = Math.min(textWidth, getAvailableSpace() - 8);
            tagsPanelOffset = textWidth - startTextWidth;
            tagsPanel.setOffset(tagsPanelOffset);
        } else {
            tagsPanelOffset = 0;
            tagsPanel.clearOffset();
        }
        setInputOffset(tagsPanel.getOffsetWidth());
        filter.setText(input.getText());

        if (dropDownList != null) {
            if (input.getText().isEmpty()) {
                dropDownList.hide();
            } else {
                if (!dropDownList.isShowing()) {
                    dropDownList.show();
                }
            }
        }
    }

    /**
     * Ширина элемента без границ, кнопки и отступов.
     */
    private int getAvailableSpace() {
        int res = offsetWidth;
        if (hasButton) {
            res -= Constants.IMAGE_BUTTON_WIDTH + Constants.BORDER_WIDTH;
        }
        res -= 2 * Constants.BORDER_WIDTH + Constants.COMMON_INPUT_PADDING;
        return res;
    }

    /**
     * Задает элементу input расстояние от левой границы поля начиная с которого можно
     * вводить текст, причем offsetWidth элемента ввода не изменяется.
     * @param offset расстояние от левой границы поля
     */
    private void setInputOffset(int offset) {
        inputOffset = offset;
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            inputBox.getElement().getStyle().setPaddingRight(inputOffset, Style.Unit.PX);
//            input.getElement().getStyle().setPaddingRight(inputOffset, Style.Unit.PX);
        } else {
            inputBox.getElement().getStyle().setPaddingLeft(inputOffset, Style.Unit.PX);
//            input.getElement().getStyle().setPaddingLeft(inputOffset, Style.Unit.PX);
        }
        inputWidth = getAvailableSpace() - offset;
        inputBox.setWidth(inputWidth + "px");
    }
    /**
     * Возвращает тег находящийся на указанной позиции.
     * @param i позиция
     * @return тег
     */
    public Tag getTag(int i) {
        return tags.get(i);
    }

    /**
     * Задавать ширину поля можно только этим методом указывая ширину в пикселях
     * @param width ширина в пикселях
     */
    public void setWidth(int width) {
        this.offsetWidth = width;
        //минус граница
        width -= Constants.BORDER_WIDTH * 2;
        super.setWidth(width + "px");

        if (hasButton) {
            //минус кнопка
            width -= Constants.IMAGE_BUTTON_WIDTH + 1;
        }
        //минус padding поля ввода
        width -= Constants.COMMON_INPUT_PADDING * 2;

        inputWidth = width;
        inputBox.setWidth(width + "px");
        setInputOffset(Constants.COMMON_INPUT_PADDING);

        tagsPanel.setMaxWidth(getAvailableSpace() - 26);
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("надо использовать setWidth(int) для задания ширины");
    }

    @Override
    public String getText() {
        return input.getText();
    }

    @Override
    public void setText(String text) {
        input.setText(text);
        textChanged();
    }

    public DropDownListMulti<V> getDropDownList() {
        return dropDownList;
    }

    public void setDropDownList(final DropDownListMulti<V> dropDownList) {
        this.dropDownList = dropDownList;
        dropDownList.setFilter(filter);

        dropDownList.removeAutoHidePartner(this.getElement());
        if (hasButton) {
            dropDownList.addAutoHidePartner(button.getElement());
        }
        dropDownList.setBus(innerBus);
        dropDownList.setHideAfterSelect(!isMultiComboBox);

        if (isEnabled() && hasButton && buttonRegistration == null) {
            buttonRegistration = button.addClickHandler(buttonClick);
        }
    }

    @Override
    public boolean isEnabled() {
        return input.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled && buttonRegistration != null) {
            buttonRegistration.removeHandler();
        } else if (dropDownList != null && !isEnabled()) {
            buttonRegistration = button.addClickHandler(buttonClick);
        }

        root.addStyleName(SynergyComponents.resources.cssComponents().disabled());
        input.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    public boolean isMultiComboBox() {
        return isMultiComboBox;
    }

    public void setMultiComboBox(boolean isMultiComboBox) {
        this.isMultiComboBox = isMultiComboBox;
        if (isMultiComboBox) {
            if (button != null) {
                button.setIcon(ImageResources.IMPL.comboBoxDropDown());
            }
        }
        if (dropDownList != null) {
            dropDownList.setHideAfterSelect(!isMultiComboBox);
        }
    }

    @Override
    public HandlerRegistration addTagAddHandler(TagAddEvent.Handler handler) {
        return addHandler(handler, TagAddEvent.TYPE);
    }

    @Override
    public HandlerRegistration addTagRemoveHandler(TagRemoveEvent.Handler handler) {
        return addHandler(handler, TagRemoveEvent.TYPE);
    }

    /**
     * Задает тип фильтра.
     * Пока есть только два фильтра, поэтому выбор регулируется boolean переменной,
     * в будущем возможно сделать enum.
     * @param filterType true - префиксный фильтр, false - фильтр на содержание
     */
    public void setFilterType(boolean filterType) {
        if (filterType) {
            filter = ListTextFilter.createPrefixFilter();
        } else {
            filter = ListTextFilter.createContainsFilter();
        }
        if (dropDownList != null) {
            dropDownList.setFilter(filter);
        }
        filter.setText(input.getText());
    }
}
