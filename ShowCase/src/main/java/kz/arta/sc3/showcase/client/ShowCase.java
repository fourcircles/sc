package kz.arta.sc3.showcase.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import kz.arta.sc3.showcase.client.resources.SCResources;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: user
 * Date: 20.06.14
 * Time: 16:47
 */
public class ShowCase implements EntryPoint {

    @Override
    public void onModuleLoad() {
        ScriptInjector.fromString(SCResources.IMPL.highlightJs().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
        SCResources.IMPL.gitHub().ensureInjected();

        Window.setMargin("0px");
        RootPanel.get().getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        RootPanel.get().add(new ShowCasePanel());
        RootPanel.get().addStyleName(SynergyComponents.getResources().cssComponents().mainText());
    }
}
