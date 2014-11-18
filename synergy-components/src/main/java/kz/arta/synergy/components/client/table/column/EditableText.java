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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.table.events.CellEditEvent;

/**
 * User: user
 * Date: 18.11.14
 * Time: 16:25
 * Изменяемый текст. При изменении текст заменяется на элемент input.
 * Создает соответствующие события {@link kz.arta.synergy.components.client.table.events.CellEditEvent}
 */
public class EditableText<T> extends Composite {
    /**
     * Корневая панель
     */
    protected FocusPanel root;

    /**
     * Текст
     */
    protected InlineLabel label;

    /**
     * Ввод текста
     */
    protected TextBox input;

    /**
     * Производится ли изменение
     */
    protected boolean isEditing;

    /**
     * Объект
     */
    protected T object;

    /**
     * Текущий текст
     */
    protected String text;

    /**
     * Старый текст, который был в ячейке до начала изменения
     */
    protected String oldText;

    protected EventBus bus;

    protected AbstractEditableColumn<T> column;

    public EditableText(AbstractEditableColumn<T> column, T object, EventBus bus) {
        root = new FocusPanel();
        initWidget(root);

        this.bus = bus;
        this.column = column;

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            root.getElement().getStyle().setPaddingRight(14, Style.Unit.PX);
        } else {
            root.getElement().getStyle().setPaddingLeft(14, Style.Unit.PX);
        }

        this.object = object;

        isEditing = false;
        text = column.getValue(object);

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
        } else if (type == Event.ONKEYDOWN &&
                (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_F2)) {
            edit();
        }
        super.onBrowserEvent(event);
    }

    /**
     * Обработка событий при изменении
     * @param event событие
     */
    protected void editEvent(Event event) {

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
                default:
            }
            event.stopPropagation();
        }
    }

    /**
     * Создает элемент ввода текста
     */
    protected TextBox createInput() {
        TextBox inputBox = new TextBox();
        inputBox.setStyleName(SynergyComponents.getResources().cssComponents().mainText());
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
    public void edit() {
        oldText = text;
        isEditing = true;

        if (input == null) {
            input = createInput();
        }
        input.setValue(column.getValue(object));
        root.setWidget(input);
        input.setSelectionRange(0, input.getValue().length());
        input.getElement().focus();

        getElement().getStyle().clearPaddingLeft();
        getElement().getStyle().clearPaddingRight();

        bus.fireEvent(new CellEditEvent<T>(object, column, CellEditEvent.EditType.EDIT_START));
    }

    /**
     * Отмена изменения текста
     */
    protected void cancel() {
        text = oldText;
        unEdit();
        label.setText(text);

        bus.fireEvent(new CellEditEvent<T>(object, column, CellEditEvent.EditType.CANCEL));
        root.setFocus(true);
    }

    /**
     * Изменяет текст в ячейке
     */
    protected void commit(boolean jumpForward) {
        text = input.getText();
        unEdit();
        label.setText(text);

        column.setValue(object, text);

        bus.fireEvent(new CellEditEvent<T>(object, column, CellEditEvent.EditType.COMMIT, jumpForward));
        root.setFocus(true);
    }

    protected void commit() {
        commit(false);
    }

    /**
     * Заменяет элемент ввода на текст
     */
    protected void unEdit() {
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
        label.setText(column.getValue(object));
    }
}
