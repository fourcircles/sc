package kz.arta.synergy.components.client.label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.ArtaHasText;
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

    /**
     * Заданная ширина
     */
    private int width;

    /**
     * Задана ли ширина.
     */
    private boolean widthSet;

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

        getElement().getStyle().setProperty("boxSizing", "border-box");

        panel.add(textLabel);
        textLabel.getElement().getStyle().setOverflowX(Style.Overflow.VISIBLE);
        textLabel.setStyleName("");

        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);

        gradient.setStyleName(SynergyComponents.resources.cssComponents().gradient());
    }

    /**
     * Добавляет градиент, если текст слишком длинный для заданной ширины элемента.
     * Метод вызывается при изменении текста, стиля текста, ширины виджета и при присоединении к DOM.
     */
    public void adjustGradient() {
        if (isAttached() && widthSet && Utils.getTextWidth(this) > width) {
            if (Utils.getTextWidth(this) > width) {
                panel.add(gradient);
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (widthSet) {
            setWidth(width);
        }
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

    /**
     * Задает ширину включая все кроме margins.
     * @param width ширина
     */
    public void setWidth(int width) {
        widthSet = true;
        this.width = width;
        if (isAttached()) {
            super.setWidth(width + "px");
        }
        adjustGradient();
    }



    /**
     * Снимает заданную ширину, виджет растягивается на длину текста.
     */
    public void clearWidth() {
        widthSet = false;
        getElement().getStyle().clearWidth();
        panel.remove(gradient);
    }

    /**
     * Ширину задавать надо в пикселях
     */
    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("ширина текста с градиентом задается используя целое значение в пикселях");
    }

    @Override
    public void setHeight(String height) {
        textLabel.setHeight(height);
        gradient.setHeight(height);
        super.setHeight(height);
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
}
