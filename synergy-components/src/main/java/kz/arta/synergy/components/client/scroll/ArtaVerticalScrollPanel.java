package kz.arta.synergy.components.client.scroll;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 17.07.14
 * Time: 10:50
 * Панель с вертикальным скролом.
 * Горизонтальный скролл не отображается.
 */
public class ArtaVerticalScrollPanel extends CustomScrollPanel {
    /**
     * Вертикальный скролл
     */
    ArtaVerticalScroll vScroll;

    public ArtaVerticalScrollPanel() {
        super();
        vScroll = new ArtaVerticalScroll(this);
        setVerticalScrollbar(vScroll, Constants.SCROLL_BAR_WIDTH);
        removeHorizontalScrollbar();
        addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                vScroll.setVerticalScrollPosition(getVerticalScrollPosition());
            }
        });
        //без этого если ширина контента меньше ширины родителя, то выделяться будет только часть строки
        getContainerElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.BLOCK);
    }

    public ArtaVerticalScrollPanel(Widget widget) {
        this();
        setWidget(widget);
    }
}
