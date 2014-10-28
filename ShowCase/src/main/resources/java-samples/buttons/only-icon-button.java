import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import kz.arta.sc3.showcase.client.ShowCase;
import kz.arta.synergy.components.client.button.ImageButton;

public class Sample {
    public static void main(String[] args) {
        ImageButton iconButton = new ImageButton(ShowCase.IMAGES.zoom());

        iconButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //действие при клике
            }
        });
    }
}
