import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.dialog.Dialog;
import kz.arta.synergy.components.client.input.TextInput;

public class Sample {
    public static void main(String[] args) {
        final TextInput input = new TextInput();
        input.setAllowEmpty(false);

        final SimpleButton newDialog = new SimpleButton(Messages.i18n().tr("Добавить новый диалог"));
        newDialog.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                // если присутствует текст в текстовом поле
                if (input.checkInput()) {
                    Dialog dialog = new Dialog();

                    dialog.setText(input.getText());

                    dialog.setModal(false);

                    // содержимое диалога
                    FlowPanel content = new FlowPanel();
                    content.getElement().getStyle().setWidth(400, Style.Unit.PX);
                    content.getElement().getStyle().setHeight(400, Style.Unit.PX);
                    dialog.setContent(content);

                    dialog.setLeftButtonVisible(true);
                    dialog.setRightButtonVisible(true);

                    // добавление в панель задач
                    getTaskBar().addItem(dialog);
                    input.setValue("", false);

                    //диалог сразу не показывается -- он в панели задач
                }
            }
        });
    }
}