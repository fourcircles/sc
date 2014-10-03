package kz.arta.synergy.components.client.taskbar;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel2;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 01.10.14
 * Time: 12:28
 *
 * Элемент панели задач
 */
public class TaskBarItem extends Composite implements HasClickHandlers, ArtaHasText {
    /**
     * Иконка по умолчание
     */
    private static final ImageResource DEFAULT_ICON = ImageResources.IMPL.calendarIcon();

    /**
     * Стиль шрифта
     */
    private static final String FONT = SynergyComponents.resources.cssComponents().mainText();

    /**
     * Задана ли иконка
     */
    private boolean iconSet;

    /**
     * Иконка
     */
    private final Image icon;

    /**
     * Текст
     */
    private final GradientLabel2 label;

    public TaskBarItem() {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.resources.cssComponents().item());

        icon = new Image();
        label = new GradientLabel2(FONT);

        root.add(icon);
        root.add(label);

        root.sinkEvents(Event.ONCLICK);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (!iconSet) {
            icon.setResource(DEFAULT_ICON);
        }
    }

    /**
     * Заменяет иконку
     * @param iconImage новая иконка
     */
    public void setIcon(ImageResource iconImage) {
        iconSet = true;
        icon.setResource(iconImage);
    }

    public void clearIcon() {
        iconSet = false;
        icon.setResource(DEFAULT_ICON);
    }

    /**
     * При изменении ширины надо обновить градиент
     */
    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        label.adjustGradient();
    }

    /**
     * {@link #setWidth(String)}
     */
    public void setWidth(double width) {
        getElement().getStyle().setWidth(width, Style.Unit.PX);
        label.adjustGradient();
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }

    /**
     * Возвращает ширину (без границ) элемента без сжатия текста
     */
    public double getNormalWidth() {
        double textWidth = Utils.getPreciseTextWidth(this);
        return Constants.STD_ICON_WIDTH + textWidth + 5 + 10 + 10;
    }

    @Override
    public String getFontStyle() {
        return FONT;
    }
}
