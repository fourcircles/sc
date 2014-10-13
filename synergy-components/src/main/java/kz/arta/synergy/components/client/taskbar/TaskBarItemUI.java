package kz.arta.synergy.components.client.taskbar;

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
import kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent;
import kz.arta.synergy.components.client.taskbar.events.TaskBarEvent;
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
    private static final String FONT = SynergyComponents.getResources().cssComponents().mainText();

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

    /**
     * Внутренний eventbus для событий.
     */
    private EventBus bus;

    private TaskBarItem model;

    public TaskBarItemUI(final TaskBarItem model) {
        bus = new SimpleEventBus();
        this.model = model;

        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().item());

        icon = GWT.create(Image.class);
        updateIcon();

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

        root.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                click();
                bus.fireEventFromSource(event, this);
            }
        }, ClickEvent.getType());

        model.addTaskBarHandler(new TaskBarEvent.AbstractHandler() {
            @Override
            public void onClose(TaskBarEvent event) {
                openChanged();
                TaskBarItemUI.this.removeFromParent();
            }
            @Override
            public void onCollapse(TaskBarEvent event) {
                openChanged();
            }
            @Override
            public void onShow(TaskBarEvent event) {
                openChanged();
            }
        });
    }

    void click() {
        if (model.isOpen()) {
            model.close();
        } else {
            model.open();
        }
    }

    /**
     * Вызывается при поступлении события {@link kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent}
     * из модели
     */
    private void modelChanged() {
        updateIcon();

        String modelText = model.getText();
        if (!label.getText().equals(modelText)) {
            label.setText(modelText);

            bus.fireEventFromSource(new ModelChangeEvent(), TaskBarItemUI.this);
        }
    }

    /**
     * Обновляет иконку в соответствии с моделью
     * Размер иконки всегда один, поэтому этот метод не создает {@link kz.arta.synergy.components.client.taskbar.events.ModelChangeEvent}
     */
    private void updateIcon() {
        ImageResource newIcon = model.getTaskBarIcon() == null ? DEFAULT_ICON : model.getTaskBarIcon();
        if (iconImage != newIcon) {
            iconImage = newIcon;
            icon.setResource(iconImage);
        }
    }

    /**
     * Обновляет вид в соответствии с тем, открыта ли модель
     */
    private void openChanged() {
        if (model.isOpen()) {
            addStyleName(SynergyComponents.getResources().cssComponents().open());
        } else {
            removeStyleName(SynergyComponents.getResources().cssComponents().open());
        }
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
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
        return bus.addHandlerToSource(ClickEvent.getType(), this, handler);
    }

    /**
     * Возвращает ширину (без границ) элемента без сжатия текста
     */
    public double getNormalWidth() {
        double textWidth = Utils.impl().getPreciseTextWidth(model.getText(), FONT);
        return textWidth + Constants.STD_ICON_WIDTH +
                Constants.TASKBAR_ITEM_PADDING * 3 + Constants.TASKBAR_IMAGE_MARGIN;
    }

    public HandlerRegistration addModelChangeHandler(ModelChangeEvent.Handler handler) {
        return bus.addHandlerToSource(ModelChangeEvent.getType(), this, handler);
    }
}
