package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SimplePanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.menu.DropDownListMulti;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.menu.events.MenuItemSelection;
import kz.arta.synergy.components.client.input.InputWithEvents;
import kz.arta.synergy.components.client.input.events.TextChangedEvent;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.menu.filters.ListTextFilter;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.List;

/**
 * User: vsl
 * Date: 24.07.14
 * Time: 16:20
 *
 * Поле с тегами
 */
public class TagInput<V> extends TagsContainer<V> implements HasText,
        TagAddEvent.HasHandler<V>, TagRemoveEvent.HasHandler<V>, HasEnabled {
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
    InputWithEvents input;

    /**
     * Кнопка поля
     */
    protected ImageButton mainButton;

    /**
     * Панель для отображения тегов, которые не скрыты в индикаторе
     */
    protected TagsPanel<V> tagsPanel;

    /**
     * Ширина элемента для ввода текста (без всех отступов и границ)
     */
    private int inputWidth;

    /**
     * Шинира элемента вместе с границей
     */
    private int offsetWidth;

    /**
     * Выпадающий список для поля
     */
    protected DropDownListMulti<Tag<V>> list;

    private boolean listEnabled = false;


    private ListTextFilter filter = ListTextFilter.createPrefixFilter();

    /**
     * Имеет ли поле кнопку
     */
    private boolean hasButton;

    public TagInput() {
        this(true);
    }

    /**
     * @param hasButton имеет ли кнопку
     */
    public TagInput(boolean hasButton) {
        super();

        root = new FlowPanel();
        initWidget(root);

        this.hasButton = hasButton;

        addStyleName(SynergyComponents.getResources().cssComponents().mainText());
        addStyleName(SynergyComponents.getResources().cssComponents().tagInput());
        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        input = new InputWithEvents(innerBus);

        inputBox = new SimplePanel(input);
        inputBox.getElement().getStyle().setPaddingLeft(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
        inputBox.getElement().getStyle().setPaddingRight(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);

        root.add(inputBox);

        tagsPanel = new TagsPanel<V>(innerBus, 0);

        if (hasButton) {
            mainButton = createButton();
            root.add(mainButton);
            setWidth(Constants.FIELD_WITH_BUTTON_MIN_WIDTH);
        } else {
            setWidth(Constants.TAG_INPUT_NO_BUTTON_MIN_WIDTH);
        }

        root.add(tagsPanel);

        //Изменение текста в поле
        TextChangedEvent.register(innerBus, new TextChangedEvent.Handler() {
            @Override
            public void onTextChanged(TextChangedEvent event) {
                textChanged();
            }
        });

        //Кнопки "вниз" и "enter"
        input.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_ENTER:
                        keyEnter();
                        break;
                    case KeyCodes.KEY_DOWN:
                        if (listEnabled && !list.isShowing()) {
                            list.showUnder(TagInput.this);
                        }
                        break;
                    default:
                }
            }
        });
        input.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                addStyleName(SynergyComponents.getResources().cssComponents().focus());
            }
        });
        input.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                removeStyleName(SynergyComponents.getResources().cssComponents().focus());
            }
        });

        //Удаление тега
        TagRemoveEvent.register(innerBus, new TagRemoveEvent.Handler<V>() {
            @Override
            public void onTagRemove(TagRemoveEvent<V> event) {
                new Timer() {
                    @Override
                    public void run() {
                        tagRemoved();
                    }
                }.schedule(20);
            }
        });

        list = new DropDownListMulti<Tag<V>>();
        list.setLeftRightNavigation(false);
        list.setFilter(filter);

        list.removeAutoHidePartner(this.getElement());
        if (hasButton) {
            list.addAutoHidePartner(mainButton.getElement());
        }

        //Выбор элемента в списке
        list.addItemSelectionHandler(new MenuItemSelection.Handler<Tag<V>>() {
            @Override
            public void onItemSelection(MenuItemSelection<Tag<V>> event) {
                onListSelection(event.getItem(), event.isSelected());
            }
        });
    }

    private ImageButton createButton() {
        ImageButton button = new ImageButton(ImageResources.IMPL.zoom());
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onButtonClick(event);
            }
        });
        return button;
    }

    /**
     * При клике по кнопке поля с тегами
     * @param event событие клика
     */
    private void onButtonClick(ClickEvent event) {
        if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
            return;
        }
        if (listEnabled) {
            if (list.isShowing()) {
                list.hide();
            } else {
                list.showUnder(this);
            }
        }
    }

    public void addListItem(V value, String text) {
        Tag<V> tag = new Tag<V>(text, value);
        tag.setBus(innerBus);

        final MenuItem<Tag<V>> newItem = new MenuItem<Tag<V>>(tag, text);
        tag.addTagRemoveHandler(new TagRemoveEvent.Handler<V>() {
            @Override
            public void onTagRemove(TagRemoveEvent<V> event) {
                newItem.setValue(false, false);
            }
        });

        list.addItem(newItem);
    }

    protected MenuItem<Tag<V>> getListItem(V value) {
        if (value == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            MenuItem<Tag<V>> listItem = list.getItemAt(i);
            //noinspection NonJREEmulationClassesInClientCode
            if (value.equals(listItem.getUserValue().getValue())) {
                return listItem;
            }
        }
        return null;
    }

    public void removeListItem(V value) {
        MenuItem<Tag<V>> item = getListItem(value);
        if (item != null) {
            list.removeItem(item);
        }
    }

    public void removeListItem(int index) {
        if (index >= 0 && index < list.size()) {
            list.removeItem(list.getItemAt(index));
        }
    }

    protected void onListSelection(final MenuItem<Tag<V>> item, boolean select) {
        if (select) {
            addTag(item.getUserValue());
            input.setText("");
            list.noFocused();
            input.setFocus(true);
        } else {
            removeTag(item.getUserValue());
        }
        setInputOffset(tagsPanel.getOffsetWidth());
    }

    protected void tagRemoved() {
        input.setText("");
        setInputOffset(tagsPanel.getOffsetWidth());
        input.setFocus(true);
    }

    /**
     * Действия при нажатии клавиши "Enter".
     * Добавляется dummy тег.
     */
    protected void keyEnter() {
        if (!getText().isEmpty()) {
            Tag<V> tag = Tag.createDummy(input.getText());
            tag.setBus(innerBus);
            addTag(tag);

            input.setText("");
            setInputOffset(tagsPanel.getOffsetWidth());
            if (listEnabled) {
                list.hide();
            }
        }
    }

    /**
     * Метод вызывается при изменении текста в поле ввода.
     * Если текст слишком длинный - теги сдвигаются влево и поле ввода увеличивается.
     */
    private void textChanged() {
        int textWidth = Utils.impl().getTextWidth(input);

        //ширина поля ввода текста
        int startTextWidth = getAvailableSpace() - tagsPanel.getTagsWidth();

        if (textWidth > startTextWidth) {
            //ограничено сверху шириной поля с тегами
            textWidth = Math.min(textWidth, getAvailableSpace() - 8);

            tagsPanel.setOffset(textWidth - startTextWidth);
        } else {
            tagsPanel.clearOffset();
        }
        setInputOffset(tagsPanel.getOffsetWidth());
        filter.setText(input.getText());

        if (listEnabled) {
            list.showUnder(this);
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
    protected void setInputOffset(int offset) {
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            inputBox.getElement().getStyle().setPaddingRight(offset, Style.Unit.PX);
        } else {
            inputBox.getElement().getStyle().setPaddingLeft(offset, Style.Unit.PX);
        }
        inputWidth = getAvailableSpace() - offset;
        inputBox.setWidth(inputWidth + "px");
    }

    public List<Tag<V>> getTags() {
        return tagsPanel.getTags();
    }

    /**
     * Задавать ширину поля можно только этим методом указывая ширину в пикселях
     * @param width ширина в пикселях
     */
    public void setWidth(int width) {
        this.offsetWidth = width;
        int newInputWidth = width;
        newInputWidth -= Constants.BORDER_WIDTH * 2;
        super.setWidth(newInputWidth + "px");

        if (hasButton) {
            newInputWidth -= Constants.IMAGE_BUTTON_WIDTH + 1;
        }
        newInputWidth -= Constants.COMMON_INPUT_PADDING * 2;

        this.inputWidth = newInputWidth;
        inputBox.setWidth(newInputWidth + "px");

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

    @Override
    public boolean isEnabled() {
        return input.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            root.addStyleName(SynergyComponents.getResources().cssComponents().disabled());
        } else {
            root.removeStyleName(SynergyComponents.getResources().cssComponents().disabled());
        }
        input.setEnabled(enabled);
        if (hasButton) {
            mainButton.setEnabled(enabled);
        }
        tagsPanel.setEnabled(enabled);
    }

    @Override
    public HandlerRegistration addTagAddHandler(TagAddEvent.Handler<V> handler) {
        return innerBus.addHandlerToSource(TagAddEvent.TYPE, this, handler);
    }

    @Override
    public HandlerRegistration addTagRemoveHandler(TagRemoveEvent.Handler<V> handler) {
        return innerBus.addHandlerToSource(TagRemoveEvent.TYPE, this, handler);
    }

    public void setListFilter(ListTextFilter filter) {
        this.filter = filter;
        if (list != null) {
            list.setFilter(filter);
        }
        if (filter != null) {
            filter.setText(getText());
        }
    }

    public void setListEnabled(boolean listEnabled) {
        this.listEnabled = listEnabled;
    }

}
