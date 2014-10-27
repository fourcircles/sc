import com.google.gwt.user.client.Command;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.tree.Tree;
import kz.arta.synergy.components.client.tree.TreeItem;
import kz.arta.synergy.components.client.tree.events.TreeItemContextMenuEvent;
import kz.arta.synergy.components.client.tree.events.TreeSelectionEvent;

public class Sample {
    public static void main(String[] args) {
        Tree tree = new Tree();

        // заполнение дерева
        fillTree(tree);

        // хэндлер на выбор узла дерева
        tree.addTreeSelectionEvent(new TreeSelectionEvent.Handler() {
            @Override
            public void onTreeSelection(TreeSelectionEvent event) {
                Panel panel = (Panel) event.getTreeItem().getUserObject();

                // показать панель
                showPanel(panel);
            }
        });

        final ContextMenu menu = new ContextMenu();

        // добавление хэдлера на контекстное меню, которое заполняется
        // в зависимости от типа узла
        setTreeItemHandler(tree, new TreeItemContextMenuEvent.Handler() {
            @Override
            public void onTreeContextMenu(TreeItemContextMenuEvent event) {
                // event.getEvent() - событие вызова контекстного меню
                event.getEvent().preventDefault();
                event.getEvent().stopPropagation();

                menu.clear();
                final TreeItem item = event.getItem();
                if (item.isSelected()) {
                    menu.addItem(Messages.i18n().tr("Снять выделение"), new Command() {
                        @Override
                        public void execute() {
                            item.setSelected(false);
                        }
                    });
                } else {
                    menu.addItem(Messages.i18n().tr("Выделить"), new Command() {
                        @Override
                        public void execute() {
                            item.setSelected(true);
                        }
                    });
                }

                if (item.hasItems()) {
                    menu.addSeparator();
                    if (item.isOpen()) {
                        menu.addItem(Messages.i18n().tr("Закрыть"), new Command() {
                            @Override
                            public void execute() {
                                item.setOpen(false);
                            }
                        });
                    } else {
                        menu.addItem(Messages.i18n().tr("Открыть"), new Command() {
                            @Override
                            public void execute() {
                                item.setOpen(true);
                            }
                        });
                    }
                }
                // в событии содержаться абсолютные координаты клика мыши
                menu.show(event.getX(), event.getY());
            }
        });

    }

    /**
     * Заполняет дерево
     */
    private void fillTree(Tree tree) {
        TreeItem basicComponents = tree.addItem(Messages.i18n().tr("Базовые компоненты"));

        TreeItem buttons = tree.addItem(basicComponents, Messages.i18n().tr("Кнопки"));

        TreeItem simpleButtons = tree.addItem(buttons, Messages.i18n().tr("Простые кнопки"));
        simpleButtons.setUserObject(getSimpleButtonsPanel());

        TreeItem iconButtons = tree.addItem(buttons, Messages.i18n().tr("Кнопка с иконкой"));
        iconButtons.setUserObject(getIconButtonsPanel());

        TreeItem colorButtons = tree.addItem(buttons, Messages.i18n().tr("Цветные кнопки"));
        colorButtons.setUserObject(getColorButtonsPanel());

        TreeItem groupButtons = tree.addItem(buttons, Messages.i18n().tr("Групповые кнопки"));
        groupButtons.setUserObject(getGroupButtonsPanel());
    }

    /**
     * Добавляет хэндлер ко всем корневым узлам дерева
     */
    private void setTreeItemHandler(Tree tree, TreeItemContextMenuEvent.Handler handler) {
        for (TreeItem item : tree.getItems()) {
            setTreeItemHandler(item, handler);
        }
    }

    /**
     * Добавляет хэндлер ко всем вложенным узлам узла.
     */
    private void setTreeItemHandler(TreeItem item, TreeItemContextMenuEvent.Handler handler) {
        item.addTreeContextMenuHandler(handler);
        if (!item.hasItems()) {
            return;
        }
        for (TreeItem child : item.getItems()) {
            setTreeItemHandler(child, handler);
        }
    }
}