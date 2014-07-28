package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.client.input.tags.events.*;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 24.07.14
 * Time: 16:20
 *
 * Поле с тегами
 */
public class TagInput extends Composite implements HasText,
        HasClickHandlers, HasTagAddEventHandler, HasTagRemoveEventHandler {
    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Элемент для ввода текста
     */
    private TextInput input;

    /**
     * Кнопка поля
     */
    private ImageButton button;

    /**
     * Индикатор количества скрытых тегов
     */
    private Label indicator;

    /**
     * Количество скрытых тегов
     */
    private int hiddenTagsCount;

    /**
     * Панель для отображения тегов, которые не скрыты в индикаторе
     */
    private FlowPanel tagsPanel;

    /**
     * Список всех добавленных тегов
     */
    private ArrayList<Tag> tags;

    /**
     * Обработчик закрытия тега
     */
    private CloseHandler<Tag> closeTagHandler;

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
     * Попап для индикатора количества скрытых тэгов
     */
    private TagIndicator tagIndicator;

    public TagInput() {
        root = new FlowPanel();
        initWidget(root);

        tags = new ArrayList<Tag>();

        input = new TextInput();
        input.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        root.add(input);

        button = new ImageButton(ImageResources.IMPL.zoom());
        button.getElement().getStyle().setMarginTop(-1, Style.Unit.PX);
        button.getElement().getStyle().setMarginRight(-1, Style.Unit.PX);

        addStyleName(SynergyComponents.resources.cssComponents().mainText());
        addStyleName(SynergyComponents.resources.cssComponents().tagInput());

        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        setWidth(Constants.FIELD_WITH_BUTTON_MIN_WIDTH);

        root.add(button);

        tagsPanel = new FlowPanel();
        tagsPanel.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        tagsPanel.getElement().getStyle().setTop(-32, Style.Unit.PX);
        tagsPanel.getElement().getStyle().setPaddingRight(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
        tagsPanel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        root.add(tagsPanel);

        tagIndicator = new TagIndicator();
        tagIndicator.addTagRemoveHandler(new TagRemoveEvent.TagRemoveEventHandler() {
            @Override
            public void onTagRemove(TagRemoveEvent event) {
                removeTag(event.getTag());
            }
        });

        input.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                new Timer() {
                    @Override
                    public void run() {
                        textChanged();
                    }
                }.schedule(20);
            }
        });
        input.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_ENTER:
                        String inputText = input.getText();
                        if (!input.getText().isEmpty()) {
                            input.setText("");

                            Tag tag = addTag(inputText);
                            tag.addCloseHandler(closeTagHandler);

                            textChanged();
                        }
                        break;
                    case KeyCodes.KEY_BACKSPACE:
                    case KeyCodes.KEY_DELETE:
                    case KeyCodes.KEY_SPACE:
                        textChanged();
                        break;
                    case KeyCodes.KEY_ESCAPE:
                        input.setText("");
                        textChanged();
                        break;
                }
            }
        });

        closeTagHandler = new CloseHandler<Tag>() {
            @Override
            public void onClose(CloseEvent<Tag> event) {
                removeTag(event.getTarget());
            }
        };
    }

    /**
     * Метод вызывается при изменении текста в поле ввода.
     * Если текст слишком длинный - теги сдвигаются влево и поле ввода увеличивается.
     */
    private void textChanged() {
        int textWidth = Utils.getTextWidth(input);
        if (textWidth >= inputWidth) {
            tagsPanelOffset = tagsPanelOffset - (textWidth - inputWidth);
            tagsPanel.getElement().getStyle().setLeft(tagsPanelOffset, Style.Unit.PX);
            setInputOffset(inputOffset - (textWidth - inputWidth));
        } else {
            tagsPanelOffset = 0;
            tagsPanel.getElement().getStyle().setLeft(2, Style.Unit.PX);
            setInputOffset(tagsPanel.getOffsetWidth());
        }
    }

    /**
     * Удаляет тег
     */
    private void removeTag(Tag tag) {
        tags.remove(tag);
        tagsPanel.remove(tag);
        placeTags();

        fireEvent(new TagRemoveEvent(tag));
    }

    /**
     * Ширина элемента без границ, кнопки и отступов.
     */
    private int getAvailableSpace() {
        return offsetWidth - Constants.IMAGE_BUTTON_WIDTH - 3 * Constants.BORDER_WIDTH - Constants.COMMON_INPUT_PADDING;
    }

    /**
     * Задает элементу input расстояние от левой границы поля начиная с которого можно
     * вводить текст, причем offsetWidth элемента ввода не изменяется.
     * @param offset расстояние от левой границы поля
     */
    private void setInputOffset(int offset) {
        inputOffset = offset;
        input.getElement().getStyle().setPaddingLeft(inputOffset, Style.Unit.PX);
        inputWidth = getAvailableSpace() - offset;
        input.setWidth(inputWidth + "px");
    }

    /**
     * Размещает теги в панели тегов, помещая скрытые теги в индикатор.
     * Обычно вызывается при изменении добавлении или удалении тегов.
     */
    private void placeTags() {
        if (tags.isEmpty()) {
            setInputOffset(Constants.COMMON_INPUT_PADDING);
            return;
        }
        int availableSpace = getAvailableSpace() - 40;
        //интервал между самым правым тегом и началом ввода текста
        availableSpace -= Constants.COMMON_INPUT_PADDING;
        int i = tags.size() - 1;
        while (i >= 0 && availableSpace >= 0) {
            availableSpace -= tags.get(i).getOffsetWidth() + Constants.INTERVAL_BETWEEN_TAGS;
            i--;
        }
        if (availableSpace < 0) {
            //места не хватает, создаем индикатор количества
            placeTags(i + 1, availableSpace + tags.get(i + 1).getOffsetWidth());
        } else {
            tagsPanel.clear();
            for (int tagNum = 0; tagNum < tags.size(); tagNum++) {
                tags.get(tagNum).getElement().getStyle().setMarginLeft(2, Style.Unit.PX);
                tagsPanel.add(tags.get(tagNum));
            }
            tagIndicator.hide();
        }

        setInputOffset(tagsPanel.getOffsetWidth());
    }

    /**
     * Разместить первые {@code tagsCount} тегов в индикаторе количества тегов,
     * остальные - в поле.
     * @param tagsCount количество тегов в индикаторе
     */
    private void placeTags(int tagsCount, int indicatorWidth) {
        if (indicator == null) {
            indicator = new Label();
            indicator.addStyleName(SynergyComponents.resources.cssComponents().tag());
            indicator.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            indicator.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    showIndicatorPopup();
                }
            });
        }
        indicator.setText(tagsCount + 1 + "+");
        hiddenTagsCount = tagsCount + 1;

        tagsPanel.clear();
        tagsPanel.add(indicator);

        if (indicator.getOffsetWidth() > indicatorWidth) {
            indicator.setText(tagsCount + 2 + "+");
            hiddenTagsCount = tagsCount + 2;
            tagsCount++;
        }
        for (int i = tagsCount + 1; i < tags.size(); i++) {
            tags.get(i).getElement().getStyle().setMarginLeft(2, Style.Unit.PX);
            tagsPanel.add(tags.get(i));
        }
    }

    /**
     * Создает тег с указанным текстом и добаляет его.
     * @param text текст тега
     * @return созданный тег
     */
    private Tag addTag(String text) {
        Tag tag = new Tag(text);

        tags.add(tag);
        tagsPanel.add(tag);
        placeTags();

        fireEvent(new TagAddEvent(tag));
        return tag;
    }

    /**
     * Учитывать порядок пока не обязательно, добавление тегов только в конец списка.
     */
    public void showIndicatorPopup() {
        for (int i = 0; i < hiddenTagsCount; i++) {
            tagIndicator.addTag(tags.get(i));
        }
        tagIndicator.showRelativeTo(this);
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

        //минус кнопка
        width -= Constants.IMAGE_BUTTON_WIDTH + 1;
        //минус padding поля ввода
        width -= Constants.COMMON_INPUT_PADDING * 2;

        inputWidth = width;
        input.setWidth(width + "px");
        setInputOffset(Constants.COMMON_INPUT_PADDING);
        placeTags();
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("надо использовать setWidth(int) для задания ширины");
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return button.addClickHandler(handler);
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
    public HandlerRegistration addTagAddHandler(TagAddEvent.TagAddEventHandler handler) {
        return addHandler(handler, TagAddEvent.TYPE);
    }

    @Override
    public HandlerRegistration addTagRemoveHandler(TagRemoveEvent.TagRemoveEventHandler handler) {
        return addHandler(handler, TagRemoveEvent.TYPE);
    }
}
