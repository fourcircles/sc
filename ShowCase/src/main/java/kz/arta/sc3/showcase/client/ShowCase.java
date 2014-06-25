package kz.arta.sc3.showcase.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * User: user
 * Date: 20.06.14
 * Time: 16:47
 */
public class ShowCase implements EntryPoint {
    public StyleElement styleElement = StyleInjector.injectStylesheet("");

    @Override
    public void onModuleLoad() {
        Window.setMargin("0px");
        RootLayoutPanel.get().add(new ShowCasePanel(styleElement));
    }

}
