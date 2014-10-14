package kz.arta.sc3.showcase.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import kz.arta.sc3.showcase.client.resources.SCImageResources;
import kz.arta.sc3.showcase.client.resources.SCResources;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: user
 * Date: 20.06.14
 * Time: 16:47
 */
public class ShowCase implements EntryPoint {

    public static final SCResources SC_RESOURCES = GWT.create(SCResources.class);
    public static final SCImageResources SC_IMAGES = com.google.gwt.core.client.GWT.create(SCImageResources.class);

    @Override
    public void onModuleLoad() {
        ScriptInjector.fromString(SC_RESOURCES.highlightJs().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
        SC_RESOURCES.gitHub().ensureInjected();

        Window.setMargin("0px");
        RootPanel.get().getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        RootPanel.get().add(new ShowCasePanel());
        RootPanel.get().addStyleName(SynergyComponents.getResources().cssComponents().mainText());
    }
}
