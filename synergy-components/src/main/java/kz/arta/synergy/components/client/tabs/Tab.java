package kz.arta.synergy.components.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.tabs.events.HasTabHandlers;
import kz.arta.synergy.components.client.tabs.events.TabCloseEvent;
import kz.arta.synergy.components.client.tabs.events.TabSelectionEvent;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 08.08.14
 * Time: 12:23
 *
 * Вкладка
 */
public class Tab extends Composite implements ArtaHasText, HasTabHandlers {
    static final EventBus bus = new SimpleEventBus();

    /**
     * Корневая панель
     */
    ArtaFlowPanel root;

    /**
     * Картинка для закрытия вкладки
     */
    private Image closeImage;

    /**
     * Текст вкладки с градиентом
     */
    private GradientLabel label;

    /**
     * Выбрана ли вкладка
     */
    private boolean isActive = false;

    /**
     * Содержимое владки
     */
    private IsWidget content;

    /**
     * Содержит ли вкладка кнопку для закрытия
     */
    private boolean hasCloseButton;

    /**
     * @param hasCloseButton имеет ли вкладка кнопку закрыть
     */
    public Tab(boolean hasCloseButton) {
        root = GWT.create(ArtaFlowPanel.class);
        initWidget(root);
        addStyleName(SynergyComponents.resources.cssComponents().tab());

        label = new GradientLabel(getFontStyle());
        label.setHeight(Constants.TAB_HEIGHT - Constants.BORDER_WIDTH * 2 + "px");

        closeImage = GWT.create(Image.class);
        closeImage.setResource(ImageResources.IMPL.dialogCloseButton());

        root.add(label);

        this.hasCloseButton = hasCloseButton;
        if (hasCloseButton) {
            root.add(closeImage);
        }

        root.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!isActive()) {
                    setActive(true, true);
                }
            }
        });

        closeImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
            }
        });
    }

    public Tab() {
        this(true);
    }

    @Override
    public String getFontStyle() {
        if (isActive) {
            return SynergyComponents.resources.cssComponents().mainTextBold();
        } else {
            return SynergyComponents.resources.cssComponents().mainText();
        }
    }

    @Override
    public String getText() {
        return label.getText();
    }

    /**
     * Задает ширину текста с градиентом для правильного выставления градиента
     */
    private void adjustWidth() {
        int width = Utils.getTextWidth(getText(), SynergyComponents.resources.cssComponents().mainTextBold());
        width += Constants.COMMON_INPUT_PADDING * 2 + Constants.BORDER_WIDTH * 2;
        if (hasCloseButton) {
            width += Constants.STD_ICON_WIDTH + Constants.DIALOG_CLOSE_BUTTON_RIGHT_MARGIN;
        }

        width = Math.min(width, Constants.TAB_MAX_WIDTH);
        super.setWidth(width + "px");

        int textWidth = width - Constants.COMMON_INPUT_PADDING * 2 - Constants.BORDER_WIDTH * 2;
        if (hasCloseButton) {
            textWidth -= Constants.STD_ICON_WIDTH + Constants.DIALOG_CLOSE_BUTTON_RIGHT_MARGIN;
        }
        label.setWidth(textWidth);
    }

    @Override
    public void setText(String text) {
        label.setText(text);
        if (isAttached()) {
            adjustWidth();
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        adjustWidth();
    }

    /**
     * Задание ширины вкладки напрямую не прудсмотрено. Ширина определяется исходя
     * из максимальной ширины и текста.
     */
    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("ширина вкладки зависит только от текста");
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Изменяет статус выбранной вкладки
     * @param isActive true - выбрана, false - нет
     * @param fireEvents создавать ли события выбора вкладки
     */
    public void setActive(boolean isActive, boolean fireEvents) {

        label.removeStyleName(getFontStyle());
        if (isActive) {
            addStyleName(SynergyComponents.resources.cssComponents().selected());
            getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
            if (fireEvents) {
                bus.fireEventFromSource(new TabSelectionEvent(this), this);
            }
        } else {
            removeStyleName(SynergyComponents.resources.cssComponents().selected());
            getElement().getStyle().clearBorderStyle();
        }
        this.isActive = isActive;
        label.setStyleName(getFontStyle());
    }

    /**
     * Закрывает вкладку
     */
    public void close() {
        bus.fireEventFromSource(new TabCloseEvent(this), this);
    }

    public IsWidget getContent() {
        return content;
    }

    public void setContent(IsWidget content) {
        this.content = content;
    }

    /**
     * Убрать-добавить кнопку закрытия
     * @param hasCloseButton true - добавить, false - убрать
     */
    public void setHasCloseButton(boolean hasCloseButton) {
        if (this.hasCloseButton != hasCloseButton) {
            if (this.hasCloseButton) {
                root.remove(closeImage);
            }
            if (!this.hasCloseButton) {
                root.add(closeImage);
            }
            this.hasCloseButton = hasCloseButton;
            adjustWidth();
        }
    }

    public boolean hasCloseButton() {
        return hasCloseButton;
    }

    @Override
    public HandlerRegistration addTabSelectionHandler(TabSelectionEvent.Handler handler) {
        return bus.addHandlerToSource(TabSelectionEvent.TYPE, this, handler);
    }

    @Override
    public HandlerRegistration addTabCloseHandler(TabCloseEvent.Handler handler) {
        return bus.addHandlerToSource(TabCloseEvent.TYPE, this, handler);
    }
}
