package kz.arta.sc3.showcase.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import kz.arta.sc3.showcase.client.resources.ImageResources;
import kz.arta.sc3.showcase.client.resources.SCResources;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.dialog.DialogSimple;
import kz.arta.synergy.components.client.util.mousetracking.IdleEvent;
import kz.arta.synergy.components.client.util.mousetracking.MouseTracking;

/**
 * User: user
 * Date: 20.06.14
 * Time: 16:47
 */
public class ShowCase implements EntryPoint {

    public static final SCResources RESOURCES = GWT.create(SCResources.class);
    public static final ImageResources IMAGES = GWT.create(ImageResources.class);

    @Override
    public void onModuleLoad() {
        ScriptInjector.fromString(RESOURCES.highlightJs().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
        RESOURCES.gitHub().ensureInjected();
        RESOURCES.css().ensureInjected();

        Window.setMargin("0px");
        RootPanel.get().getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        RootPanel.get().add(new ShowCasePanel());
        RootPanel.get().addStyleName(SynergyComponents.getResources().cssComponents().mainText());

        MouseTracking.enable();
        MouseTracking.addIdleHandler(new IdleEvent.Handler() {
            @Override
            public void onMouseTracking(IdleEvent event) {
                MouseTracking.disable();
                
                DialogSimple idle = new DialogSimple(true);
                idle.setWidth("280px");
                
                idle.setText("You've been idle too long");
                idle.center();
                idle.show();

                idle.addCloseButtonHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        MouseTracking.enable();
                    }
                });
            }
        });
    }
}
