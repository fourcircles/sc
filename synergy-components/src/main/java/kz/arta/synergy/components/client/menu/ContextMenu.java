package kz.arta.synergy.components.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 09.07.14
 * Time: 15:14
 *
 * Конекстное меню
 */
public class ContextMenu extends MenuBase {
    /**
     * Разделитель
     */
    final static MenuItem SEPARATOR = null;

    @Override
    protected void overItem(MenuItem item, boolean mouseSelected) {
        if (item == SEPARATOR) {
            return;
        }
        super.overItem(item, mouseSelected);
    }

    /**
     * Определяет является ли элемент меню разделителем
     * @param item элемент меню
     * @return true - является, false - нет
     */
    private boolean isSeparator(MenuItem item) {
        return item == SEPARATOR;
    }

    private boolean isSeparator(int index) {
        return isSeparator(items.get(index));
    }

    /**
     * Добавляет разделитель в конец меню
     */
    public void addSeparator() {
        items.add(SEPARATOR);
        FlowPanel separatorPanel = GWT.create(FlowPanel.class);
        separatorPanel.setStyleName(SynergyComponents.resources.cssComponents().menuSeparator());
        panel.add(separatorPanel);
    }

    /**
     * Разделители не могут быть выбраны
     * @param item элемент списка
     * @return true - элемент не разделитель и может быть выбран, false - иначе
     */
    @Override
    protected boolean canBeChosen(MenuItem item) {
        return !isSeparator(item);
    }

    /**
     * Возвращает название главного стиля для меню
     * @return название стиля
     */
    @Override
    protected String getMainStyle() {
        return SynergyComponents.resources.cssComponents().contextMenu();
    }

    /**
     * При выборе элемента выполняется команда, если она есть
     * @param item выбранный элемент
     */
    @Override
    protected void itemSelected(MenuItem item) {
        hide();
        if (item.command != null) {
            item.command.execute();
        }
    }

    /**
     * Выравнивает меню так, чтобы его верхний левый угол имел заданные координаты. Если при этом
     * меню выходит за пределы окна браузера, например за правую границу, заданные координаты будет
     * иметь верхний правый угол и т. д.
     * @param posX координата X
     * @param posY координата Y
     */
    public void smartShow(int posX, int posY) {
        show();
        int lenX = getOffsetWidth();
        int lenY = getOffsetHeight();

        if (posX + lenX > Window.getClientWidth()) {
            posX -= lenX;
        }
        if (posY + lenY > Window.getClientHeight()) {
            posY -= lenY;
        }

        setPopupPosition(posX, posY);
    }
}
