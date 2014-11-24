import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.collapsing.CollapsingPanel;
import kz.arta.synergy.components.client.input.TextInput;
import kz.arta.synergy.components.style.client.Constants;

public class Sample {
    public static void main(String[] args) {
        CollapsingPanel meta = new CollapsingPanel(Messages.i18n().tr("Метаданные"));
        meta.setWidth("500px");
        meta.getPanel().getElement().getStyle().setPadding(18, Style.Unit.PX);

        FlowPanel row = new FlowPanel();
        row.getElement().getStyle().setLineHeight(32, Style.Unit.PX);
        row.setStyleName(SynergyComponents.getResources().cssComponents().mainText());

        InlineLabel label = new InlineLabel(labelName);
        label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        label.setWidth(textWidth + "px");

        row.add(label);

        TextInput textInput = new TextInput();
        textInput.setWidth(inputWidth - Constants.COMMON_INPUT_PADDING * 2 - Constants.BORDER_WIDTH * 2 + "px");

        row.add(textInput);

        //добавление контента в коллапсинг-панель
        meta.getPanel().add(row);

        // добавление кнопки в коллапсинг панель
        meta.addButton(Messages.i18n().tr("Кнопка"));
        meta.showButton();
        meta.addButtonClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // click
            }
        });
    }
}

