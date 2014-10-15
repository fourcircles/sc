import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import kz.arta.sc3.showcase.client.ShowCase;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.button.ContextMenuButton;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.menu.ContextMenu;
import kz.arta.synergy.components.client.resources.ImageResources;

public class Sample {
    public static void main(String[] args) {
        ContextMenu menu = new ContextMenu();
        menu.addItem("Zoom", ImageResources.IMPL.zoom(), null);
        menu.addItem("Left", ImageResources.IMPL.navigationLeft(), null);
        menu.addSeparator();
        menu.addItem("Right", ImageResources.IMPL.navigationRight(), null);

        ContextMenuButton button = new ContextMenuButton(SCMessages.i18n().tr("Кнопка с меню"), SimpleButton.Type.APPROVE);

        button.setContextMenu(menu);

        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //действие при клике
            }
        });
    }
}
