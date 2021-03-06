import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.sc3.showcase.client.ShowCase;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.button.ButtonBase;
import kz.arta.synergy.components.client.button.SimpleButton;

public class Sample {
    public static void main(String[] args) {
        SimpleButton iconButton = new SimpleButton(Messages.i18n().tr("Кнопка с длинным текстом"), ShowCase.IMAGES.zoom(), ButtonBase.IconPosition.RIGHT);

        iconButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //действие при клике
            }
        });
    }
}
