package kz.arta.synergy.components.client.table;

import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.table.column.ArtaColumn;
import kz.arta.synergy.components.client.table.events.CellEditEvent;
import kz.arta.synergy.components.client.table.events.StartEditEvent;
import kz.arta.synergy.components.client.table.events.TableMenuEvent;
import kz.arta.synergy.components.client.table.events.TableSortEvent;
import kz.arta.synergy.components.client.util.Utils;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: vsl
 * Date: 18.09.14
 * Time: 15:23
 *
 * Таблица
 * @see {@link Table} заголовки, шапка
 */
public class TableCore<T> extends Composite implements HasData<T> {
    static final int TAB_INDEX = 1;

    /**
     * Таблица
     */
    FlexTable table;

    /**
     * Предоставляет ключи
     */
    private ProvidesKey<T> keyProvider;

    /**
     * Начало отображаемого множества объектов
     */
    private int start;

    /**
     * Количество объектов на странице
     */
    private int pageSize;

    /**
     * Объекты добавленные в таблицу
     */
    List<T> objects;

    /**
     * Список столбцов. Порядок соответствует порядку отображения.
     */
    private List<ArtaColumn<T>> columns;

    /**
     * Модель выбора объекта
     */
    private TableSelectionModel<T> selectionModel;

    /**
     * Разрешено ли выделение нескольких объектов в таблице.
     */
    private boolean multiSelectionAllowed = false;

    private EventBus bus;

    /**
     * Режим выбора в таблице.
     * true - выбираются ряды, false - ячейки
     */
    private boolean onlyRows;

    /**
     * Столбец, по которому отсортирована таблица
     */
    private ArtaColumn<T> sortedColumn;

    /**
     * Ширина заголовков
     *
     * //todo не вижу смысла оставлять логическую ширину столбцов, надо правильно убрать
     */
    Map<ArtaColumn<T>, Integer> widths = new HashMap<ArtaColumn<T>, Integer>();

    /**
     * Точное ли количество объектов
     */
    private boolean isRowCountExact;

    /**
     * Индексы показывают какая ячейка была выбрана последней.
     * Эти индексы будут использоваться, как начало выбора при shift+click.
     */
    private int lastSelectedRow = 0;
    private int lastSelectedColumn = 0;

