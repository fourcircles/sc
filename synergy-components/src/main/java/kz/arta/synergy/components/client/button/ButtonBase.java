package kz.arta.synergy.components.client.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.client.util.MouseStyle;
import kz.arta.synergy.components.client.util.Selection;
import kz.arta.synergy.components.style.client.Constants;

//TODO при открытии диалога кнопкой у кнопки остается стиль over
/**
 * User: user
 * Date: 23.06.14
 * Time: 18:20
 * Базовый класс для кнопок
 */
public class ButtonBase extends FlowPanel implements HasClickHandlers, HasFocusHandlers, HasEnabled{

    /**
     * Активна ли кнопка
     */
    protected boolean enabled = true;

    /**
     * Панель для текста с иконкой
     */
    protected FlowPanel textPanel = GWT.create(FlowPanel.class);

    /**
     * Надпись кнопки
     */
    protected GradientLabel textLabel;

    /**
     * Текст кнопки
     */
    protected String text = Messages.i18n.tr("Кнопка");

    /**
     * Иконка
     */
    protected ImageResource iconResource;

    protected Image icon;

    /**
     * Позиция иконки
     */
    public enum IconPosition {
        LEFT, RIGHT;
    }

    /**
     * Текущая позиция иконки
     */
    protected IconPosition iconPosition = IconPosition.LEFT;

    protected ButtonBase() {
        textLabel = GWT.create(GradientLabel.class);
        icon = GWT.create(Image.class);
        buildButton();
        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    public ButtonBase(String text) {
        this(text, null, null);
    }

    public ButtonBase(ImageResource iconResource) {
        this(null, iconResource, null);
    }

    public ButtonBase(String text, ImageResource iconResource) {
        this(text, iconResource, null);
    }

    public ButtonBase(String text, ImageResource iconResource, IconPosition iconPosition) {
        setTextInternal(text);
        setIconInternal(iconResource);
        if (iconPosition != null) {
            this.iconPosition = iconPosition;
        }
        buildButton();

        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    /**
     * Добавляет элементы в базовую панель кнопки в порядке, который соответствует указанной позиции иконки
     */
    private void buildButton() {
        clear();
        if (iconPosition == IconPosition.RIGHT) {
            if (textLabel != null) {
                add(textLabel);
            }
            if (icon != null) {
                add(icon);
                icon.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
                icon.getElement().getStyle().setMarginRight(Constants.BUTTON_PADDING, Style.Unit.PX);
            }
        } else {
            if (icon != null) {
                add(icon);
                icon.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
                icon.getElement().getStyle().setMarginLeft(Constants.BUTTON_PADDING, Style.Unit.PX);
            }
            if (textLabel != null) {
                add(textLabel);
            }
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (icon != null) {
            icon.getElement().getStyle().setMarginTop(8, Style.Unit.PX);
        }
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                adjustMargins();
            }
        });
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

    /**
     * Переключает кнопку между состояниями 'disabled' и 'enabled'
     * @param enabled true - 'enabled', false - 'disabled'
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        }

        if (icon != null) {
            if (enabled) {
                icon.getElement().getStyle().setOpacity(1);
            } else {
                icon.getElement().getStyle().setOpacity(0.5);
            }
        }
    }

    public String getText() {
        return text;
    }

    /**
     * Создает или изменяет текст кнопки, задает стили и запрещает выделение.
     * В случае если до этого у кнопки не было текста - возвращает true, чтобы можно было
     * произвести перестройку кнопки в зависимости от позиции иконки.
     * @param text текст кнопки
     * @return true - если до этого у кнопки не было текста
     */
    private boolean setTextInternal(String text) {
        boolean needRebuild = false;
        if (text == null) {
            return false;
        }
        this.text = text;
        if (textLabel == null) {
            needRebuild = true;
            textLabel = GWT.create(GradientLabel.class);
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().buttonText());

            textLabel.getElement().getStyle().setMarginRight(Constants.BUTTON_PADDING, Style.Unit.PX);
            textLabel.getElement().getStyle().setMarginLeft(Constants.BUTTON_PADDING, Style.Unit.PX);

            Selection.disableTextSelectInternal(getElement());
        }
        textLabel.setText(text);
        return needRebuild;
    }

    /**
     * Создает или изменяет текст кнопки
     * @param text текст кнопки
     */
    public void setText(String text) {
        if (setTextInternal(text)) {
            buildButton();
        }
    }

    /**
     * Создает или изменяет иконку для кнопки.
     * @param iconResource рисунок для иконки
     * @return true - в случае, если кнопка до этого не имела иконки
     */
    private boolean setIconInternal(ImageResource iconResource) {
        boolean needRebuild = false;
        if (iconResource == null) {
            return false;
        }
        this.iconResource = iconResource;
        if (icon == null) {
            needRebuild = true;
            icon = GWT.create(Image.class);
            icon.addDragStartHandler(new DragStartHandler() {
                @Override
                public void onDragStart(DragStartEvent event) {
                    event.preventDefault();
                }
            });
            icon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
        }
        icon.setUrl(iconResource.getSafeUri());
        return needRebuild;
    }

    /**
     * Создает или изменяет иконку для кнопки.
     * @param iconResource рисунок для иконки
     */
    public void setIcon(ImageResource iconResource) {
        if (setIconInternal(iconResource)) {
            buildButton();
        }
    }

    /**
     * Указывает позицию иконки
     * @param position позиция
     */
    public void setIconPosition(IconPosition position) {
        if (position == null) {
            return;
        }
        if (iconPosition != position) {
            iconPosition = position;
            buildButton();
        }
    }

    public void onBrowserEvent(Event event) {
        if (!enabled){
            return;
        }
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                MouseStyle.setPressed(this);
                break;
            case Event.ONMOUSEUP:
                MouseStyle.removeAll(this);
                break;
            case Event.ONMOUSEOUT:
                MouseStyle.removeAll(this);
                break;
        }
        super.onBrowserEvent(event);
    }

    /**
     * Вычисляет ширину текстового элемента в зависимости от ширины кнопки и
     * наличия дополнительных элементов (иконка, кнопка для открытия меню и т.д.)
     * @return ширина текста
     */
    protected int getTextLabelWidth() {
        int textLabelWidth = getOffsetWidth() - 2 * Constants.BUTTON_PADDING;
        if (icon != null) {
            textLabelWidth -= icon.getWidth();
            textLabelWidth -= Constants.BUTTON_PADDING;
        }
        return textLabelWidth;
    }

    /**
     * Изменяет ширину текста
     */
    protected void adjustMargins() {
        if (!isAttached()) {
            return;
        }
        if (textLabel != null) {
            int width = getTextLabelWidth();
            if (width < textLabel.getOffsetWidth()) {
                textLabel.setWidth(width + "px");
            }
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        adjustMargins();
    }

    public HasClickHandlers getButton() {
        return this;
    }
}
