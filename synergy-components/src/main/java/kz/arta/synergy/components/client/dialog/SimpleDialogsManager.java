package kz.arta.synergy.components.client.dialog;

/**
 * User: vsl
 * Date: 02.10.14
 * Time: 16:08
 *
 * Связывает события диалога и панель задач
 */
public class SimpleDialogsManager {
//    /**
//     * Панель задач
//     */
//    private TaskBar taskBar;
//
//    /**
//     * Соответствие между диалогами и элементами панели задач
//     */
//    private Map<DialogSimple, TaskBarItemUI> dialogs;
//
//    private EventBus bus;
//
//    public SimpleDialogsManager(final TaskBar taskBar) {
//        this.taskBar = taskBar;
//
//        bus = new SimpleEventBus();
//        dialogs = new HashMap<DialogSimple, TaskBarItemUI>();
//
//        bus.addHandler(TaskBarEvent.getType(), new TaskBarEvent.AbstractHandler() {
//            @Override
//            public void onClose(TaskBarEvent event) {
//                DialogSimple dialog = event.getDialog();
//                close(dialog);
//            }
//
//            @Override
//            public void onTextChange(TaskBarEvent event) {
//                dialogs.get(event.getDialog()).setText(event.getDialog().getText());
//                taskBar.updateWidths();
//            }
//
//            @Override
//            public void onShow(TaskBarEvent event) {
//                dialogs.get(event.getDialog()).addStyleName(SynergyComponents.resources.cssComponents().open());
//            }
//
//            @Override
//            public void onCollapse(TaskBarEvent event) {
//                dialogs.get(event.getDialog()).removeStyleName(SynergyComponents.resources.cssComponents().open());
//            }
//        });
//    }
//
//    /**
//     * Инициализирует диалог.
//     * @param dialog диалог
//     */
//    public void initDialog(final DialogSimple dialog) {
//        if (!dialogs.containsKey(dialog)) {
//            final TaskBarItemUI item = taskBar.addItem(dialog.getText());
//            item.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent event) {
//                    dialog.center();
//                    dialog.show();
//                }
//            });
//            dialogs.put(dialog, item);
//            dialog.setBus(bus);
//        }
//    }
//
//    /**
//     * Действия при закрытии диалога
//     * @param dialog диалог
//     */
//    public void close(DialogSimple dialog) {
//        if (dialogs.containsKey(dialog)) {
//            taskBar.removeItem(dialogs.get(dialog));
//            dialogs.remove(dialog);
//        }
//    }
}
