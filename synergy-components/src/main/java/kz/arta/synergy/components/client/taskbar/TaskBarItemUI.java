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
import kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 01.10.14
 * Time: 12:28
 *
 * Элемент панели задач
 */
public class TaskBarItemUI extends Composite implements HasClickHandlers {
    /**
     * Иконка по умолчанию
     */
    private static final ImageResource DEFAULT_ICON = ImageResources.IMPL.calendarIcon();

    /**
     * Стиль шрифта
     */
    private static final String FONT = SynergyComponents.resources.cssComponents().mainText();

    /**
     * Иконка
     */
    private final Image icon;

    /**
     * Картинка для иконки
     */
    private ImageResource iconImage;

    /**
     * Текст
     */
    private final GradientLabel2 label;

    private TaskBarItem model;

    public TaskBarItemUI(TaskBarItem model) {
        this.model = model;

        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.resources.cssComponents().item());

        icon = new Image();
        if (model.getTaskBarIcon() != null) {
            iconImage = model.getTaskBarIcon();
        } else {
            iconImage = DEFAULT_ICON;
        }
        icon.setResource(iconImage);

        label = new GradientLabel2(FONT);
        label.setText(model.getText() == null ? "" : model.getText());

        root.add(icon);
        root.add(label);

        root.sinkEvents(Event.ONCLICK);

        model.addModelChangeHandler(new ModelChangeEvent.Handler() {
            @Override
            public void onModelChange(ModelChangeEvent event) {
                modelChanged();
            }
        });
    }

    private void modelChanged() {
        if (iconImage != model.getTaskBarIcon()) {
            iconImage = model.getTaskBarIcon();
            icon.setResource(iconImage);
        }
        String modelText = model.getText();
        if (!label.getText().equals(modelText)) {
            label.setText(modelText);
            fireEvent(new ModelChangeEvent());
        }
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

    /**
     * Возвращает ширину (без границ) элемента без сжатия текста
     */
    public double getNormalWidth() {
        double textWidth = Utils.getPreciseTextWidth(model.getText(), FONT);
        return Constants.STD_ICON_WIDTH + textWidth + 5 + 10 + 10;
    }

    public HandlerRegistration addModelChangeHandler(ModelChangeEvent.Handler handler) {
        return addHandler(handler, ModelChangeEvent.getType());
    }
}
