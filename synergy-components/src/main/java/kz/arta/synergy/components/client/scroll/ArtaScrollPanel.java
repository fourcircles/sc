package kz.arta.synergy.components.client.scroll;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.theme.ColorType;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 17.07.14
 * Time: 10:50
 * Скролл панель Synergy
 */
public class ArtaScrollPanel extends CustomScrollPanel {

    MyScrollResources resources = (MyScrollResources) GWT.create(MyScrollResources.class);

    /**
     * Вертикальный скролл
     */
    ArtaVerticalScroll vScroll;

    /**
     * Горизонтальный скролл
     */
    ArtaHorizontalScroll hScroll;

    public interface MyScrollResources extends Resources{

        @Source("css/Scroll.css")
        Style customScrollPanelStyle();
    }

    public interface Style extends CustomScrollPanel.Style {
        /**
         * Applied to the widget.
         */
        String customScrollPanel();

        /**
         * Applied to the square that appears in the bottom corner where the
         * vertical and horizontal scrollbars meet, when both are visible.
         */
        String customScrollPanelCorner();

        String dark();
    }

    public ArtaScrollPanel() {
        super((MyScrollResources) GWT.create(MyScrollResources.class));
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
                ArtaScrollPanel.this.onResize();
            }
        });
    }

    public ArtaScrollPanel(ColorType type) {
        this();
        if (type == ColorType.BLACK) {
            vScroll.panel.addStyleName(SynergyComponents.getResources().cssComponents().dark());
            vScroll.setType(type);
            hScroll.panel.addStyleName(SynergyComponents.getResources().cssComponents().dark());
            hScroll.setType(type);
            addStyleName(resources.customScrollPanelStyle().dark());
        }
    }

    public ArtaScrollPanel(Widget widget) {
        this();
        setWidget(widget);
    }
}
