package kz.arta.synergy.components.client.scroll;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalScrollbar;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.theme.ColorType;
import kz.arta.synergy.components.style.client.Constants;
import kz.arta.synergy.components.style.client.resources.ComponentResources;

/**
 * User: vsl
 * Date: 16.07.14
 * Time: 17:11
 * Вертикальный скролл
 */
public class ArtaVerticalScroll extends Composite implements VerticalScrollbar{
    interface VerticalScrollBinder extends UiBinder<ArtaFlowPanel, ArtaVerticalScroll> {}
    private static VerticalScrollBinder binder = GWT.create(VerticalScrollBinder.class);

    /**
     * Часть экрана, которая будет пролистываться при нажатии кнопок
     * или при клике вне бегунка
     */
    private static final double SCROLL_STEP = 0.8;

    /**
     * Основная панель
     */
    @UiField ArtaFlowPanel panel;

    /**
     * Кнопка вверх
     */
    @UiField Image up;

    /**
     * Кнопка вниз
     */
    @UiField Image down;

    /**
     * Бегунок
     */
    @UiField ArtaFlowPanel bar;

    @UiField(provided = true) ImageResources images;
    @UiField(provided = true) ComponentResources resources;

    /**
     * Высота скроллбара
     */
    int height;

    /**
     * Высота скролируемой области
     */
    int contentHeight;

    /**
     * Координата начала dnd относительно начала бегунка
     */
    private int dragStartY;

    boolean dragging = false;

    /**
     * Панель к которой относится данный скроллбар
     */
    private ArtaScrollPanel scrollPanel;

    /**
     * Высота бегунка
     */
    private int barHeight;

    /**
     * Высота скроллбара без кнопок и бегунка
     */
    private int freeTrackSpace;

    private ColorType type = ColorType.WHITE;

    public ArtaVerticalScroll(final ArtaScrollPanel scrollPanel) {
        images = ImageResources.IMPL;
        resources = SynergyComponents.getResources();

        panel = binder.createAndBindUi(this);
        initWidget(panel);

        this.scrollPanel = scrollPanel;
        down.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        down.getElement().getStyle().setBottom(0, Style.Unit.PX);
    }

    @UiHandler("up")
    void up(ClickEvent event) {
        event.stopPropagation();
        scrollUp();
    }

    @UiHandler("up")
    void upPress(MouseDownEvent event) {
        event.preventDefault();
        up.setResource(ImageResources.IMPL.scrollBarUpPressed());
    }

    @UiHandler("up")
    void upRelease(MouseUpEvent event) {
        up.setResource(ImageResources.IMPL.scrollBarUp());
    }

    @UiHandler("down")
    void down(ClickEvent event) {
        event.stopPropagation();
        scrollDown();
    }

    @UiHandler("down")
    void downPress(MouseDownEvent event) {
        down.setResource(ImageResources.IMPL.scrollBarDownPressed());
    }

    @UiHandler("down")
    void downRelease(MouseUpEvent event) {
        down.setResource(ImageResources.IMPL.scrollBarDown());
    }


    /**
     * Нажатие на бегунок, начало его перемещения
     */
    @UiHandler("bar")
    void barDown(MouseDownEvent event) {
        event.stopPropagation();
        bar.addStyleName(SynergyComponents.getResources().cssComponents().pressed());
        dragging = true;
        dragStartY = event.getY();
        Event.setCapture(bar.getElement());
    }

    @UiHandler("up")
    void onPress(MouseDownEvent event) {
        if (type == ColorType.BLACK) {
            up.setResource(ImageResources.IMPL.scrollBarDarkUpPressed());
        } else {
            up.setResource(ImageResources.IMPL.scrollBarUpPressed());
        }
    }

    @UiHandler("up")
    void onUp(MouseUpEvent event) {
        if (type == ColorType.BLACK) {
            up.setResource(ImageResources.IMPL.scrollBarDarkUp());
        } else {
            up.setResource(ImageResources.IMPL.scrollBarUp());
        }
    }

    @UiHandler("down")
    void onDownPress(MouseDownEvent event) {
        if (type == ColorType.BLACK) {
            down.setResource(ImageResources.IMPL.scrollBarDarkDownPressed());
        } else {
            down.setResource(ImageResources.IMPL.scrollBarDownPressed());
        }
    }

    @UiHandler("down")
    void onDownUp(MouseUpEvent event) {
        if (type == ColorType.BLACK) {
            down.setResource(ImageResources.IMPL.scrollBarDarkDown());
        } else {
            down.setResource(ImageResources.IMPL.scrollBarDown());
        }
    }

    /**
     * Завершение перемещения бегунка
     */
    @UiHandler("bar")
    void barUp(MouseUpEvent event) {
        event.stopPropagation();
        bar.removeStyleName(SynergyComponents.getResources().cssComponents().pressed());
        dragging = false;
        Event.releaseCapture(bar.getElement());
    }

