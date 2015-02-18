package kz.arta.synergy.components.client.table.column;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.checkbox.ArtaCheckBox;

/**
 * User: vsl
 * Date: 27.11.14
 * Time: 16:22
 *
 * Столбец с чекбоксом
 */
public abstract class CheckBoxColumn<T> extends AbstractArtaColumn<T> {

    public static final int WIDTH = 36;

    protected CheckBoxColumn(String headerText) {
        super(headerText);
        locked = true;
    }

    @Override
    public Widget createWidget(T object, EventBus bus) {
        TableCheckBox box = new TableCheckBox(object);
        box.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        box.getElement().getStyle().setProperty("marginLeft", "auto");
        box.getElement().getStyle().setProperty("marginRight", "auto");

        return box;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateWidget(Widget widget, T object) {
        super.updateWidget(widget, object);
        TableCheckBox box = (TableCheckBox) widget;
        box.setObject(object);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public int getMinWidth() {
        return WIDTH;
    }

    /**
     * Возвращает значение для объекта
     */
    public abstract boolean getValue(T object);

    /**
     * Этот метод используется для изменения значения в объекте, когда на
     * чекбокс кликает пользователь
     * @param object объект
     * @param newValue новое значение
     */
    public abstract void setValue(T object, boolean newValue);

    /**
     * Чекбокс с объектом
     */
    private class TableCheckBox extends ArtaCheckBox {
        /**
         * Текущий объект для чекбокса
         */
        private T object;

        private TableCheckBox(T object) {
            super();
            this.object = object;
            addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    CheckBoxColumn.this.setValue(TableCheckBox.this.object, getValue());
                }
            });
            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    event.stopPropagation();
                }
            }, ClickEvent.getType());
        }

        public T getObject() {
            return object;
        }

        /**
         * Изменяет объект, который отображается в виджете
         * @param object объект
         */
        public void setObject(T object) {
            this.object = object;
            update();
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            update();
        }

        /**
         * Обновляет вид виджета в соответствии с объектом
         */
        private void update() {
            setValue(CheckBoxColumn.this.getValue(object), false);
        }
    }
}
