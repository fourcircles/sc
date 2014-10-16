import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.checkbox.ArtaCheckBox;
import kz.arta.synergy.components.client.checkbox.ArtaRadioButton;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.stack.SingleStack;
import kz.arta.synergy.components.client.stack.StackPanel;
import kz.arta.synergy.components.client.stack.events.StackOpenEvent;
import kz.arta.synergy.components.client.theme.ColorType;

import java.util.Arrays;
import java.util.List;

public class Sample {
    public static void main(String[] args) {
        final StackPanel stacks = new StackPanel(Arrays.asList(
                new SingleStack(SCMessages.i18n().tr("Первая")),
                new SingleStack(SCMessages.i18n().tr("Вторая")),
                new SingleStack(SCMessages.i18n().tr("Третья")),
                new SingleStack(SCMessages.i18n().tr("Четвертая"))), 500);
        stacks.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        // создание панели большого размера для отображения скролла
        ArtaScrollPanel scroll1 = new ArtaScrollPanel(ColorType.BLACK);
        FlowPanel colorButtonPanel = new FlowPanel();
        colorButtonPanel.setPixelSize(500, 1000);
        scroll1.setWidget(colorButtonPanel);
        scroll1.setHeight("100%");

        // добавление панели в первую стек-панель
        stacks.getStacks().get(0).getPanel().add(scroll1);

        List<ArtaCheckBox> boxes = new ArrayList<ArtaCheckBox>();
        final List<ArtaRadioButton> radios = new ArrayList<ArtaRadioButton>();

        for (int i = 0; i < stacks.getStacks().size(); i++) {
            final SingleStack stack = stacks.getStacks().get(i);

            ArtaCheckBox box = new ArtaCheckBox();
            box.setValue(true, false);
            // чекбокс управляет включением-выключением панели
            box.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    stack.setEnabled(event.getValue());
                }
            })
            boxes.add(box);

            ArtaRadioButton radio = new ArtaRadioButton("stack");
            // включение одной из радио-кнопок открывает вкладку
            radio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    stacks.openStack(stack, true);
                }
            });
            radios.add(radio);
        }

        //первая вкладка открыта по-умолчанию
        radios.get(0).setValue(true, false);

        //соответствие открытой вкладки и выбранной радио-кнопки
        stacks.addStackOpenHandler(new StackOpenEvent.Handler() {
            @Override
            public void onStackOpened(StackOpenEvent event) {
                int index = event.getIndex();
                radios.get(index).setValue(true, false);
            }
        })

    }
}

