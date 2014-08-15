package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.label.GradientLabel;
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
public class Tag<V> extends Composite implements ArtaHasText, TagRemoveEvent.HasHandler, HasEnabled {
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
    private GradientLabel label;

    /**
     * Значение для тега
     */
    private V value;

    /**
     * Элемент для закрытия тега
     */
    private Image image;

    /**
     * Включен/выключен
     */
    private boolean isEnabled;

    private EventBus bus;

    /**
     * Конструктор для случая когда у тега нет значения
     * @param text текст тега
     */
    public Tag(String text) {
        root = new FlowPanel();
        initWidget(root);

        label = new GradientLabel(getFontStyle());
        label.setHeight(Constants.TAG_HEIGHT + "px");
        label.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
        label.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);

        image = new Image(ImageResources.IMPL.tagClose());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isEnabled()) {
                    bus.fireEvent(new TagRemoveEvent(Tag.this));
                }
            }
        });
        setText(text);
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
    public String getFontStyle() {
        return SynergyComponents.resources.cssComponents().mainText();
    }

    /**
     * Ширина определяется из ширины текста, ширины кнопки и внутренних отступов.
     * Возможно узнать ширину до присоединения элемента к DOM.
     * @return ширина
     */
    @Override
    public int getOffsetWidth() {
        return Math.min(Constants.TAG_MAX_WIDTH,
                Utils.getTextWidth(this) + Constants.TAG_PADDING * 3 + 16);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        label.setText(text);
        int totalWidth = Utils.getTextWidth(this);
        totalWidth += 3 * Constants.COMMON_INPUT_PADDING;
        totalWidth += Constants.STD_ICON_WIDTH;

        if (totalWidth > Constants.TAG_MAX_WIDTH) {
            label.setWidth(Constants.TAG_MAX_WIDTH - 3 * Constants.COMMON_INPUT_PADDING - Constants.STD_ICON_WIDTH);
        }
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public EventBus getBus() {
        return bus;
    }

    public void setBus(EventBus bus) {
        this.bus = bus;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        getElement().getStyle().clearWidth();
            if (getElement().getScrollWidth() > getElement().getClientWidth()) {
            label.setWidth(label.getOffsetWidth() - (getElement().getScrollWidth() - getElement().getClientWidth()));
        }
    }

    @Override
    public HandlerRegistration addTagRemoveHandler(TagRemoveEvent.Handler handler) {
        return bus.addHandlerToSource(TagRemoveEvent.TYPE, this, handler);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
