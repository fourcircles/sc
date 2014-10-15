import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.sc3.showcase.client.ShowCase;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.button.ButtonBase;
import kz.arta.synergy.components.client.button.ContextMenuButton;

public class Sample {
    public static void main(String[] args) {
        ContextMenuButton iconButton = new ContextMenuButton(SCMessages.i18n().tr("Кнопка с меню"), ShowCase.SC_IMAGES.zoom(), ButtonBase.IconPosition.RIGHT);
        iconButton.setWidth("400px");

        iconButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //действие при клике
            }
        });
    }
}
