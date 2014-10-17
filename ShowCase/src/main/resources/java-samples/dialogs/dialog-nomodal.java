import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.dialog.DialogSimple;

public class Sample {
    private int dialogCount = 0;

    public static void main(String[] args) {
        SimpleButton newDialog = new SimpleButton(SCMessages.i18n().tr("Добавить новый диалог"));
        newDialog.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogSimple dialog = new DialogSimple();

                dialog.setText(SCMessages.i18n().tr("Диалог") + " #" + dialogCount++);

                dialog.setModal(true);

                // содержимое диалога
                FlowPanel content = new FlowPanel();
                content.getElement().getStyle().setWidth(400, Style.Unit.PX);
                content.getElement().getStyle().setHeight(400, Style.Unit.PX);
                dialog.setContent(content);

                // добавление в панель задач
                getTaskBar().addItem(dialog);

                dialog.show();
            }
        });
    }
}