package kz.arta.synergy.components.client.menu;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 17.07.14
 * Time: 10:50
 * Панель с вертикальным скролом
 */
public class ArtaVerticalScrollPanel extends CustomScrollPanel {
    /**
     * Высота панели
     */
    int height;

    /**
     * Вертикальный скролл
     */
    ArtaVerticalScroll vScroll;

    public ArtaVerticalScrollPanel(int height) {
        super();
        vScroll = new ArtaVerticalScroll(height, this);
        setVerticalScrollbar(vScroll, Constants.SCROLL_BAR_WIDTH);
        addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                vScroll.setVerticalScrollPosition(getVerticalScrollPosition());
            }
        });
    }

    public ArtaVerticalScrollPanel(int height, Widget widget) {
        this(height);
        setWidget(widget);
    }
}
