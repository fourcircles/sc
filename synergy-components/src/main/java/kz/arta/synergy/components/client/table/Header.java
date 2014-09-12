package kz.arta.synergy.components.client.table;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.label.GradientLabel;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 03.09.14
 * Time: 17:27
 *
 * Заголовок.
 */
public class Header extends Composite implements ArtaHasText,
        HasClickHandlers, HasMouseDownHandlers, HasMouseUpHandlers, HasMouseMoveHandlers {
    public static final boolean DEFAULT_IS_ASCENDING = true;
    /**
     * Корневая панель
     */
    private ArtaFlowPanel root;

    /**
     * Отсортирован ли столбец
     */
    private boolean isSorted = false;

    /**
     * В каком направлении отсортирован
     */
    private boolean isAscending = DEFAULT_IS_ASCENDING;

    /**
     * Текст
     */
    private final GradientLabel label;

    /**
     * Картинка для на правления сортировки
     */
    private final Image image;

    public Header(String text) {
        root = new ArtaFlowPanel();
        initWidget(root);
        root.addStyleName(SynergyComponents.resources.cssComponents().header());

        label = new GradientLabel(SynergyComponents.resources.cssComponents().mainText());
        label.setText(text);

        image = new Image();

        root.add(label);
    }

    @Override
    public String getFontStyle() {
        return SynergyComponents.resources.cssComponents().mainText();
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }

    /**
     * Изменить вид заголовка
     * @param isSorted отсортирован ли
     * @param isAscending направление сортировки, не учитывается при isSorted == false
     */
    public void setSorted(boolean isSorted, boolean isAscending) {
        if (isSorted) {
            this.isAscending = isAscending;

            root.add(image);
            if (isAscending) {
                image.setResource(ImageResources.IMPL.navigationDown());
            } else {
                image.setResource(ImageResources.IMPL.navigationUp());
            }
        } else {
            root.remove(image);
            this.isAscending = DEFAULT_IS_ASCENDING;
        }
        this.isSorted = isSorted;
        setWidth(getOffsetWidth());
    }

    /**
     * Изменить вид сортировки. Если заголовок уже был отсортирован - изменяет направление
     * @param isSorted отсортирован ли
     */
    public void setSorted(boolean isSorted) {
        boolean isAscending;
        if (!this.isSorted) {
            isAscending = DEFAULT_IS_ASCENDING;
        } else {
            isAscending = !this.isAscending;
        }
        setSorted(isSorted, isAscending);
    }

    public boolean isAscending() {
        return isAscending;
    }

    public boolean isSorted() {
        return isSorted;
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return root.addClickHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return root.addMouseDownHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return root.addMouseMoveHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return root.addMouseUpHandler(handler);
    }

    public GradientLabel getLabel() {
        return label;
    }

    public void setWidth(int width) {
        width -= 18;
        if (isSorted) {
            width -= 10 + Constants.STD_ICON_WIDTH + 5;
        }
        label.setWidth(width);
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("Задание ширины производится через setWidth(int)");
    }
}
