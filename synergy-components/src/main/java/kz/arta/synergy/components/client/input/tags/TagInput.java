package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Timer;
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
 *
 * Добавление и удаление тегов происходит через соответствующие методы и
 * выбором элементов из списка (при отсутствии списка через поле ввода и enter).
 */
public class TagInput<V> extends Composite implements HasText,
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
    protected ImageButton button;

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
    protected DropDownListMulti<V> dropDownList;

    private ListTextFilter filter = ListTextFilter.createPrefixFilter();

    EventBus innerBus;

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
        root = new FlowPanel();
        initWidget(root);

        innerBus = new SimpleEventBus();

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

            button.addClickHandler(new ClickHandler() {
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
            });

            root.add(button);
        }

        tagsPanel = new TagsPanel<V>(innerBus, 0);

        if (hasButton) {
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
                        if (dropDownList != null && !dropDownList.isShowing()) {
                            dropDownList.show();
                        }
                }
            }
        });
        input.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                addStyleName(SynergyComponents.resources.cssComponents().focus());
            }
        });
        input.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                removeStyleName(SynergyComponents.resources.cssComponents().focus());
            }
        });

        //Удаление тега
        TagRemoveEvent.register(innerBus, new TagRemoveEvent.Handler<V>() {
            @Override
            public void onTagRemove(TagRemoveEvent<V> event) {
                Tag<V> tag = event.getTag();
                if (!tag.isDummy() && dropDownList.contains(tag.getValue())) {
                    ((DropDownListMulti.Item) dropDownList.get(tag.getValue())).setSelected(false, false);
                }
                new Timer() {
                    @Override
                    public void run() {
                        tagRemoved();
                    }
                }.schedule(20);
            }
        });

        //Выбор элемента в списке
        ListSelectionEvent.register(innerBus, new ListSelectionEvent.Handler<V>() {
            @Override
            public void onSelection(final ListSelectionEvent<V> selectionEvent) {
                Tag<V> tag = new Tag<V>(selectionEvent.getItem().getText(), selectionEvent.getItem().getValue());
                tag.setBus(innerBus);

                addTag(tag);
                setInputOffset(tagsPanel.getOffsetWidth());

                listItemSelected(selectionEvent.getItem());
            }

            @Override
            public void onDeselection(ListSelectionEvent<V> event) {
                for (Tag<V> tag : tagsPanel.getTags()) {
                    //noinspection NonJREEmulationClassesInClientCode
                    if (tag.getValue().equals(event.getItem().getValue())) {
                        removeTag(tag);
                        break;
                    }
                }
                setInputOffset(tagsPanel.getOffsetWidth());
            }
        });
    }

    protected void tagRemoved() {
        input.setText("");
        setInputOffset(tagsPanel.getOffsetWidth());
        input.setFocus(true);
    }

    protected void listItemSelected(DropDownList<V>.Item item) {
        input.setText("");
        dropDownList.noFocused();
        input.setFocus(true);
        dropDownList.hide();
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
            dropDownList.hide();
        }
    }

    /**
     * Метод вызывается при изменении текста в поле ввода.
     * Если текст слишком длинный - теги сдвигаются влево и поле ввода увеличивается.
     */
    private void textChanged() {
        int textWidth = Utils.getTextWidth(input);

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

        if (dropDownList != null) {
            dropDownList.show();
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

    public void removeTag(Tag<V> tag) {
        innerBus.fireEventFromSource(new TagRemoveEvent<V>(tag), this);
    }

    public void addTag(Tag<V> tag) {
        innerBus.fireEventFromSource(new TagAddEvent<V>(tag), this);
    }

    public List<Tag<V>> getTags() {
        return tagsPanel.getTags();
    }

    public Tag<V> getTag(V value) {
        for (Tag<V> tag : tagsPanel.getTags()) {
            //noinspection NonJREEmulationClassesInClientCode
            if (tag.getValue().equals(value)) {
                return tag;
            }
        }
        return null;
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

    public DropDownListMulti<?> getDropDownList() {
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
    }

    @Override
    public boolean isEnabled() {
        return input.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            root.addStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            root.removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        }
        input.setEnabled(enabled);
        if (hasButton) {
            button.setEnabled(enabled);
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
        if (dropDownList != null) {
            dropDownList.setFilter(filter);
        }
        if (filter != null) {
            filter.setText(getText());
        }
    }
}
