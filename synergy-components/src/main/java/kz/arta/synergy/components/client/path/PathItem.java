package kz.arta.synergy.components.client.path;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel2;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 28.10.14
 * Time: 13:35
 *
 * Элемент компонента путь
 */
public class PathItem extends Composite implements HasClickHandlers {
    /**
     * Шрифт по умолчанию
     */
    private static final String FONT = SynergyComponents.getResources().cssComponents().mainText();
    private EventBus bus;

    /**
     * Картинка для иконки левой иконки, правая - постоянная
     */
    private ImageResource iconImage;

    /**
     * Левая иконка
     */
    private Image image;

    /**
     * Корневая панель
     */
    private final FlowPanel root;

    /**
     * Текст с градиентом
     */
    private final GradientLabel2 label;

    /**
     * @param text текст
     * @param iconImage иконка, если null -- ее не будет
     */
    public PathItem(String text, ImageResource iconImage) {
        bus = new SimpleEventBus();

        root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().item());

        label = new GradientLabel2(FONT);
        label.setText(text);
        root.add(label);

        if (iconImage != null) {
            setIcon(iconImage);
        }

        Image arrow = GWT.create(Image.class);
        arrow.setResource(ImageResources.IMPL.pagerRight());
        root.add(arrow);

        root.sinkEvents(Event.ONCLICK);
    }

    public PathItem(String text) {
        this(text, null);
    }

    /**
     * В начале надо задать ширину исходя из длины текста, потому что
     * внутри элементы расположены абсолютно
     */
    @Override
    protected void onLoad() {
        super.onLoad();
        double width = Utils.impl().getPreciseTextWidth(label.getText(), FONT);
        width += Constants.STD_ICON_WIDTH * 2;
        width += 8 * 2;
        width += 8 * 2;
        getElement().getStyle().setWidth(width, Style.Unit.PX);
    }

    /**
     * Изменить иконку
     * @param iconImage новая картинка для иконки
     */
    public void setIcon(ImageResource iconImage) {
        if (image == null) {
            image = GWT.create(Image.class);
            root.insert(image, 0);
        }
        if (iconImage != null) {
            if (this.iconImage != iconImage) {
                this.iconImage = iconImage;
                image.setResource(iconImage);
            }
        } else {
            image.getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
    }

    /**
     * Добавляет хэндлер для клика по элементу
     */
    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return bus.addHandlerToSource(ClickEvent.getType(), this, handler);
    }

    /**
     * Этот метод надо вызывать после изменения ширины элемента (например, через свойства)
     */
    public void updateWidth() {
        label.adjustGradient();
    }

    /**
     * Изменение ширины элемента
     * @param width новая ширина
     */
    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        updateWidth();
    }
}
