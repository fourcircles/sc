package kz.arta.synergy.components.client.scroll;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 17.07.14
 * Time: 10:50
 * Панель с вертикальным скролом.
 * Горизонтальный скролл не отображается.
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
        //без этого если ширина контента меньше ширины родителя, то выделяться будет только часть строки
//        getContainerElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.BLOCK);
    }

    public ArtaScrollPanel(Widget widget) {
        this();
        setWidget(widget);
    }
}
