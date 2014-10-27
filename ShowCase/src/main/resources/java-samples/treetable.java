import com.google.gwt.dom.client.Style;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RowCountChangeEvent;
import kz.arta.sc3.showcase.client.resources.Messages;
import kz.arta.synergy.components.client.table.Table;
import kz.arta.synergy.components.client.table.TreeTableProvider;
import kz.arta.synergy.components.client.table.UserTree;
import kz.arta.synergy.components.client.table.column.ArtaTextColumn;
import kz.arta.synergy.components.client.table.column.TreeColumn;

public class Sample {
    public static void main(String[] args) {

        // при создании таблицы можно указать как предоставлять ключ для объекта
        final Table<UserTree> table = new Table<UserTree>(10, new ProvidesKey<UserTree>() {
            @Override
            public Object getKey(UserTree item) {
                return item.getKey();
            }
        });
        table.getCore().setOnlyRows(true);
        table.enableHat(true);
        table.getHat().setName(Messages.i18n().tr("Дерево-таблица"));
        table.getHat().enableAddButton(true);

        // позиция таблицы может быть задана относительно родителя
        Style tableStyle = table.getElement().getStyle();
        tableStyle.setPosition(Style.Position.ABSOLUTE);
        tableStyle.setBottom(20, Style.Unit.PX);
        tableStyle.setTop(20, Style.Unit.PX);
        tableStyle.setLeft(40, Style.Unit.PX);
        tableStyle.setRight(40, Style.Unit.PX);

        // в дереве-таблице размер страницы всегда равен количеству добавленных объектов
        table.getCore().addRowCountChangeHandler(new RowCountChangeEvent.Handler() {
            @Override
            public void onRowCountChange(RowCountChangeEvent event) {
                table.getCore().setPageSize(event.getNewRowCount());
            }
        });

        // столбец id
        ArtaTextColumn<UserTree> idColumn = new ArtaTextColumn<UserTree>("id") {
            @Override
            public String getValue(UserTree object) {
                return "" + object.getKey();
            }
        };
        // в дереве-таблице сортировка не имеет смысла (пока)
        // явно указываем это, хотя по-умолчание столбцы не сортируются
        idColumn.setSortable(false);
        table.addColumn(idColumn);

        // специальный столбце для отображения объектов для дерева-таблицы
        // объекты должны implement интерфейс TreeTableItem
        TreeColumn<UserTree> treeColumn = new TreeColumn<UserTree>("Название человека") {
            @Override
            public String getText(UserTree object) {
                return object.getFirstName();
            }
        };
        table.addColumn(treeColumn);

        ArtaTextColumn<UserTree> lastNameColumn = new ArtaTextColumn<UserTree>(Messages.i18n().tr("Фамилия")) {
            @Override
            public String getValue(UserTree object) {
                return object.getLastName();
            }
        };
        table.addColumn(lastNameColumn);

        ArtaTextColumn<UserTree> addressColumn = new ArtaTextColumn<UserTree>(Messages.i18n().tr("Адрес")) {
            @Override
            public String getValue(UserTree object) {
                return object.getAddress();
            }
        };
        table.addColumn(addressColumn);

        // явное задание ширины некоторых столбцов, остальные -- растягиваются
        // надо заметить, что при изменении размера столбцов возможно фиксирование ранее растягиваемых столбцов
        table.getCore().setColumnWidth(idColumn, 65);
        table.getCore().setColumnWidth(lastNameColumn, 150);
        table.getCore().setColumnWidth(addressColumn, 150);

        // для дерева-таблицы надо использовать специальный провайдер
        TreeTableProvider<UserTree> provider = new TreeTableProvider<UserTree>();
        provider.addDataDisplay(table.getCore());

        // наполнение данными
        for (int i = 0; i < 15; i++) {
            String name = "john";
            String lastName = "jones";
            String address = "Orinbor";

            UserTree user = new UserTree(null, name + i, lastName + i, address + " " + (i + 1));
            // user2 находится внутри user
            UserTree user2 = new UserTree(user, name + (i + 1), lastName + (i + 1), address + " " + (i + 2));
            // два объекта внутри user2
            new UserTree(user2, name + (i + 2), lastName + (i + 2), address + " " + (i + 3));
            new UserTree(user2, name + (i + 3), lastName + (i + 3), address + " " + (i + 4));

            // добавление нового объекта вместе со всеми объектами "внутри" него
            // при попытке добавить некорневой объект, будет добавлен его корневой объект
            provider.addItem(user);
        }
        provider.flush();
    }
}

