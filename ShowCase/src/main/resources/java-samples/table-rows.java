import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.ListDataProvider;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.menu.ContextMenu;
import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.table.Pager;
import kz.arta.synergy.components.client.table.Table;
import kz.arta.synergy.components.client.table.User;
import kz.arta.synergy.components.client.table.column.ArtaEditableTextColumn;
import kz.arta.synergy.components.client.table.column.ArtaTextColumn;
import kz.arta.synergy.components.client.table.events.TableHeaderMenuEvent;
import kz.arta.synergy.components.client.table.events.TableMenuEvent;
import kz.arta.synergy.components.client.table.events.TableRowMenuEvent;
import kz.arta.synergy.components.client.table.events.TableSortEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Sample {
    public static void main(String[] args) {
        // 29 - размер страницы
        final Table<User> table = new Table<User>(29);
        // шапка
        table.enableHat(true);
        // название таблицы
        table.getHat().setName(Messages.i18n().tr("Таблица"));
        // есть ли кнопка "добавить"
        table.getHat().enableAddButton(true);
        // показывать ли пагинатор всегда
        // если false, то он будет показан только тогда, когда есть несколько страниц
        table.getHat().enablePagerAlways(true);
        // показывать ли пагинатор
        table.getHat().enablePager(true);
        //переносить ли на следующую строку (и увеличивать высоту строки) длинные значения в ячейках
        table.setMultiLine(false);

        // выбор только строк
        table.getCore().setOnlyRows(true);

        // для задания высоты надо использовать setHeight()
        // или как угодно, но после изменения надо вызвать метод heightUpdated()
        table.setHeight("600px");

        // столбец id
        final ArtaTextColumn<User> idColumn = new ArtaTextColumn<User>("#") {
            @Override
            public String getValue(User object) {
                return Integer.toString(object.getKey());
            }
        };
        // столбец можно сортировать
        idColumn.setSortable(true);
        table.addColumn(idColumn);

        final ArtaEditableTextColumn<User> firstNameColumn = new ArtaEditableTextColumn<User>(Messages.i18n().tr("Имя")) {
            @Override
            public String getValue(User value) {
                return value.getFirstName();
            }

            @Override
            public void setValue(User value, String text) {
                value.setFirstName(text);
            }
        };
        firstNameColumn.setSortable(true);
        table.addColumn(firstNameColumn);

        final ArtaEditableTextColumn<User> lastNameColumn = new ArtaEditableTextColumn<User>(Messages.i18n().tr("Фамилия")) {
            @Override
            public String getValue(User value) {
                return value.getLastName();
            }

            @Override
            public void setValue(User value, String text) {
                value.setLastName(text);
            }
        };
        lastNameColumn.setSortable(true);
        table.addColumn(lastNameColumn);

        // предоставляет данные для таблицы
        final ListDataProvider<User> provider = new ListDataProvider<User>();
        // добавления таблицы как одного из отображений данных провайдера
        provider.addDataDisplay(table.getCore());

        // добавление данных
        final List<User> list = provider.getList();
        for (int i = 0; i < 190; i++) {
            list.add(new User("jon" + i, "jones" + i, "" + (85281 + i)));
        }
        // отобразить изменения в провайдере в таблице
        provider.flush();

        setUpSorting(table, provider.getList());

        // создание внешнего пагинатора
        final Pager simplePager = new Pager(false);
        simplePager.setDisplay(table.getCore());

        setUpRowMenu(table, provider);
        setUpHeaderMenu(table);
    }

    private void setUpSorting(Table<User> table, List<User> list) {
        TableSortEvent.ListHandler<User> listHandler = new TableSortEvent.ListHandler<User>(list);
        // компаратор для столбца "id"
        listHandler.setComparator(idColumn, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return user1.getKey() > user2.getKey() ? 1 : user1.getKey() < user2.getKey() ? -1 : 0;
            }
        });
        // компаратор для имени
        listHandler.setComparator(firstNameColumn, new Comparator<User>() {
            @Override
            public int compare(User user, User user2) {
                //noinspection NonJREEmulationClassesInClientCode
                return user.getFirstName().compareTo(user2.getFirstName());
            }
        });
        // компаратор для фамилии
        listHandler.setComparator(lastNameColumn, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                //noinspection NonJREEmulationClassesInClientCode
                return user1.getLastName().compareTo(user2.getLastName());
            }
        });
        table.getCore().addSortHandler(listHandler);
    }

    /**
     * Добавляет контекстное меню для рядов.
     * Через него можно удалить ряд.
     */
    private void setUpRowMenu(Table<User> table, ListDataProvider<User> provider) {
        // контекстное меню
        final ContextMenu tableMenu = new ContextMenu();

        // содержит только один пункт
        tableMenu.add(new MenuItem<Command>(new Command() {
            @Override
            public void execute() {
                Set<User> selected = table.getCore().getSelectionModel().getSelectedObjects();
                list.removeAll(selected);
                provider.flush();
            }
        }, Messages.i18n().tr("Удалить")));

        // вызов контекстного меню
        table.getCore().addContextMenuHandler(new TableMenuEvent.Handler() {
            @Override
            public void onTableMenu(TableMenuEvent event) {
                tableMenu.show(event.getX(), event.getY());
            }
        });
    }

    /**
     * Добавляет контекстное меню для заголовков.
     * Через него можно отсортировать столбец.
     */
    private void setUpHeaderMenu(Table<User> table) {
        final ContextMenu headerMenu = new ContextMenu();
        table.addHeaderMenuHandler(new TableHeaderMenuEvent.Handler<User>() {
            @Override
            public void onTableHeaderMenu(final TableHeaderMenuEvent<User> event) {
                headerMenu.clear();
                headerMenu.addItem(Messages.i18n().tr("Отсортировать"), new Command() {
                    @Override
                    public void execute() {
                        // сортируем по столбцу, для которого было вызвано меню
                        table.getCore().sort(event.getColumn());
                    }
                });
                headerMenu.show(event.getX(), event.getY());
            }
        });
    }
}