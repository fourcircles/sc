package kz.arta.synergy.components.client.scroll;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 17.07.14
 * Time: 10:50
 * Скролл панель Synergy
 */
public class ArtaScrollPanel extends CustomScrollPanel {
    /**
     * Вертикальный скролл
     */
    ArtaVerticalScroll vScroll;

    /**
     * Горизонтальный скролл
     */
    ArtaHorizontalScroll hScroll;

    public ArtaScrollPanel() {
        super();
        vScroll = new ArtaVerticalScroll(this);
        hScroll = new ArtaHorizontalScroll(this);

        setVerticalScrollbar(vScroll, Constants.SCROLL_BAR_WIDTH);
        setHorizontalScrollbar(hScroll, Constants.SCROLL_BAR_HEIGHT);
        vScroll.setVerticalScrollPosition(0);
        hScroll.setHorizontalScrollPosition(0);
        addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                vScroll.setVerticalScrollPosition(getVerticalScrollPosition());
                hScroll.setHorizontalScrollPosition(getHorizontalScrollPosition());
            }
        });
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                vScroll.setScrollHeight(event.getHeight());
                hScroll.setScrollWidth(event.getWidth());
            }
        });
    }

    public ArtaScrollPanel(Widget widget) {
        this();
        setWidget(widget);
    }
}
