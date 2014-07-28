package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 24.07.14
 * Time: 17:49
 *
 * Тег для поля с тегами.
 */
public class Tag<V> extends Composite implements ArtaHasText, HasCloseHandlers<Tag>{
    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Текст тега
     */
    private String text;

    /**
     * Элемент для текста
     */
    private Label label;

    /**
     * Значение для тега
     */
    private V value;

    /**
     * Элемент для закрытия тега
     */
    private Image image;

    /**
     * Конструктор для случая когда у тега нет значения
     * @param text текст тега
     */
    public Tag(String text) {
        root = new FlowPanel();
        initWidget(root);

        this.text = text;
        label = new Label(text);
        image = new Image(ImageResources.IMPL.tagClose());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CloseEvent.fire(Tag.this, Tag.this);
            }
        });
        root.add(label);
        root.add(image);
        addStyleName(SynergyComponents.resources.cssComponents().tag());
        addStyleName(getFontStyle());
    }

    /**
     * Конструктор для тега с текстом и значением
     * @param text текст тега
     * @param value значение тега
     */
    public Tag(String text, V value) {
        this(text);
        this.value = value;
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<Tag> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    @Override
    public String getFontStyle() {
        return SynergyComponents.resources.cssComponents().mainText();
    }

    /**
     * Ширина определяется из ширины текста, ширины кнопки и отступов.
     * Возможно узнать ширину до присоединения элемента к DOM.
     * @return ширина
     */
    @Override
    public int getOffsetWidth() {
        return Utils.getTextWidth(this) + Constants.TAG_PADDING * 3 + image.getWidth();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        label.setText(text);
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
