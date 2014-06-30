package kz.arta.synergy.components.client.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.client.util.Selection;

/**
 * User: user
 * Date: 23.06.14
 * Time: 18:20
 * Базовый класс для кнопок
 */
public class ButtonBase extends FlowPanel implements HasClickHandlers, HasFocusHandlers, HasEnabled {

    private static final int PADDING = 10;

    /**
     * Активна ли кнопка
     */
    protected boolean enabled = true;

    /**
     * Ширина кнопки, контентозависима, если не задана явно
     */
    protected int width = 0;

    /**
     * Панель для текста с иконкой
     */
    protected FlowPanel textPanel = GWT.create(FlowPanel.class);

    /**
     * Надпись кнопки
     */
    protected InlineLabel textLabel = GWT.create(InlineLabel.class);

    /**
     * Панель градиента для затемнения надписи иконки
     */
    protected FlowPanel gradient = GWT.create(FlowPanel.class);

    /**
     * Текст кнопки
     */
    protected String text = Messages.i18n.tr("Кнопка");

    /**
     * Иконка
     */
    protected ImageResource iconResource;


    protected void init() {
        textLabel.setStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        textPanel.setStyleName(SynergyComponents.resources.cssComponents().buttonText());

        if (iconResource != null) {
            Image icon = new Image(iconResource.getSafeUri());
            icon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
            textPanel.add(icon);
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().paddingElement());
        }
        textLabel.setText(text);

        textPanel.add(textLabel);
        add(textPanel);

        Selection.disableTextSelectInternal(getElement());
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addHandler(handler, FocusEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        /*устанавливаем размеры кнопок*/
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                int textWidth = textLabel.getElement().getOffsetWidth();
                if (textWidth == 0) {
                    textWidth = getWidth(textLabel.getElement());
                }
                if (width == 0) {
                    setWidth(textWidth + (iconResource != null ? iconResource.getWidth() + 2 * PADDING : 2 * PADDING));
                }
                if (textWidth + (iconResource != null ? iconResource.getWidth() + 2 * PADDING : 2 * PADDING) > width) {
                    add(gradient);
                }
            }
        });
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        super.setWidth(width + "px");
        this.width = width;
    }

    /**
     * Получаем ширину элемента
     * @param element   элемент
     * @return  ширина элемента
     */
    public int getWidth(Element element) {
        Element e = DOM.clone(element, true);
        e.getStyle().setVisibility(Style.Visibility.HIDDEN);
        e.setClassName(textLabel.getStyleName());
        Document.get().getBody().appendChild(e);
        int width = e.getOffsetWidth();
        Document.get().getBody().removeChild(e);
        return width;
    }
}
