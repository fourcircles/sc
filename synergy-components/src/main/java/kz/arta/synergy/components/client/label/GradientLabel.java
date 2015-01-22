package kz.arta.synergy.components.client.label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.StyleUtils;
import kz.arta.synergy.components.client.util.Utils;

/**
 * User: vsl
 * Date: 02.07.14
 * Time: 18:02
 * Label с градиентом
 *
 * При инициализации необходимо указать стиль текста для оценки ширины.
 * Высота зависит от родителя (100%).
 *
 * Ширина задается методом setWidth(int). Если ширину не задавать, то виджет
 * растянется до ширины текста.
 *
 * Можно задавать ширину как до отображения, так и после.
 */
public class GradientLabel extends Composite implements ArtaHasText {

    /**
     * Главная панель
     */
    FlowPanel panel;

    /**
     * Градиент
     */
    protected FlowPanel gradient = GWT.create(FlowPanel.class);

    /**
     * Текст
     */
    private InlineLabel textLabel = GWT.create(InlineLabel.class);

    /**
     * Стиль текста
     */
    private String textStyle;

    public GradientLabel(String textStyle) {
        this();
        this.textStyle = textStyle;
        addStyleName(textStyle);
    }

    /**
     * Этот конструктор нужен только для тестирования
     */
    protected GradientLabel() {
        panel = new FlowPanel();
        initWidget(panel);
        panel.getElement().getStyle().setPosition(Style.Position.RELATIVE);

        getElement().getStyle().setProperty("boxSizing", "border-box");

        panel.add(textLabel);
        textLabel.getElement().getStyle().setOverflowX(Style.Overflow.VISIBLE);
        textLabel.setStyleName("");

        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        StyleUtils.setWhiteSpace(getElement(), StyleUtils.WhiteSpace.NOWRAP);

        gradient.setStyleName(SynergyComponents.getResources().cssComponents().gradient());
    }

    /**
     * Добавляет градиент, если текст слишком длинный для заданной ширины элемента.
     * Метод вызывается при изменении текста, стиля текста, ширины виджета и при присоединении к DOM.
     */
    public void adjustGradient() {
        getElement().getStyle().setPadding(0, Style.Unit.PX);
        if (isAttached()) {
            if (Utils.impl().getPreciseWidth(getElement()) < Utils.impl().getPreciseTextWidth(this)) {
                panel.add(gradient);
            } else {
                panel.remove(gradient);
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        adjustGradient();
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

    @Override
    public String getFontStyle() {
        return textStyle;
    }

    /**
     * Задает новый стиль текста.
     * При этом возможно увеличение ширины текста, поэтому возможно добавление градиента.
     * @param textStyle новый стиль текста
     */
    public void setFontStyle(String textStyle) {
        if (this.textStyle != null) {
            textLabel.removeStyleName(this.textStyle);
        }
        this.textStyle = textStyle;
        textLabel.addStyleName(textStyle);
        adjustGradient();
    }

    public void setWidth(int width) {
        getElement().getStyle().setWidth(width, Style.Unit.PX);
        adjustGradient();
    }

    public void clearWidth() {
        getElement().getStyle().clearWidth();
        adjustGradient();
    }
}