    /**
     * Перемещение бегунка
     */
    @UiHandler("bar")
    void barMove(MouseMoveEvent event) {
        event.preventDefault();

        if (dragging) {
            int dragAreaStart = up.getAbsoluteTop() + up.getOffsetHeight();

            int barTop = event.getClientY() - dragStartY;
            int marginTop = barTop - dragAreaStart;

            scrollPanel.setVerticalScrollPosition((int) getScrollPosition(marginTop));
        }
    }

    @UiHandler("panel")
    void panelClick(ClickEvent event) {
        event.preventDefault();
        int barTop = bar.getAbsoluteTop();
        int barBottom = bar.getAbsoluteTop() + barHeight;
        if (event.getClientY() < barTop) {
            scrollUp();
        } else if (event.getClientY() > barBottom) {
            scrollDown();
        }
    }

    /**
     * Смещение видимой части на один шаг вниз
     */
    private void scrollDown() {
        int pos = scrollPanel.getVerticalScrollPosition()   ;
        pos += SCROLL_STEP * height;
        pos = Math.min(pos, getMaximumVerticalScrollPosition());
        scrollPanel.setVerticalScrollPosition(pos);
    }

    /**
     * Смещение видимой части на один шаг вверх
     */
    private void scrollUp() {
        int pos = scrollPanel.getVerticalScrollPosition();
        pos -= SCROLL_STEP * height;
        pos = Math.max(pos, getMinimumVerticalScrollPosition());
        scrollPanel.setVerticalScrollPosition(pos);
    }

    /**
     * Возвращает высоту контролируемого контента
     * @return
     */
    @Override
    public int getScrollHeight() {
        return scrollPanel.getWidget().getOffsetHeight();
    }

    /**
     * Метод вызывается ScrollPanel при открытии и задает высоту контента.
     * @param height высота
     */
    @Override
    public void setScrollHeight(int height) {

        contentHeight = height;
        this.height = scrollPanel.getOffsetHeight();
        if (scrollPanel.getOffsetWidth() < scrollPanel.getWidget().getOffsetWidth()) {
            this.height -= Constants.SCROLL_BAR_HEIGHT;
            height += Constants.SCROLL_BAR_HEIGHT;
        }
        panel.setHeight(this.height + "px");
        barHeight = (int) Math.ceil ((this.height / (double) height) * (this.height - Constants.SCROLL_BAR_HEIGHT * 2));
        bar.setHeight(barHeight + "px");
        freeTrackSpace = this.height - barHeight - (Constants.SCROLL_BAR_HEIGHT + 1) * 2;
        if (getVerticalScrollPosition() == 0) {
            setVerticalScrollPosition(0);
        }
    }

    @Override
    public HandlerRegistration addScrollHandler(ScrollHandler handler) {
        return scrollPanel.addScrollHandler(handler);
    }

    @Override
    public int getMaximumVerticalScrollPosition() {
        return scrollPanel.getMaximumVerticalScrollPosition();
    }

    @Override
    public int getMinimumVerticalScrollPosition() {
        return scrollPanel.getMinimumVerticalScrollPosition();
    }

    @Override
    public int getVerticalScrollPosition() {
        return scrollPanel.getVerticalScrollPosition();
    }

    /**
     * Вычисляет сдвиг бегунка соответствующего текущей позиции скролла
     * @param scrollPosition позиция скролла
     * @return сдвиг бегунка
     */
    private double getBarPosition(int scrollPosition) {
        double scrollPct = ((double) scrollPosition - getMinimumVerticalScrollPosition()) /
                (getMaximumVerticalScrollPosition() - getMinimumVerticalScrollPosition());
        return freeTrackSpace * scrollPct;
    }

    /**
     * Вычисляет позицию скролла соответствующая сдвигу бегунка
     * @param barPosition сдвиг бегунка
     * @return позиция скролла
     */
    private double getScrollPosition(double barPosition) {
        double pct = barPosition / freeTrackSpace;
        return pct * (getMaximumVerticalScrollPosition() - getMinimumVerticalScrollPosition());
    }

    /**
     * Изменяет позицию бегунка на соответствующую позиции скролла
     * @param position позиция скролла
     */
    @Override
    public void setVerticalScrollPosition(int position) {
        double barPosition = getBarPosition(position);
        bar.getElement().getStyle().setMarginTop(barPosition, Style.Unit.PX);
        bar.getElement().getStyle().setMarginBottom(freeTrackSpace - barPosition, Style.Unit.PX);
    }

    public void onLoad() {
        super.onLoad();
        setVerticalScrollPosition(0);
    }

    public void setType(ColorType type) {
        this.type = type;
        if (type == ColorType.BLACK) {
            up.setResource(images.scrollBarDarkUp());
            down.setResource(images.scrollBarDarkDown());
        }
    }
}
