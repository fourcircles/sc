package kz.arta.synergy.components.client.label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 02.07.14
 * Time: 18:02
 * Label с градиентом
 */
public class GradientLabel extends FlowPanel {

    //todo применять градиент к границе кнопки или текста
    /**
     * Градиент
     */
    protected FlowPanel gradient = GWT.create(FlowPanel.class);

    /**
     * Текст
     */
    private InlineLabel textLabel = GWT.create(InlineLabel.class);

    public GradientLabel() {
        add(textLabel);
        getElement().getStyle().setProperty("wordWrap", "break-word");
        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);

        gradient.setStyleName(SynergyComponents.resources.cssComponents().gradient());
        gradient.getElement().getStyle().setMarginRight(-10, Style.Unit.PX);
        setHeight(Constants.buttonHeight());
    }

    public GradientLabel(String text) {
        this();
        textLabel.setText(text);
    }

    /**
     * Определяент влезает ли текст элемент.
     * @return
     */
    protected boolean textFits() {
        textLabel.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        int oldHeight = textLabel.getOffsetHeight();
        textLabel.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NORMAL);
        int newHeight = textLabel.getOffsetHeight();
        textLabel.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        return oldHeight == newHeight;
    }

    /**
     * Добавляет градиент, если текст слишком длинный для текущей ширины элемента.
     */
    protected void adjustGradient() {
        if (!textFits()) {
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                insert(gradient, 0);
            } else {
                add(gradient);
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                adjustGradient();
            }
        });
    }

    /**
     * Задает текст элемента, в случае надобности добавляется градиент.
     * @param text текст
     */
    public void setText(String text) {
        textLabel.setText(text);
        adjustGradient();
    }

    public String getText() {
        return textLabel.getText();
    }

    /**
     * Изменяет ширину, в случае надобности добавляется градиент.
     * @param width
     */
    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        adjustGradient();
    }
}
