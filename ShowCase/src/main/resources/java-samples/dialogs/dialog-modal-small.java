import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.dialog.DialogSimple;

public class Sample {
    public static void main(String[] args) {
        // кнопка, при нажатии показывает диалог (модальный)
        final SimpleButton showTiny = new SimpleButton(SCMessages.i18n().tr("Показать небольшой диалог"));
        showTiny.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogSimple dialog = new DialogSimple();

                dialog.setText(SCMessages.i18n().tr("Небольшой диалог"));

                dialog.setModal(true);

                // содержимое диалога
                FlowPanel content = new FlowPanel();
                content.getElement().getStyle().setWidth(116, Style.Unit.PX);
                content.getElement().getStyle().setHeight(84, Style.Unit.PX);
                dialog.setContent(content);

                dialog.center();
                dialog.show();
            }
        });
    }
}