package kz.arta.synergy.components.client.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.synergy.components.client.dialog.events.DialogEvent;
import kz.arta.synergy.components.client.taskbar.TaskBar;
import kz.arta.synergy.components.client.taskbar.TaskBarItem;

import java.util.HashMap;
import java.util.Map;

/**
 * User: vsl
 * Date: 02.10.14
 * Time: 16:08
 *
 * Связывает события диалога и панель задач
 */
public class SimpleDialogsManager {
    /**
     * Панель задач
     */
    private TaskBar taskBar;

    /**
     * Соответствие между диалогами и элементами панели задач
     */
    private Map<DialogSimple, TaskBarItem> dialogs;

    private EventBus bus;

    public SimpleDialogsManager(final TaskBar taskBar) {
        this.taskBar = taskBar;

        bus = new SimpleEventBus();
        dialogs = new HashMap<DialogSimple, TaskBarItem>();

        bus.addHandler(DialogEvent.getType(), new DialogEvent.AbstractHandler() {
            @Override
            public void onClose(DialogEvent event) {
                close(event.getDialog());
            }

            @Override
            public void onTextChange(DialogEvent event) {
                dialogs.get(event.getDialog()).setText(event.getDialog().getText());
                taskBar.updateWidths();
            }
        });
    }

    /**
     * Инициализирует диалог.
     * @param dialog диалог
     */
    public void initDialog(final DialogSimple dialog) {
        if (!dialogs.containsKey(dialog)) {
            TaskBarItem item = taskBar.addItem(dialog.getText());
            item.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    dialog.center();
                    dialog.show();
                }
            });
            dialogs.put(dialog, item);
            dialog.setBus(bus);
        }
    }

    /**
     * Действия при закрытии диалога
     * @param dialog диалог
     */
    public void close(DialogSimple dialog) {
        if (dialogs.containsKey(dialog)) {
            taskBar.removeItem(dialogs.get(dialog));
            dialogs.remove(dialog);
        }
    }
}
