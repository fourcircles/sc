package kz.arta.synergy.components.client.label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Utils;

/**
 * User: vsl
 * Date: 01.10.14
 * Time: 15:00
 *
 * Улучшеный лейбл с градиентом
 * //todo заменить градиент
 */
public class GradientLabel2 extends Composite implements ArtaHasText {
    private final double eps = 0.01;

    /**
     * Градиент
     */
    protected FlowPanel gradient;

    private String font;

    private FlowPanel root;
    private InlineLabel label;

    public GradientLabel2(String font) {
        root = new FlowPanel();
        initWidget(root);
        root.getElement().getStyle().setFontSize(0, Style.Unit.PX);
        root.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        root.getElement().getStyle().setPosition(Style.Position.RELATIVE);

        this.font = font;

        label = GWT.create(InlineLabel.class);
        label.setText("");
        label.setStyleName(font);
        root.add(label);

        gradient = new FlowPanel();
        gradient.setStyleName(SynergyComponents.getResources().cssComponents().gradient());
    }

    /**
     * Добавляет или удаляет градиент в зависимости от размеров.
     * Должен вызываться после изменения размера.
     */
    public void adjustGradient() {
        getElement().getStyle().setPadding(0, Style.Unit.PX);
        if (isAttached()) {
            double width = Utils.impl().getPreciseWidth(getElement());
            double textWidth = Utils.impl().getPreciseTextWidth(getText(), font);
            if (Math.abs(width - textWidth) > eps && width < textWidth) {
                root.add(gradient);
            } else {
                root.remove(gradient);
            }
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        adjustGradient();
    }

    public void setFontStyle(String font) {
        if (this.font != null) {
            removeStyleName(this.font);
        }
        this.font = font;
        addStyleName(this.font);

        adjustGradient();
    }

    @Override
    public String getFontStyle() {
        return font;
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setText(String text) {
        label.setText(text);
        adjustGradient();
    }
}
