package kz.arta.synergy.components.client.table.column;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.table.events.CellEditEvent;

/**
 * User: vsl
 * Date: 02.09.14
 * Time: 15:12
 *
 * Столбец, в котором можно изменять значения
 */
public abstract class ArtaEditableTextColumn<T> extends AbstractArtaColumn<T> {

    protected ArtaEditableTextColumn(String headerText) {
        super(headerText);
    }

    public abstract String getValue(T object);

    /**
     * Изменяет значение у объекта на заданное.
     * Возможно это можно как-то обобщить в будущем.
     * @param object объект
     * @param value значение
     */
    public abstract void setValue(T object, String value);

    @Override
    public EditableText createWidget(T object, EventBus bus) {
        return new EditableText(object, bus);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateWidget(Widget widget, T object) {
        EditableText textWidget = (EditableText) widget;
        textWidget.setObject(object);
    }

    @Override
    public int getMinWidth() {
        return 60;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    /**
     * Изменяемый текст. При изменении текст заменяется на элемент input.
     * Создает соответствующие события {@link kz.arta.synergy.components.client.table.events.CellEditEvent}
     */
    public class EditableText extends Composite {
        /**
         * Корневая панель
         */
        private FocusPanel root;

        /**
         * Текст
         */
        private InlineLabel label;

        /**
         * Ввод текста
         */
        private TextBox input;

        /**
         * Производится ли изменение
         */
        private boolean isEditing;

        /**
         * Объект
         */
        private T object;

        /**
         * Текущий текст
         */
        private String text;

        /**
         * Старый текст, который был в ячейке до начала изменения
         */
        private String oldText;

        private EventBus bus;

        public EditableText(T object, EventBus bus) {
            root = new FocusPanel();
            initWidget(root);

            this.bus = bus;

            if (LocaleInfo.getCurrentLocale().isRTL()) {
                root.getElement().getStyle().setPaddingRight(14, Style.Unit.PX);
            } else {
                root.getElement().getStyle().setPaddingLeft(14, Style.Unit.PX);
            }

            this.object = object;

            isEditing = false;
            text = getValue(object);

            HasDirection.Direction textDirection;
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                textDirection = HasDirection.Direction.RTL;
            } else {
                textDirection = HasDirection.Direction.LTR;
            }
            label = new InlineLabel(text, textDirection);

            root.setWidget(label);

            sinkEvents(Event.ONKEYDOWN);
            sinkEvents(Event.ONCLICK);
            sinkEvents(Event.ONBLUR);
            sinkEvents(Event.ONFOCUS);
        }

        @Override
        public void onBrowserEvent(Event event) {
            int type = event.getTypeInt();
            int keyCode = event.getKeyCode();
            if (isEditing) {
                editEvent(event);
            } else {
                switch (type) {
                    case Event.ONKEYDOWN :
                        if (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_F2) {
                            edit();
                        }
                        break;
                }
            }
            super.onBrowserEvent(event);
        }

        /**
         * Обработка событий при изменении
         * @param event событие
         */
        private void editEvent(Event event) {

            String type = event.getType();
            int keyCode = event.getKeyCode();

            if (BrowserEvents.KEYDOWN.equals(type)) {
                switch(keyCode) {
                    case KeyCodes.KEY_ENTER :
                        commit();
                        break;
                    case KeyCodes.KEY_ESCAPE :
                        commit();
                        break;
                    case KeyCodes.KEY_TAB :
                        commit(true);
                        break;
                }
                event.stopPropagation();
            }
        }

        /**
         * Создает элемент ввода текста
         */
        private TextBox createInput() {
            TextBox inputBox = new TextBox();
            inputBox.setStyleName(SynergyComponents.resources.cssComponents().mainText());
            inputBox.setDirectionEstimator(false);

            if (LocaleInfo.getCurrentLocale().isRTL()) {
                inputBox.getElement().getStyle().setPaddingRight(14, Style.Unit.PX);
                inputBox.setDirection(HasDirection.Direction.RTL);
            } else {
                inputBox.getElement().getStyle().setPaddingLeft(14, Style.Unit.PX);
                inputBox.setDirection(HasDirection.Direction.LTR);
            }
            inputBox.addBlurHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    cancel();
                }
            });
            return inputBox;
        }

        /**
         * Заменяет текст на элемент ввода.
         */
        private void edit() {
            oldText = text;
            isEditing = true;

            if (input == null) {
                input = createInput();
            }
            input.setValue(getValue(object));
            root.setWidget(input);
            input.setSelectionRange(0, text.length());
            input.getElement().focus();

            getElement().getStyle().clearPaddingLeft();
            getElement().getStyle().clearPaddingRight();

            bus.fireEvent(new CellEditEvent<T>(object, ArtaEditableTextColumn.this, CellEditEvent.EditType.EDIT_START));
        }

        /**
         * Отмена изменения текста
         */
        private void cancel() {
            text = oldText;
            unEdit();
            label.setText(text);

            bus.fireEvent(new CellEditEvent<T>(object, ArtaEditableTextColumn.this, CellEditEvent.EditType.CANCEL));
            root.setFocus(true);
        }

        /**
         * Изменяет текст в ячейке
         */
        private void commit(boolean jumpForward) {
            text = input.getText();
            unEdit();
            label.setText(text);

            setValue(object, text);

            bus.fireEvent(new CellEditEvent<T>(object, ArtaEditableTextColumn.this, CellEditEvent.EditType.COMMIT, jumpForward));
            root.setFocus(true);
        }

        private void commit() {
            commit(false);
        }

        /**
         * Заменяет элемент ввода на текст
         */
        private void unEdit() {
            isEditing = false;
            root.setWidget(label);
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                getElement().getStyle().setPaddingRight(14, Style.Unit.PX);
            } else {
                getElement().getStyle().setPaddingLeft(14, Style.Unit.PX);
            }
            root.setFocus(true);
        }

        /**
         * Изменяет объект.
         */
        public void setObject(T object) {
            if (isEditing) {
                cancel();
            }
            this.object = object;
            label.setText(getValue(object));
        }

    }


}
