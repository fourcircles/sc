package kz.arta.synergy.components.client.menu;

import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 15.07.14
 * Time: 13:40
 *
 * Выпадающий список
 */
public abstract class DropDownList extends MenuBase {
    /**
     * Панель с вертикальным скроллом
     */
    ArtaVerticalScrollPanel scroll;

    public DropDownList(Widget parent) {
        super();
        scroll = new ArtaVerticalScrollPanel(200, panel);
        setWidget(scroll);

        setRelativeWidget(parent);
        setHeight("200px");
    }

    /**
     * Указывает что максимальная ширина списка равна родительской без границ
     */
    @Override
    public void showUnderParent() {
        if (relativeWidget != null && relativeWidget.isAttached()) {
            getElement().getStyle().setProperty("maxWidth", relativeWidget.getOffsetWidth() - 4 * 2 + "px");
            super.showUnderParent();
        }
    }

    /**
     * При выборе элемента клавиатурой он должен быть видим
     * @param item элемент
     * @param mouseSelected способ выбора
     */
    @Override
    protected void overItem(MenuItem item, boolean mouseSelected) {
        if (!mouseSelected) {
            scroll.ensureVisible(item.asWidget());
        }
        super.overItem(item, mouseSelected);
    }


    @Override
    protected String getMainStyle() {
        return SynergyComponents.resources.cssComponents().contextMenu();
    }
}
