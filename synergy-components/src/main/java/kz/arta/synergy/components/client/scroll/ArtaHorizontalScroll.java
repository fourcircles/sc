package kz.arta.synergy.components.client.scroll;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalScrollbar;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.style.client.Constants;
import kz.arta.synergy.components.style.client.resources.ComponentResources;

/**
 * User: user
 * Date: 29.07.14
 * Time: 12:23
 * Горизонтальный скролл-бар
 */
public class ArtaHorizontalScroll extends Composite implements HorizontalScrollbar {

    interface HorizontalScrollBinder extends UiBinder<ArtaFlowPanel, ArtaHorizontalScroll> {}
    private static HorizontalScrollBinder binder = GWT.create(HorizontalScrollBinder.class);

    /**
     * Часть экрана, которая будет пролистываться при нажатии кнопок
     * или при клике вне бегунка
     */
    private static final double SCROLL_STEP = 0.8;

    /**
     * Основная панель
     */
    @UiField
    ArtaFlowPanel panel;

    /**
     * Кнопка влево
     */
    @UiField
    Image left;

    /**
     * Кнопка вправо
     */
    @UiField Image right;

    /**
     * Бегунок
     */
    @UiField ArtaFlowPanel bar;

    @UiField(provided = true)
    ImageResources images;
    @UiField(provided = true)
    ComponentResources resources;

    /**
     * Ширина скроллбара
     */
    int width;

    /**
     * Ширина скролируемой области
     */
    int contentWidth;

    /**
     * Координата начала dnd относительно начала бегунка
     */
    private int dragStartX;

    boolean dragging = false;

    /**
     * Панель к которой относится данный скроллбар
     */
    private ArtaScrollPanel scrollPanel;

    /**
     * Ширина бегунка
     */
    private int barWidth;

    /**
     * Ширина скроллбара без кнопок и бегунка
     */
    private int freeTrackSpace;

