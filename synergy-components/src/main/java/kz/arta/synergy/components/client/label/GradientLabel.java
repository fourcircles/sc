package kz.arta.synergy.components.client.label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 02.07.14
 * Time: 18:02
 */
public class GradientLabel extends FlowPanel {
    protected FlowPanel gradient = GWT.create(FlowPanel.class);

    Command callback;

    private InlineLabel textLabel = GWT.create(InlineLabel.class);

    public GradientLabel() {
        add(textLabel);
        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);

        gradient.setStyleName(SynergyComponents.resources.cssComponents().gradient());
        gradient.getElement().getStyle().setMarginRight(-10, Style.Unit.PX);
        setHeight("32px");
    }

    public GradientLabel(String text) {
        this();
        textLabel.setText(text);
    }

    protected boolean textFits() {
        int oldHeight = textLabel.getOffsetHeight();
        textLabel.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NORMAL);
        int newHeight = textLabel.getOffsetHeight();
        textLabel.getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        return oldHeight == newHeight;
    }

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

                if (callback != null) {
                    callback.execute();
                }
            }
        });
    }

    public void setSizeCallback(Command callback) {
        this.callback = callback;
    }

    public void setText(String text) {
        textLabel.setText(text);
        adjustGradient();
    }

    public String getText() {
        return textLabel.getText();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        adjustGradient();
    }
}
