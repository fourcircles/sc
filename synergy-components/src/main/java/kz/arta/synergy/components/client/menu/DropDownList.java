package kz.arta.synergy.components.client.menu;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.HasSelectionEventHandlers;
import kz.arta.synergy.components.client.menu.events.SelectionEvent;
import kz.arta.synergy.components.client.menu.events.SelectionEventHandler;
import kz.arta.synergy.components.client.scroll.ArtaVerticalScrollPanel;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 15.07.14
 * Time: 13:40
 *
 * Выпадающий список
 */
public class DropDownList extends MenuBase implements HasSelectionEventHandlers<MenuBase.MenuItem> {
    /**
     * Панель с вертикальным скроллом
     */
    CustomScrollPanel scroll;

    /**
     * Примененный префикс
     */
    String prefix;

    public DropDownList(Widget parent) {
        super();
        scroll = new ArtaVerticalScrollPanel(panel);
        setWidget(scroll);

        setRelativeWidget(parent);
        getElement().getStyle().setProperty("maxHeight", Constants.listMaxHeight());
    }

    /**
     * Указывает что максимальная ширина списка равна родительской без границ
     */
    @Override
    public void showUnderParent() {
        if (relativeWidget != null && relativeWidget.isAttached()) {
            int cnt = 0;
            for (MenuItem item : items) {
                if (hasPrefix(item)) {
                    cnt++;
                }
            }
            setHeight(Math.min(32 * cnt, Constants.LIST_MAX_HEIGHT) + "px");
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

    /**
     * Определяет начинается ли текст элемента меню с префикса этого списка.
     * @param item элемент меню
     * @return true - начинается, false - нет
     */
    private boolean hasPrefix(MenuItem item) {
        if (prefix == null || prefix.isEmpty()) {
            return true;
        }
        String str = item.getText();
        if (str == null || prefix.length() > str.length()) {
            return false;
        }
        int len = prefix.length();
        str = str.toLowerCase();
        prefix = prefix.toLowerCase();

        return str.substring(0, len).equals(prefix);
    }

    /**
     * Применяет префикс к списку, показывая только элементы, текст которых начинается с
     * этого префикса
     * @param prefix префикс
     */
    public void applyPrefix(String prefix) {
        panel.clear();
        this.prefix = prefix;
        for (MenuItem item: items) {
            if (hasPrefix(item)) {
                panel.add(item.asWidget());
            }
        }
        showUnderParent();
    }

    /**
     * Убирает примененный префикс, все элементы показываются.
     */
    public void removePrefix() {
        prefix = "";
        panel.clear();
        for (MenuItem item: items) {
            panel.add(item.asWidget());
        }
    }

    /**
     * Нажатие кнопки "влево" не выбирает первый элемент списка (поведение по умолчанию)
     */
    @Override
    protected void keyLeft(Event.NativePreviewEvent event) {
    }

    /**
     * Нажатие кнопки "вправо" не выбирает последний элемент списка (поведение по умолчанию)
     */
    @Override
    protected void keyRight(Event.NativePreviewEvent event) {
    }

    /**
     * При перемещении по списку с помощью клавиатуры элементы без примененного
     * префикса не отображаются и их нельзя выбрать.
     * @param item элемент списка
     */
    @Override
    protected boolean canBeChosen(MenuItem item) {
        return hasPrefix(item);
    }

    @Override
    public void addSelectionHandler(SelectionEventHandler<MenuItem> handler) {
        addHandler(handler, SelectionEvent.TYPE);
    }
}