    public TableCore(int pageSize, ProvidesKey<T> keyProvider, final EventBus bus) {
        this.bus = bus;

        this.keyProvider = keyProvider;
        this.pageSize = pageSize;
        selectionModel = new TableSelectionModel<T>(bus, keyProvider);

        table = new FlexTable();
        table.sinkEvents(Event.ONCONTEXTMENU);

        TableElement tableElement = TableElement.as(table.getElement());
        tableElement.createTHead().insertRow(0);
        
        initWidget(table);

        table.addStyleName(SynergyComponents.getResources().cssComponents().table());
        for (int i = 0; i <= pageSize; i++) {
            table.insertRow(0);
        }

        columns = new ArrayList<ArtaColumn<T>>();
        objects = new ArrayList<T>(pageSize);

        table.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                HTMLTable.Cell cell = table.getCellForEvent(event);
                int rowIndex = cell.getRowIndex();
                int cellIndex = cell.getCellIndex();

                if (!event.isControlKeyDown() || !multiSelectionAllowed) {
                    selectionModel.clear(false);
                }

                if (event.isShiftKeyDown() && multiSelectionAllowed) {
                    if (onlyRows) {
                        for (int r = Math.min(rowIndex, lastSelectedRow); r <= Math.max(rowIndex, lastSelectedRow); r++) {
                            T object = objects.get(start + r);
                            selectionModel.setSelected(object, null, true, false);
                        }
                    } else {
                        for (int r = Math.min(rowIndex, lastSelectedRow); r <= Math.max(rowIndex, lastSelectedRow); r++) {
                            for (int c = Math.min(cellIndex, lastSelectedColumn); c <= Math.max(cellIndex, lastSelectedColumn); c++) {
                                T object = objects.get(start + r);
                                ArtaColumn<T> column = columns.get(c);
                                selectionModel.setSelected(object, column, true, false);
                            }
                        }
                    }
                } else {
                    if (!event.isControlKeyDown() || !multiSelectionAllowed) {
                        lastSelectedRow = rowIndex;
                        lastSelectedColumn = cellIndex;
                    }

                    T object = objects.get(start + rowIndex);
                    ArtaColumn<T> column = onlyRows ? null : columns.get(cellIndex);
                    boolean select = event.isControlKeyDown() ? !selectionModel.isSelected(object, column) : true;
                    selectionModel.setSelected(object, column, select, false);
                }

                updateSelection();
            }
        });

        bus.addHandler(CellEditEvent.TYPE, new CellEditEvent.Handler<T>() {
            @Override
            public void onCommit(CellEditEvent<T> event) {
                edit(event, false);
            }

            @Override
            public void onCancel(CellEditEvent<T> event) {
                edit(event, false);
            }

            @Override
            public void onEdit(CellEditEvent<T> event) {
                edit(event, true);
            }
        });

        bus.addHandler(SelectionChangeEvent.getType(), new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                updateSelection();
            }
        });

        table.addDomHandler(new ContextMenuHandler() {
            @Override
            public void onContextMenu(ContextMenuEvent event) {
                contextMenuEvent(event);
            }
        }, ContextMenuEvent.getType());

        sinkEvents(Event.ONCLICK);
        sinkEvents(Event.ONKEYDOWN);
        sinkEvents(Event.ONKEYUP);
        sinkEvents(Event.ONBLUR);
        sinkEvents(Event.ONFOCUS);
    }

    /**
     * Фокус таблицы. Выбирается первая строка или ячейка.
     */
    public void focus() {
        if (!objects.isEmpty()) {
            selectionModel.clear(false);
            if (onlyRows) {
                selectionModel.setSelected(objects.get(0), true, true);
                table.getRowFormatter().getElement(0).focus();
            } else if (!columns.isEmpty()) {
                selectionModel.setSelected(objects.get(0), columns.get(0), true, true);
                table.getFlexCellFormatter().getElement(0, 0).focus();
            }
        }
    }

    /**
     * Обработка вызова контекстного меню
     * @param event событие
     */
    private void contextMenuEvent(ContextMenuEvent event) {
        event.preventDefault();

        Element td = getCellForEvent(Event.as(event.getNativeEvent()));

        int rowIndex = TableRowElement.as(td.getParentElement()).getRowIndex();
        int columnIndex = TableCellElement.as(td).getCellIndex();

        T object = objects.get(start + rowIndex);
        ArtaColumn<T> column = onlyRows ? null : columns.get(columnIndex);

        // если правый клик не по выбранному элементу, то выделение обнуляется
        if (!selectionModel.isSelected(object, column)) {
            selectionModel.clear(false);
            selectionModel.setSelected(object, column, true, true);
        }
        bus.fireEventFromSource(new TableMenuEvent(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY()), this);
    }

    private Element getCellForEvent(Event event) {
        com.google.gwt.user.client.Element td = DOM.eventGetTarget(event);
        for (; td != null; td = DOM.getParent(td)) {
            //noinspection NonJREEmulationClassesInClientCode
            if ("td".equalsIgnoreCase(td.getPropertyString("tagName"))) {
                return td;
            }
        }
        return null;
    }

    /**
     * Вызывается при начале или завершении изменения ячейки.
     * Правильно рисует границы ячейки, которую изменяют.
     * @param event событие
     * @param start true - начало изменения, false - завершение изменения
     */
    public void edit(CellEditEvent<T> event, boolean start) {
        int row = objects.indexOf(event.getObject());
        int column = columns.indexOf(event.getColumn());
        Element td = table.getFlexCellFormatter().getElement(row, column);
        Element tdUnder = null;
        if (row + 1 < Math.min(pageSize, objects.size())) {
            tdUnder = table.getFlexCellFormatter().getElement(row + 1, column);
        }
        if (start) {
            table.getRowFormatter().getElement(row).addClassName(SynergyComponents.getResources().cssComponents().edit());
            td.addClassName(SynergyComponents.getResources().cssComponents().edit());
            if (tdUnder != null) {
                tdUnder.addClassName(SynergyComponents.getResources().cssComponents().underEdit());
            }
        } else {
            table.getRowFormatter().getElement(row).removeClassName(SynergyComponents.getResources().cssComponents().edit());
            td.removeClassName(SynergyComponents.getResources().cssComponents().edit());
            if (tdUnder != null) {
                tdUnder.removeClassName(SynergyComponents.getResources().cssComponents().underEdit());
            }
        }

        if (row == 0) {
            if (start) {
                td.getStyle().setHeight(25, Style.Unit.PX);
            } else {
                td.getStyle().clearHeight();
            }
        }

        if (event.jumpForward()) {
            cellTab(row, column, false);
        }
    }

    /**
     * Обработка событий навигационных клавиш и таба
     */
    @Override
    public void onBrowserEvent(com.google.gwt.user.client.Event event) {
        super.onBrowserEvent(event);

        if (event.getTypeInt() == Event.ONKEYDOWN || event.getTypeInt() == Event.ONFOCUS) {
            Element focused = Utils.impl().getFocusedElement();
            if (TableRowElement.is(focused)) {
                rowEvent(event, TableRowElement.as(focused));
            }
            if (TableCellElement.is(focused)) {
                cellEvent(event, TableCellElement.as(focused));
            }
        }
    }

    /**
     * Событие клавиатуры для ячейки
     * @param event событие
     * @param cellElement элемент ячейки таблицы
     */
    private void cellEvent(Event event, TableCellElement cellElement) {
        int rowIndex = TableRowElement.as(cellElement.getParentElement()).getRowIndex();
        int cellIndex = cellElement.getCellIndex();
        if (event.getTypeInt() == Event.ONKEYDOWN) {
            switch (event.getKeyCode()) {
                case KeyCodes.KEY_TAB:
                    event.preventDefault();
                    cellTab(rowIndex, cellIndex, event.getShiftKey());
                    break;
                case KeyCodes.KEY_LEFT:
                    event.preventDefault();
                    selectionModel.clear(false);
                    selectionModel.setSelected(objects.get(rowIndex + start),
                            columns.get(Utils.positiveMod(cellIndex - 1, table.getCellCount(rowIndex))),
                            true, true);
                    table.getFlexCellFormatter().getElement(rowIndex,
                            Utils.positiveMod(cellIndex - 1, table.getCellCount(rowIndex))).focus();
                    break;
                case KeyCodes.KEY_RIGHT:
                    event.preventDefault();
                    selectionModel.clear(false);
                    selectionModel.setSelected(objects.get(rowIndex + start),
                            columns.get(Utils.positiveMod(cellIndex + 1, table.getCellCount(rowIndex))),
                            true, true);
                    table.getFlexCellFormatter().getElement(rowIndex,
                            Utils.positiveMod(cellIndex + 1, table.getCellCount(rowIndex))).focus();
                    break;
                case KeyCodes.KEY_DOWN:
                    event.preventDefault();
                    selectionModel.clear(false);
                    selectionModel.setSelected(objects.get(start + Utils.positiveMod(rowIndex + 1, getVisibleRange().getLength())),
                            columns.get(cellIndex), true, true);
                    table.getFlexCellFormatter().getElement(Utils.positiveMod(rowIndex + 1, getVisibleRange().getLength()), cellIndex).focus();
                    break;
                case KeyCodes.KEY_UP:
                    event.preventDefault();
                    selectionModel.clear(false);
                    selectionModel.setSelected(objects.get(start + Utils.positiveMod(rowIndex - 1, getVisibleRange().getLength())),
                            columns.get(cellIndex), true, true);

                    table.getFlexCellFormatter().getElement(Utils.positiveMod(rowIndex - 1, getVisibleRange().getLength()), cellIndex).focus();
                    break;
                case KeyCodes.KEY_ENTER:
//                case KeyCodes.KEY_F2: пока без f2
                    event.preventDefault();
                    bus.fireEventFromSource(new StartEditEvent(), table.getWidget(rowIndex, cellIndex));
                    break;
                default:
            }
        } else if (event.getTypeInt() == Event.ONFOCUS) {
            clearTableSelection();
            selectionModel.setSelected(objects.get(rowIndex), columns.get(cellIndex), true, true);
        }
    }

    /**
     * Нажатие таба в таблице с выбором ячеек
     * @param rowIndex строка последней выбранной ячейки
     * @param cellIndex столбец последней выбранной ячейки
     */
    private void cellTab(int rowIndex, int cellIndex, boolean shift) {
        int newRow = rowIndex;
        int newCol = cellIndex;

        if (shift) {
            if (cellIndex == 0) {
                newRow--;
            }
            newCol--;
        } else {
            if (cellIndex == columns.size() - 1) {
                newRow++;
            }
            newCol++;
        }
        newRow = Utils.positiveMod(newRow, getVisibleRange().getLength());
        newCol = Utils.positiveMod(newCol, columns.size());
        selectionModel.clear(false);
        selectionModel.setSelected(objects.get(start + newRow), columns.get(newCol), true, true);
        table.getFlexCellFormatter().getElement(newRow, newCol).focus();
    }

    /**
     * Событие клавиатуры для ряда
     * @param event событие
     * @param rowElement элемент ряда таблицы
     */
    private void rowEvent(Event event, TableRowElement rowElement) {
        int rowIndex = rowElement.getRowIndex();
        switch (event.getKeyCode()) {
            case KeyCodes.KEY_TAB:
                event.preventDefault();
                selectionModel.clear(false);
                int newRowIndex = event.getShiftKey() ? rowIndex - 1 : rowIndex + 1;
                newRowIndex = Utils.positiveMod(newRowIndex, getVisibleRange().getLength());
                selectionModel.setSelected(objects.get(start + newRowIndex), null, true, true);
                table.getRowFormatter().getElement(newRowIndex).focus();
                break;
            case KeyCodes.KEY_DOWN:
                event.preventDefault();
                selectionModel.clear(false);
                selectionModel.setSelected(objects.get(start + Utils.positiveMod(rowIndex + 1, getVisibleRange().getLength())), null, true, true);
                table.getRowFormatter().getElement(Utils.positiveMod(rowIndex + 1, getVisibleRange().getLength())).focus();
                break;
            case KeyCodes.KEY_UP:
                event.preventDefault();
                selectionModel.clear(false);
                selectionModel.setSelected(objects.get(start + Utils.positiveMod(rowIndex - 1, getVisibleRange().getLength())), null, true, true);
                table.getRowFormatter().getElement(Utils.positiveMod(rowIndex - 1, getVisibleRange().getLength())).focus();
                break;
            default:
        }
    }

    /**
     * Возвращает ширину столбца
     *
     * @param column столбец
     * @return ширина; если не задана, то -1
     */
    public int getColumnWidth(ArtaColumn<T> column) {
        if (widths.containsKey(column)) {
            int width = widths.get(column);
            return width >= 0 ? width : -1;
        } else {
            return -1;
        }
    }

    /**
     * @see {@link #getColumnWidth(kz.arta.synergy.components.client.table.column.ArtaColumn)}
     * @param index позиция столбца
     */
    public int getColumnWidth(int index) {
        return getColumnWidth(columns.get(index));
    }

    /**
     * Инициализирует ширину столбцов
     *
     * Ширина столбцов, которым она не была присвоена методом {@link #setColumnWidth(kz.arta.synergy.components.client.table.column.ArtaColumn, int)}
     * одинаковая и определяется исходя из ширины таблицы.
     * В таблице всегда будет хотя бы один столбец с неопределенной
     * Если до начала инициализации некоторым столбцам были присвоена ширина с помощью метода
     * {@link #setColumnWidth(kz.arta.synergy.components.client.table.column.ArtaColumn, int)},
     * то ширина оставшихся столбцов равна оставшейся ширине таблице
     *
     */
    void initWidths() {
        List<ArtaColumn<T>> unsetColumns = new ArrayList<ArtaColumn<T>>();
        unsetColumns.addAll(columns);

        for (ArtaColumn<T> column : widths.keySet()) {
            setColumnWidth(column, widths.get(column));
            unsetColumns.remove(column);
        }
        for (ArtaColumn<T> column : unsetColumns) {
            setColumnWidth(column, -1);
        }
    }

    /**
     * Возвращает соседний видимый объект.
     *
     * @param startObject объект, начиная с которого (не включая) начинается поиск
     * @param forward true - следующий, false - предыдущий
     * @return соседний видимый объект
     */
    T getNextVisibleObject(T startObject, boolean forward) {
        if (startObject == null) {
            return objects.get(start);
        }
        int row = objects.indexOf(startObject);
        if (forward) {
            row++;
        } else {
            row--;
        }
        int visibleSize = Math.min(pageSize, objects.size() - start);
        row -= start;
        row = Utils.positiveMod(row, visibleSize);
        row += start;
        return objects.get(row);
    }

    /**
     * Снимает выделение с ячеек и строк таблицы.
     * Этот метод не меняет выделение, изменяет только вид.
     * Для изменения выделения надо использовать {@link kz.arta.synergy.components.client.table.TableSelectionModel#clear(boolean)}
     */
    private void clearTableSelection() {
        for (int row = 0; row < getVisibleRange().getLength(); row++) {
            for (int col = 0; col < table.getCellCount(row); col++) {
                table.getFlexCellFormatter().getElement(row, col).removeClassName(SynergyComponents.getResources().cssComponents().selected());
            }
            table.getRowFormatter().getElement(row).removeClassName(SynergyComponents.getResources().cssComponents().selected());
        }
    }

    /**
     * Обновляет вид таблицы до соответствия с выделенными элементами.
     * Вызывается при поступлении события об изменении выделения, поэтому если надо произвести
     * сложное изменение выделения, то можно изменять модель выделения без создания событий и после
     * изменения модели вызвать этот метод.
     */
    void updateSelection() {
        clearTableSelection();
        for (T selectedObject : selectionModel.getSelectedObjects()) {
            int rowIndex = objects.indexOf(selectedObject);
            if (rowIndex >= start && rowIndex < start + pageSize) {
                for (ArtaColumn<T> column : selectionModel.getSelectedColumns(selectedObject)) {
                    if (column == null) {
                        table.getRowFormatter().getElement(rowIndex - start).addClassName(SynergyComponents.getResources().cssComponents().selected());
                    } else {
                        int columnIndex = columns.indexOf(column);
                        table.getFlexCellFormatter().getElement(rowIndex - start, columnIndex).addClassName(SynergyComponents.getResources().cssComponents().selected());
                    }
                }
            }
        }
    }

    /**
     * Возвращает предыдущий столбец, который можно изменять
     *
     * @param startColumn столбец с которого начинается поиск (не включая)
     * @return столбец
     */
    int getPreviousEditableColumn(int startColumn) {
        if (startColumn < 0 || startColumn > columns.size()) {
            return -1;
        }
        for (int i = startColumn - 1; i >= 0; i--) {
            ArtaColumn<T> column = columns.get(i);
            if (column.isEditable()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Возвращает следующий столбец, который можно изменять.
     *
     * @param startColumn столбец с которого начинается поиск (не включая)
     * @return изменяемый столбец
     */
    public int getNextEditableColumn(int startColumn) {
        if (startColumn < -1 || startColumn >= columns.size()) {
            return -1;
        }
        for (int i = startColumn + 1; i < columns.size(); i++) {
            ArtaColumn<T> column = columns.get(i);
            if (column.isEditable()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Переместить виджеты из одного стоблца в другой
     *
     * @param table таблица
     * @param dest куда поместить виджеты
     * @param source откуда взять виджеты
     */
    void setColumnWidgets(FlexTable table, int dest, int source) {
        for (int row = 0; row < table.getRowCount(); row++) {
            if (table.isCellPresent(row, source)) {
                table.setWidget(row, dest, table.getWidget(row, source));

                Element tdSource = table.getFlexCellFormatter().getElement(row, source);
                Element tdDest = table.getFlexCellFormatter().getElement(row, dest);
                tdDest.setAttribute("style", tdSource.getAttribute("style"));
            }
        }
        try {
            getWidthCell(table, dest).getStyle().setProperty("width", getWidthCell(table, source).getStyle().getWidth());
        } catch (NullPointerException e) {
            // значит thead'a нет
        }
    }

    /**
     * Заменить виджеты столбца на указанной позиции
     *
     * @param table таблица
     * @param index позиция столбца
     * @param widgets виджеты
     * @param styles стили
     */
    static void setColumnWidgets(FlexTable table, int index, List<Widget> widgets, List<String> styles) {
        for (int row = 0; row < table.getRowCount(); row++) {
            if (widgets.get(row) != null) {
                table.setWidget(row, index, widgets.get(row));
                table.getFlexCellFormatter().getElement(row, index).setAttribute("style", styles.get(row));
            }
        }
    }

    /**
     * Сдвигает лист вперед или назад на одну позицию.
     *
     * @param list лист
     * @see {@link java.util.Collections#rotate(java.util.List, int)}
     */
    static <V> void rotate(List<V> list, boolean forward) {
        if (!forward) {
            V first = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                list.set(i - 1, list.get(i));
            }
            list.set(list.size() - 1, first);
        } else {
            V last = list.get(list.size() - 1);
            for (int i = list.size() - 2; i >= 0; i--) {
                list.set(i + 1, list.get(i));
            }
            list.set(0, last);
        }
    }

    /**
     * Изменяет позицию стоблца.
     * На самом деле здесь происходит перемещение виджетов и перемещение
     * inline-style (перемещение атрибута style у первого ряда таблиц заголовков) в dom-элементе table.
     * Столбец перемещается на новую позицию, остальные смещаются в нужную сторону.
     *
     * @param columnIndex позиция на которой находится столбец
     * @param targetPosition позиция на которую надо переместить столбец
     * @see {@link java.util.Collections#rotate(java.util.List, int)}
     */
    void changeColumnPosition(FlexTable table, int columnIndex, int targetPosition) {
        List<Widget> columnWidgets = new ArrayList<Widget>();
        List<String> styles = new ArrayList<String>();

        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.isCellPresent(i, columnIndex)) {
                columnWidgets.add(table.getWidget(i, columnIndex));
                styles.add(table.getFlexCellFormatter().getElement(i, columnIndex).getAttribute("style"));
            } else {
                columnWidgets.add(null);
                styles.add(null);
            }
        }
        String width = getWidthCell(table, columnIndex).getStyle().getWidth();
        
        if (targetPosition < columnIndex) {
            for (int col = columnIndex; col > targetPosition; col--) {
                setColumnWidgets(table, col, col - 1);
            }
            setColumnWidgets(table, targetPosition, columnWidgets, styles);
        } else if (targetPosition > columnIndex) {
            for (int col = columnIndex; col < targetPosition; col++) {
                setColumnWidgets(table, col, col + 1);
            }
            setColumnWidgets(table, targetPosition, columnWidgets, styles);
        }
        
        if (width != null) {
            getWidthCell(table, targetPosition).getStyle().setProperty("width", width);
        }
    }

    /**
     * Перемещает столбец на новую позицию, сохраняя относительный порядок остальных столбцов
     *
     * @param srcColumnIndex начальная позиция
     * @param targetPosition позиция на которую надо переместить столбец
     */
    public void changeColumnPosition(int srcColumnIndex, int targetPosition) {
        changeColumnPosition(table, srcColumnIndex, targetPosition);
        rotate(columns.subList(Math.min(srcColumnIndex, targetPosition),
                Math.max(srcColumnIndex, targetPosition) + 1), srcColumnIndex > targetPosition);
    }

    /**
     * @param column столбец
     * @see {@link #columnHasWidth(int)}
     */
    public boolean columnHasWidth(ArtaColumn<T> column) {
        return widths.containsKey(column) && widths.get(column) != -1;
    }

    /**
     * Задана ли ширина для столбца
     *
     * @param index позиция столбца
     */
    public boolean columnHasWidth(int index) {
        return index >= 0 && index < columns.size() && columnHasWidth(columns.get(index));
    }

    /**
     * Задать ширину столбца.
     *
     * @param column столбец
     * @param width ширина
     */
    public void setColumnWidth(ArtaColumn<T> column, int width) {
        if (!columns.contains(column)) {
            return;
        }
        int index = columns.indexOf(column);
        if (width < 0) {
            if (isAttached()) {
                getWidthCell(index).getStyle().clearWidth();
            }
            widths.put(column, -1);
        } else {
            if (isAttached()) {
                getWidthCell(index).getStyle().setWidth(width, Style.Unit.PX);
            }
            widths.put(column, width);
        }
    }

    /**
     * @param index позиция столбца
     * {@link #setColumnWidth(kz.arta.synergy.components.client.table.column.ArtaColumn, int)}
     */
    void setColumnWidth(int index, int width) {
        if (index >= 0 && index < columns.size()) {
            setColumnWidth(columns.get(index), width);
        }
    }

    /**
     * Сортирует таблицу по столбцу
     *
     * @param column столбец
     */
    public void sort(ArtaColumn<T> column) {
        if (column == null || !columns.contains(column)) {
            return;
        }
        sortedColumn = column;
        boolean isAscending = Header.DEFAULT_IS_ASCENDING;

        Header header = column.getHeader();
        if (header != null) {
            header.setSorted(true);
            isAscending = header.isAscending();
        }

        bus.fireEventFromSource(new TableSortEvent<T>(column, isAscending), this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ArtaColumn<T> getSortedColumn() {
        return sortedColumn;
    }

    /**
     * Добавляет столбец.
     *
     * Повторное добавление одинаковых объектов {@link kz.arta.synergy.components.client.table.column.ArtaColumn}
     * запрещено. Одинаковые (по виду) столбцы возможны, но они должны быть разными объектами.
     * @param column столбец
     */
    public void addColumn(final ArtaColumn<T> column) {
        if (columns.contains(column)) {
            return;
        }
        columns.add(column);
        for (int i = 0; i < Math.min(table.getRowCount(), objects.size() - start); i++) {
            table.addCell(i);
            int cellColumn = table.getCellCount(i) - 1;
            table.setWidget(i, cellColumn, column.createWidget(objects.get(cellColumn), bus));
            if (!onlyRows) {
                table.getElement().setTabIndex(TAB_INDEX);
            }
        }
        TableElement ele = TableElement.as(table.getElement());
        ele.getTHead().getRows().getItem(0).insertCell(-1);
    }

    @Override
    public TableSelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public T getVisibleItem(int indexOnPage) {
        return objects.get(getVisibleRange().getStart() + indexOnPage);
    }

    @Override
    public int getVisibleItemCount() {
        return getVisibleRange().getLength();
    }

    @Override
    public Iterable<T> getVisibleItems() {
        Range visibleRange = getVisibleRange();
        return objects.subList(visibleRange.getStart(),
                visibleRange.getStart() + visibleRange.getLength());
    }

    public ArtaColumn<T> getLastColumn() {
        return columns.get(columns.size() - 1);
    }
    public ArtaColumn<T> getColumn(int index) {
        return columns.get(index);
    }

    public List<ArtaColumn<T>> getColumns() {
        return columns;
    }
    
    public Element getWidthCell(int column) {
        return getWidthCell(table, column);
    }
    
    private static Element getWidthCell(FlexTable table, int column) {
        TableElement te = TableElement.as(table.getElement());
        try {
            return te.getTHead().getRows().getItem(0).getCells().getItem(column);
        } catch (Throwable e) {
            return null;
        }
    }
    
    void setRow(int row, T value) {
        for (int i = 0; i < columns.size(); i++) {
            ArtaColumn<T> column = columns.get(i);
            if (!table.isCellPresent(row, i) || table.getWidget(row, i) == null) {
                // новая ячейка
                Widget widget = column.createWidget(value, bus);
                table.setWidget(row, i, widget);
                // апдейт после присоединения к dom нужен для обновления столбцом td
                column.updateWidget(widget, value);
                if (!onlyRows) {
                    table.getFlexCellFormatter().getElement(row, i).setTabIndex(TAB_INDEX);
                }
            } else {
                column.updateWidget(table.getWidget(row, i), value);
            }
        }
        if (onlyRows) {
            table.getRowFormatter().getElement(row).setTabIndex(TAB_INDEX);
        }
    }

    public void redraw() {
        if (pageSize > objects.size() - start) {
            table.addStyleName(SynergyComponents.getResources().cssComponents().notFull());
        } else {
            table.removeStyleName(SynergyComponents.getResources().cssComponents().notFull());
        }

        while (table.getRowCount() > pageSize) {
            table.getRowFormatter().getElement(table.getRowCount() - 1).removeFromParent();
        }

        table.getRowFormatter().getElement(pageSize - 1).addClassName(SynergyComponents.getResources().cssComponents().last());
    }

    /**
     * Изменяет значения объектов, нужные изменения сразу отображаются на текущей странице.
     * Здесь предполагается правильное количество объектов, которое задается через {@link #setRowCount(int)}
     *
     * @param start начальная позиция в объектах
     * @param values новые значения
     */
    @Override
    public void setRowData(int start, List<? extends T> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        if (start < 0 || start + values.size() > objects.size()) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < values.size(); i++) {
            objects.set(i + start, values.get(i));

            int rowNum = start - this.start + i;
            if (rowNum >= 0 && rowNum < pageSize) {
                setRow(rowNum, values.get(i));
            }
        }
        SelectionChangeEvent.fire(selectionModel);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setSelectionModel(SelectionModel<? super T> selectionModel) {
        if (!(selectionModel instanceof TableSelectionModel)) {
            throw new IllegalArgumentException();
        }
        this.selectionModel = (TableSelectionModel) selectionModel;
    }

    /**
     * Управляет отображаемым интервалом.
     * Вызывается, например, при листании страниц.
     * При изменении интервала создается событие
     *
     * @param range новый интервал
     * @param forceEvents если true, событие создается даже когда интервал не был изменен
     * @param clearData очищает данные
     */
    private void setVisibleRange(Range range, boolean clearData, boolean forceEvents) {
        boolean changed = false;
        if (start != range.getStart()) {
            start = range.getStart();
            changed = true;
        }
        if (pageSize != range.getLength()) {
            pageSize = range.getLength();
            changed = true;
        }

        if (clearData) {
            objects.clear();
        }

        if (changed || forceEvents) {
            RangeChangeEvent.fire(this, range);
        }
    }

    @Override
    public void setVisibleRangeAndClearData(final Range range, boolean forceRangeChangeEvent) {
        setVisibleRange(range, true, forceRangeChangeEvent);
    }

    @Override
    public HandlerRegistration addCellPreviewHandler(CellPreviewEvent.Handler<T> handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HandlerRegistration addRangeChangeHandler(RangeChangeEvent.Handler handler) {
        return bus.addHandlerToSource(RangeChangeEvent.getType(), this, handler);
    }

    @Override
    public HandlerRegistration addRowCountChangeHandler(RowCountChangeEvent.Handler handler) {
        return bus.addHandlerToSource(RowCountChangeEvent.getType(), this, handler);
    }

    @Override
    public int getRowCount() {
        return objects.size();
    }

    @Override
    public Range getVisibleRange() {
        return new Range(start, Math.min(pageSize, objects.size() - start));
    }

    @Override
    public boolean isRowCountExact() {
        return isRowCountExact;
    }

    @Override
    public void setRowCount(int count) {
        boolean rowCountChange = false;
        if (objects.size() < count) {
            for (int i = objects.size(); i < count; i++) {
                objects.add(null);
            }
            rowCountChange = true;
        } else if (objects.size() > count) {

            //случай, когда удаляются элементы в конце
            for (int row = count - start; row < pageSize; row++) {
                TableRowElement rowElement = TableRowElement.as(table.getRowFormatter().getElement(row));
                while (rowElement.getCells().getLength() > 0) {
                    rowElement.deleteCell(0);
                }
            }

            objects.subList(count, objects.size()).clear();
            rowCountChange = true;
        }
        if (rowCountChange) {
            RowCountChangeEvent.fire(this, getRowCount(), isRowCountExact);
        }
    }

    @Override
    public void setRowCount(int count, boolean isExact) {
        setRowCount(count);
        isRowCountExact = isExact;
    }

    @Override
    public void setVisibleRange(int start, int length) {
        setVisibleRange(new Range(start, length));
    }

    @Override
    public void setVisibleRange(Range range) {
        setVisibleRange(range, false, true);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
    }

    public HandlerRegistration addSortHandler(TableSortEvent.Handler<T> handler) {
        return bus.addHandlerToSource(TableSortEvent.TYPE, this, handler);
    }

    /**
     * Изменяет режим выбора в таблице
     *
     * @param onlyRows true - можно выбирать ряды, false - можно выбирать ячейки
     */
    public void setOnlyRows(boolean onlyRows) {
        if (onlyRows) {
            table.addStyleName(SynergyComponents.getResources().cssComponents().onlyRows());
        } else {
            table.removeStyleName(SynergyComponents.getResources().cssComponents().onlyRows());
        }
        this.onlyRows = onlyRows;
        selectionModel.clear();
    }

    /**
     * Возвращает номер ряда в котором расположен объект с заданным ключем.
     * Если объект расположен не на текущей странице или такого объекта
     * просто нет - возвращает -1.
     *
     * @param key ключ
     * @return номер ряда с объектом
     */
    public int getRowById(Object key) {
        if (keyProvider == null) {
            return -1;
        }

        for (int i = 0; i < Math.min(objects.size() - start, pageSize); i++) {
            if (keyProvider.getKey(objects.get(i + start)).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isMultiSelectionAllowed() {
        return multiSelectionAllowed;
    }

    public void setMultiSelectionAllowed(boolean multiSelectionAllowed) {
        this.multiSelectionAllowed = multiSelectionAllowed;
    }

    /**
     * Включить-выключить перенос на следующую строку
     */
    public void setMultiLine(boolean multiLine) {
        if (multiLine) {
            table.addStyleName(SynergyComponents.getResources().cssComponents().multipleLines());
        } else {
            table.removeStyleName(SynergyComponents.getResources().cssComponents().multipleLines());
        }
    }

    public HandlerRegistration addContextMenuHandler(TableMenuEvent.Handler handler) {
        return bus.addHandlerToSource(TableMenuEvent.TYPE, this, handler);
    }

    /**
     * Возвращает виджет на указанной строке и столбце
     * @param row строка
     * @param col столбец
     * @return  виджет
     */
    @SuppressWarnings("UnusedDeclaration")
    public Widget getWidget(int row, int col) {
        return table.getWidget(row, col);
    }
}
