package kz.arta.synergy.components.client.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel2;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Selection;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: user
 * Date: 23.06.14
 * Time: 18:20
 *  
 * Базовый класс для кнопок
 * 
 * Для того, чтобы можно было нажать на кнопку, отвести мышь, отпустить кнопку использован
 * {@link Event#setCapture(com.google.gwt.dom.client.Element)}, поэтому поэтому события
 * MouseUp и MouseDown не поднимаются к родителям элемента.
 */
public class ButtonBase extends FlowPanel implements
        HasClickHandlers, HasFocusHandlers, HasEnabled, HasAllMouseHandlers, ArtaHasText{

    /**
     * Активна ли кнопка
     */
    protected boolean enabled = true;

    /**
     * Надпись кнопки
     */
    protected GradientLabel2 textLabel;

    /**
     * Текст кнопки
     */
    protected String text = Messages.i18n().tr("Кнопка");

    /**
     * Иконка
     */
    protected ImageResource iconResource;

    protected Image icon;

    /**
     * Позиция иконки
     */
    public enum IconPosition {
        LEFT, RIGHT
    }

    /**
     * Текущая позиция иконки
     */
    protected IconPosition iconPosition = IconPosition.LEFT;

    protected ButtonBase() {
        textLabel = new GradientLabel2(SynergyComponents.getResources().cssComponents().mainTextBold());
        icon = GWT.create(Image.class);
        buildButton();
        addHandlers();
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
        addHandlers();
    }

    private void addHandlers() {
        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (event.getNativeButton() == NativeEvent.BUTTON_LEFT && isEnabled()) {
                    setPressed(true);
                    Event.setCapture(ButtonBase.this.getElement());
                }
            }
        }, MouseDownEvent.getType());

        addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                Event.releaseCapture(ButtonBase.this.getElement());
                setPressed(false);
            }
        }, MouseUpEvent.getType());

        addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                setOver(false);
            }
        }, MouseOutEvent.getType());

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                setOver(true);
            }
        }, MouseOverEvent.getType());

        sinkEvents(Event.MOUSEEVENTS);
        sinkEvents(Event.ONCLICK);
    }

    public void setPressed(boolean pressed) {
        if (pressed) {
            addStyleName(SynergyComponents.getResources().cssComponents().pressed());
        } else {
            removeStyleName(SynergyComponents.getResources().cssComponents().pressed());
        }
    }

    public void setOver(boolean over) {
        if (over) {
            addStyleName(SynergyComponents.getResources().cssComponents().over());
        } else {
            removeStyleName(SynergyComponents.getResources().cssComponents().over());
        }
    }

    /**
     * Добавляет элементы в базовую панель кнопки в порядке, который соответствует указанной позиции иконки
     */
    protected void buildButton() {
        clear();
        if (iconPosition == IconPosition.RIGHT) {
            if (textLabel != null) {
                add(textLabel);
            }
            if (icon != null) {
                add(icon);
                icon.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
                if (LocaleInfo.getCurrentLocale().isRTL()) {
                    icon.getElement().getStyle().setMarginLeft(Constants.BUTTON_PADDING, Style.Unit.PX);
                } else {
                    icon.getElement().getStyle().setMarginRight(Constants.BUTTON_PADDING, Style.Unit.PX);
                }
            }
        } else {
            if (icon != null) {
                add(icon);
                icon.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
                if (LocaleInfo.getCurrentLocale().isRTL()) {
                    icon.getElement().getStyle().setMarginRight(Constants.BUTTON_PADDING, Style.Unit.PX);
                } else {
                    icon.getElement().getStyle().setMarginLeft(Constants.BUTTON_PADDING, Style.Unit.PX);
                }
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
            removeStyleName(SynergyComponents.getResources().cssComponents().disabled());
        } else {
            addStyleName(SynergyComponents.getResources().cssComponents().disabled());
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
            textLabel = new GradientLabel2(SynergyComponents.getResources().cssComponents().mainTextBold());

            textLabel.setStyleName(SynergyComponents.getResources().cssComponents().buttonText());

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
     *
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
            icon.setStyleName("");
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
     *
     * @param iconResource рисунок для иконки
     */
    public void setIcon(ImageResource iconResource) {
        if (setIconInternal(iconResource)) {
            buildButton();
        }
    }

    /**
     * Указывает позицию иконки
     *
     * @param position позиция
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setIconPosition(IconPosition position) {
        if (position == null) {
            return;
        }
        if (iconPosition != position) {
            iconPosition = position;
            buildButton();
        }
    }

    /**
     * Вычисляет ширину текстового элемента в зависимости от ширины кнопки и
     * наличия дополнительных элементов (иконка, кнопка для открытия меню и т.д.)
     *
     * @return ширина текста
     */
    protected int getTextLabelWidth() {
        int textLabelWidth = getOffsetWidth() - 2 * Constants.BUTTON_PADDING - 2 * Constants.BORDER_WIDTH;
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

            width = Math.max(0, width);

            textLabel.getElement().getStyle().clearWidth();
            if (width < textLabel.getOffsetWidth()) {
                textLabel.getElement().getStyle().setWidth(width, Style.Unit.PX);
            }
            textLabel.adjustGradient();
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

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addHandler(handler, FocusEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addHandler(handler, MouseDownEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addHandler(handler, MouseMoveEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addHandler(handler, MouseOutEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addHandler(handler, MouseOverEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addHandler(handler, MouseUpEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return addHandler(handler, MouseWheelEvent.getType());
    }

    @Override
    public String getFontStyle() {
        if (textLabel != null) {
            return textLabel.getFontStyle();
        } else {
            return null;
        }

    }

    public void setFontStyle(String fontStyle) {
        textLabel.setFontStyle(fontStyle);
    }
}
