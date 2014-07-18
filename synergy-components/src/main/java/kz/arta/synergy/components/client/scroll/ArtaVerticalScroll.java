package kz.arta.synergy.components.client.scroll;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;

/**
 * User: vsl
 * Date: 16.07.14
 * Time: 17:11
 * Вертикальный скролл
 */
class ArtaVerticalScroll extends Composite implements VerticalScrollbar{
    /**
     * Часть экрана, которая будет пролистываться при нажатии кнопок
     * или при клике вне бегунка
     */
    private static final double SCROLL_STEP = 0.8;

    /**
     * Основная панель
     */
    FlowPanel panel;

    /**
     * Кнопка вверх
     */
    Image up;

    /**
     * Кнопка вниз
     */
    Image down;

    /**
     * Бегунок
     */
    FlowPanel bar;

    /**
     * Высота скроллбара
     */
    int height;

    private int dragStartY;
    boolean dragging = false;

    private CustomScrollPanel scrollPanel;

    /**
     * Высота бегунка
     */
    private int barHeight;

    /**
     * Высота скроллбара без кнопок и бегунка
     */
    private int freeTrackSpace;

    public ArtaVerticalScroll(int height, final CustomScrollPanel scrollPanel) {
        this.scrollPanel = scrollPanel;
        this.height = height;

        panel = new FlowPanel();
        initWidget(panel);

        up = new Image(ImageResources.IMPL.navigationUp());
        up.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                scrollUp();
            }
        });

        down = new Image(ImageResources.IMPL.navigationDown());
        down.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                scrollDown();
            }
        });

        bar = new FlowPanel();
        bar.setStyleName(SynergyComponents.resources.cssComponents().scrollbar());
        MouseDownHandler barMouseDown = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
                barDown(event.getY());
            }
        };
        MouseUpHandler barMouseUp = new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.stopPropagation();
                barUp();
            }
        };
        MouseMoveHandler barMouseMove = new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                event.preventDefault();
                barMove(event.getClientY());
            }
        };
        bar.addDomHandler(barMouseDown, MouseDownEvent.getType());
        bar.addDomHandler(barMouseUp, MouseUpEvent.getType());
        bar.addDomHandler(barMouseMove, MouseMoveEvent.getType());

        panel.setHeight(height + "px");

        panel.add(up);
        panel.add(bar);
        panel.add(down);

        ClickHandler panelClick = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //клик по свободной части скроллбара
                int barTop = bar.getAbsoluteTop();
                int barBottom = bar.getAbsoluteTop() + barHeight;
                if (event.getClientY() < barTop) {
                    scrollUp();
                } else if (event.getClientY() > barBottom) {
                    scrollDown();
                }
            }
        };
        panel.addDomHandler(panelClick, ClickEvent.getType());

        panel.setStyleName(SynergyComponents.resources.cssComponents().vscroll());
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
     * Нажатие на бегунок, начало его перемещения
     * @param y координата y относительно бегунка
     */
    private void barDown(int y) {
        bar.addStyleName(SynergyComponents.resources.cssComponents().pressed());
        dragging = true;
        dragStartY = y;
        Event.setCapture(bar.getElement());
    }

    /**
     * Завершение перемещения бегунка
     */
    private void barUp() {
        bar.removeStyleName(SynergyComponents.resources.cssComponents().pressed());
        dragging = false;
        Event.releaseCapture(bar.getElement());
    }

    /**
     * Перемещение бегунка
     * @param clientY координата Y мыши
     */
    private void barMove(int clientY) {
        if (dragging) {
            int dragAreaStart = up.getAbsoluteTop() + up.getOffsetHeight();

            int barTop = clientY - dragStartY;
            int marginTop = barTop - dragAreaStart;

            scrollPanel.setVerticalScrollPosition((int) getScrollPosition(marginTop));
        }
    }

    /**
     * Возвращает вышину контролируемого контента
     * @return
     */
    @Override
    public int getScrollHeight() {
        return height + getMaximumVerticalScrollPosition();
    }

    @Override
    public void setScrollHeight(int height) {
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

    @Override
    protected void onLoad() {
        super.onLoad();
        barHeight = (int) ((double) height / (height + getMaximumVerticalScrollPosition()) * height);
        bar.setHeight(barHeight + "px");
        freeTrackSpace = height - barHeight - 17 * 2;
        setVerticalScrollPosition(0);
    }
}
