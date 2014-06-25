package kz.arta.sc3.showcase.client.css;

import com.google.gwt.resources.client.CssResource;

/**
 * User: vsl
 * Date: 25.06.14
 * Time: 10:53
 */
public interface CssTheme extends CssResource{
    @ClassName("gwt-TabLayoutPanelTab")
    String gwtTabLayoutPanelTab();

    @ClassName("gwt-TabLayoutPanel")
    String gwtTabLayoutPanel();

    @ClassName("gwt-TabLayoutPanelContent")
    String gwtTabLayoutPanelContent();

    String tabText();

    @ClassName("gwt-TabLayoutPanelTab-selected")
    String gwtTabLayoutPanelTabSelected();
}