    public ArtaHorizontalScroll(final ArtaScrollPanel scrollPanel) {
        images = ImageResources.IMPL;
        resources = SynergyComponents.resources;

        panel = binder.createAndBindUi(this);
        initWidget(panel);

        this.scrollPanel = scrollPanel;
        right.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            right.getElement().getStyle().setLeft(1, Style.Unit.PX);
        } else {
            right.getElement().getStyle().setRight(1, Style.Unit.PX);
        }
    }

    @UiHandler("left")
    void up(ClickEvent event) {
        event.stopPropagation();
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            scrollRight();
        } else {
            scrollLeft();
        }
    }

    @UiHandler("right")
    void down(ClickEvent event) {
        event.stopPropagation();
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            scrollLeft();
        } else {
            scrollRight();
        }
    }

    @UiHandler("left")
    void onPress(MouseDownEvent event) {
        left.setResource(ImageResources.IMPL.scrollBarLeftPressed());
    }

    @UiHandler("left")
    void onUp(MouseUpEvent event) {
        left.setResource(ImageResources.IMPL.scrollBarLeft());
    }

    @UiHandler("right")
    void onDownPress(MouseDownEvent event) {
        right.setResource(ImageResources.IMPL.scrollBarRightPressed());
    }

    @UiHandler("right")
    void onDownUp(MouseUpEvent event) {
        right.setResource(ImageResources.IMPL.scrollBarRight());
    }


    /**
     * Нажатие на бегунок, начало его перемещения
     */
    @UiHandler("bar")
    void barDown(MouseDownEvent event) {
        event.stopPropagation();
        bar.addStyleName(SynergyComponents.resources.cssComponents().pressed());
        dragging = true;
        dragStartX = event.getX();
        Event.setCapture(bar.getElement());
    }

    /**
     * Завершение перемещения бегунка
     */
    @UiHandler("bar")
    void barUp(MouseUpEvent event) {
        event.stopPropagation();
        bar.removeStyleName(SynergyComponents.resources.cssComponents().pressed());
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
            int dragAreaStart = left.getAbsoluteLeft() + left.getOffsetWidth();
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                dragAreaStart = left.getAbsoluteLeft();
            }
            int barLeft = event.getClientX() - dragStartX;
            int marginLeft = barLeft - dragAreaStart;
            int pos = (int) getScrollPosition(marginLeft + (LocaleInfo.getCurrentLocale().isRTL() ? barWidth : 0));
            scrollPanel.setHorizontalScrollPosition(checkPosIE11(pos));
        }
    }

    @UiHandler("panel")
    void panelClick(ClickEvent event) {
        event.preventDefault();
        int barTop = bar.getAbsoluteLeft();
        int barBottom = bar.getAbsoluteLeft() + barWidth;
        if (event.getClientX() < barTop) {
            scrollLeft();
        } else if (event.getClientX() > barBottom) {
            scrollRight();
        }
    }

    /**
     * Смещение видимой части на один шаг вправо
     */
    private void scrollRight() {
        int pos = checkPosIE11(scrollPanel.getHorizontalScrollPosition());
        pos += SCROLL_STEP * width;
        pos = Math.min(pos, getMaximumHorizontalScrollPosition());
        pos = checkPosIE11(pos);
        scrollPanel.setHorizontalScrollPosition(pos);
    }

    /**
     * Смещение видимой части на один шаг влево
     */
    private void scrollLeft() {
        int pos = checkPosIE11(scrollPanel.getHorizontalScrollPosition());
        pos -= SCROLL_STEP * width;
        pos = Math.max(pos, getMinimumHorizontalScrollPosition());
        pos = checkPosIE11(pos);
        scrollPanel.setHorizontalScrollPosition(pos);
    }

    /**
     * Приходится делать эту проверку из-за неверного расчета позиций скролла ie11
     * @param pos   позиция скролла
     * @return  скорректированная позиция скролла
     */
    private int checkPosIE11(int pos) {
        if (Navigator.isIE11 && LocaleInfo.getCurrentLocale().isRTL()) {
            pos = -pos;
        }
        return pos;
    }

    /**
     * Вычисляет позицию скролла соответствующая сдвигу бегунка
     * @param barPosition сдвиг бегунка
     * @return позиция скролла
     */
    private double getScrollPosition(double barPosition) {
        double pct = barPosition / freeTrackSpace;
        return pct * (getMaximumHorizontalScrollPosition() - getMinimumHorizontalScrollPosition());

    }

    /**
     * Возвращает высоту контролируемого контента
     * @return
     */
    @Override
    public int getScrollWidth() {
        System.out.println("getScrollWidth: " + scrollPanel.getWidget().getOffsetWidth());
        return scrollPanel.getWidget().getOffsetWidth();
    }

    @Override
    public void setScrollWidth(int width) {
        contentWidth = width;
        this.width = scrollPanel.getOffsetWidth();

        if (scrollPanel.getOffsetHeight() < scrollPanel.getWidget().getOffsetHeight() + 2) {
            this.width -= Constants.SCROLL_BAR_WIDTH;
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                scrollPanel.getWidget().getElement().getStyle().setMarginLeft(Constants.SCROLL_BAR_WIDTH, Style.Unit.PX);
            } else {
                scrollPanel.getWidget().getElement().getStyle().setMarginRight(Constants.SCROLL_BAR_WIDTH, Style.Unit.PX);
            }
        } else {
            scrollPanel.getWidget().getElement().getStyle().clearMarginLeft();
            scrollPanel.getWidget().getElement().getStyle().clearMarginRight();
        }
        panel.setWidth(this.width + "px");
        barWidth = (int) Math.ceil (((this.width) / (double) width) * (this.width - Constants.SCROLL_BAR_WIDTH * 2));
        freeTrackSpace = this.width - barWidth - (Constants.SCROLL_BAR_WIDTH + 1) * 2;
        bar.setWidth(barWidth + "px");

        if (getHorizontalScrollPosition() == 0) {
            setHorizontalScrollPosition(0);
        }
    }

    @Override
    public int getHorizontalScrollPosition() {
        return scrollPanel.getHorizontalScrollPosition();
    }

    @Override
    public int getMaximumHorizontalScrollPosition() {
        return scrollPanel.getMaximumHorizontalScrollPosition();
    }

    @Override
    public int getMinimumHorizontalScrollPosition() {
        return scrollPanel.getMinimumHorizontalScrollPosition();
    }

    @Override
    public void setHorizontalScrollPosition(int position) {
        position = checkPosIE11(position);
        double barPosition = getBarPosition(position);
        bar.getElement().getStyle().setMarginLeft(barPosition, Style.Unit.PX);
        bar.getElement().getStyle().setMarginRight(freeTrackSpace - barPosition, Style.Unit.PX);
    }

    /**
     * Вычисляет сдвиг бегунка соответствующего текущей позиции скролла
     * @param scrollPosition позиция скролла
     * @return сдвиг бегунка
     */
    private double getBarPosition(int scrollPosition) {
        double scrollPct = ((double) scrollPosition - getMinimumHorizontalScrollPosition()) /
                (getMaximumHorizontalScrollPosition() - getMinimumHorizontalScrollPosition());
        return freeTrackSpace * scrollPct;
    }

    @Override
    public HandlerRegistration addScrollHandler(ScrollHandler handler) {
        return scrollPanel.addScrollHandler(handler);
    }

    public void onLoad() {
        super.onLoad();
        setHorizontalScrollPosition(0);
    }
}
