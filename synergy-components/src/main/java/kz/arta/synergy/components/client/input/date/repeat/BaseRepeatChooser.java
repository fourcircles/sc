package kz.arta.synergy.components.client.input.date.repeat;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.PopupPanel;

import java.util.*;

/**
 * User: vsl
 * Date: 24.09.14
 * Time: 17:12
 *
 * Базовый класс для компонента повторения.
 */
public class BaseRepeatChooser implements HasValueChangeHandlers<Collection<RepeatDate>>, HasCloseHandlers<BaseRepeatChooser> {
    /**
     * Добавленные даты
     */
    protected Set<RepeatDate> selectedDates;
    EventBus bus;

    /**
     * Корневой попап
     */
    protected PopupPanel popupPanel;

    public BaseRepeatChooser() {
        this.selectedDates = new HashSet<RepeatDate>();
        bus = new SimpleEventBus();

        popupPanel = new PopupPanel(true);
        popupPanel.setStyleName("");
        popupPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                CloseEvent.fire(BaseRepeatChooser.this, BaseRepeatChooser.this);
            }
        });
    }

    public int size() {
        return selectedDates.size();
    }

    public boolean contains(RepeatDate date) {
        return selectedDates.contains(date);
    }

    /**
     * Добавляет все
     * @param dates
     * @param fireEvents
     */
    public void addAll(Collection<RepeatDate> dates, boolean fireEvents) {
        if (dates == null || selectedDates.containsAll(dates)) {
            return;
        }

        Set<RepeatDate> diff = new HashSet<RepeatDate>(dates);
        diff.removeAll(selectedDates);

        selectedDates.addAll(dates);
        if (fireEvents) {
            ValueChangeEvent.fire(this, diff);
        }
    }

    /**
     * Выбрать дату
     * @param date дата
     * @param fireEvents создавать ли события
     */
    public void add(RepeatDate date, boolean fireEvents) {
        if (date == null || selectedDates.contains(date)) {
            return;
        }

        selectedDates.add(date);
        if (fireEvents) {
            ValueChangeEvent.fire(this, Arrays.asList(date));
        }
    }

    /**
     * Убрать дату из выбранных дат
     * @param date дата
     * @param fireEvents создавать ли события
     */
    protected void remove(RepeatDate date, boolean fireEvents) {
        if (date == null || !selectedDates.contains(date)) {
            return;
        }

        selectedDates.remove(date);
        if (fireEvents) {
            ValueChangeEvent.fire(this, Arrays.asList(date));
        }
    }

    /**
     * Добавляет новую дату или убирает уже добавленную.
     * @param date дата
     */
    protected void changeSelection(RepeatDate date) {
        if (selectedDates.contains(date)) {
            remove(date, true);
        } else {
            add(date, true);
        }
    }

    /**
     * @return Выбранные даты
     */
    public Set<RepeatDate> getSelectedDates() {
        return selectedDates;
    }

    /**
     * @param date дата
     * @return выбрана ли дата
     */
    public boolean isSelected(RepeatDate date) {
        return selectedDates.contains(date);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Collection<RepeatDate>> handler) {
        return bus.addHandlerToSource(ValueChangeEvent.getType(), this, handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
    }

    /**
     * Скрыть компонент
     */
    public void hide(boolean fireEvents) {
        popupPanel.hide();
        if (fireEvents) {
            CloseEvent.fire(this, this);
        }
    }

    public void hide() {
        hide(true);
    }

    /**
     * Показать компонент так, чтобы он "заканчивался" на end.
     * Заканчивался в смысле правая граница в ltr, и левая в rtl.
     * @param end x-координата
     * @param y y-координата начала компонента
     */
    public void show(final int end, final int y) {
        popupPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                if (LocaleInfo.getCurrentLocale().isRTL()) {
                    popupPanel.setPopupPosition(end, y);
                } else {
                    popupPanel.setPopupPosition(end - offsetWidth, y);
                }

            }
        });
    }

    public boolean isShowing() {
        return popupPanel.isShowing();
    }

    /**
     * Удалить все добавленные даты
     */
    public void clear() {
        selectedDates.clear();
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<BaseRepeatChooser> handler) {
        return bus.addHandlerToSource(CloseEvent.getType(), this, handler);
    }

    /**
     * @see {@link com.google.gwt.user.client.ui.PopupPanel#addAutoHidePartner(com.google.gwt.dom.client.Element)}
     */
    public void addAutoHidePartner(Element element) {
        popupPanel.addAutoHidePartner(element);
    }

    /**
     * @see {@link com.google.gwt.user.client.ui.PopupPanel#removeAutoHidePartner(com.google.gwt.dom.client.Element)}
     */
    public void removeAutoHidePartner(Element element) {
        popupPanel.removeAutoHidePartner(element);
    }
}
