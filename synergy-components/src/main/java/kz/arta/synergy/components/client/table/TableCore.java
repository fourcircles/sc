package kz.arta.synergy.components.client.table;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.table.column.ArtaColumn;
import kz.arta.synergy.components.client.table.events.CellEditEvent;
import kz.arta.synergy.components.client.table.events.TableSortEvent;

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
 * @see {@link Table} дополнительный функционал
 */
public class TableCore<T> extends Composite implements HasData<T> {
    /**
     * Корневая панель
     */
    private final ArtaScrollPanel scroll;

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
    private List<ArtaColumn<T, ?>> columns;

    /**
     * Модель выбора объекта
     */
    private TableSelectionModel<T> selectionModel;
    private EventBus bus;

    /**
     * Режим выбора в таблице.
     * true - выбираются ряды, false - ячейки
     */
    private boolean onlyRows;

    /**
     * Выбранный ряд.
     */
    private int selectedRow = -1;

    /**
     * Выбранный столбец
     */
    private int selectedColumn = -1;

    /**
     * Столбец, по которому отсортирована таблица
     */
    private ArtaColumn<T, ?> sortedColumn;

    /**
     * Ширина заголовков
     */
    Map<ArtaColumn<T, ?>, Integer> widths = new HashMap<ArtaColumn<T, ?>, Integer>();

    /**
     * Задана ли высота. Если не задана, то таблица растягивается.
     * Растяжение таблицы реализовано, как периодическое изменение параметра height.
     * Явно заданный параметр height необходим для корректной работы скролла.
     */
    private boolean isHeightSet;

    /**
     * Точное ли количество объектов
     */
    private boolean isRowCountExact;

    public TableCore(int pageSize, ProvidesKey<T> keyProvider, EventBus bus) {
        scroll = new ArtaScrollPanel();
        initWidget(scroll);
        this.bus = bus;

        this.keyProvider = keyProvider;
        this.pageSize = pageSize;
        selectionModel = new TableSelectionModel<T>(bus, keyProvider);

        table = new FlexTable();
        table.addStyleName(SynergyComponents.resources.cssComponents().table());
        for (int i = 0; i < pageSize; i++) {
            table.insertRow(0);
        }

        scroll.setWidget(table);

        columns = new ArrayList<ArtaColumn<T, ?>>();
        objects = new ArrayList<T>(pageSize);

        table.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                HTMLTable.Cell cell = table.getCellForEvent(event);
                T object = objects.get(cell.getRowIndex() + start);


                if (onlyRows) {
                    selectionModel.setSelected(object, null, true);
                } else {
                    ArtaColumn<T, ?> column = columns.get(cell.getCellIndex());
                    if (column.isEditable()) {
                        selectionModel.setSelected(object, column, true);
                    }
                }
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
                select(selectionModel.getSelectedObject(), selectionModel.getSelectedColumn());
            }
        });

        sinkEvents(Event.ONCLICK);
        sinkEvents(Event.ONKEYDOWN);
        sinkEvents(Event.ONKEYUP);
        sinkEvents(Event.ONBLUR);
        sinkEvents(Event.ONFOCUS);
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
            table.getRowFormatter().getElement(row).addClassName(SynergyComponents.resources.cssComponents().edit());
            td.addClassName(SynergyComponents.resources.cssComponents().edit());
            if (tdUnder != null) {
                tdUnder.addClassName(SynergyComponents.resources.cssComponents().underEdit());
            }
        } else {
            table.getRowFormatter().getElement(row).removeClassName(SynergyComponents.resources.cssComponents().edit());
            td.removeClassName(SynergyComponents.resources.cssComponents().edit());
            if (tdUnder != null) {
                tdUnder.removeClassName(SynergyComponents.resources.cssComponents().underEdit());
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
            selectNextCell();
        }
    }

    /**
     * Обработка событий навигационных клавиш и таба
     */
    @Override
    public void onBrowserEvent(com.google.gwt.user.client.Event event) {
        super.onBrowserEvent(event);
        int type = event.getTypeInt();
        int keyCode = event.getKeyCode();

        if (type == Event.ONKEYDOWN) {
            switch (keyCode) {
                case KeyCodes.KEY_DOWN:
                    event.preventDefault();
                    selectionModel.setSelected(getNextVisibleObject(selectionModel.getSelectedObject(), true),
                            selectionModel.getSelectedColumn(), true);
                    break;
                case KeyCodes.KEY_UP:
                    event.preventDefault();
                    selectionModel.setSelected(getNextVisibleObject(selectionModel.getSelectedObject(), false),
                            selectionModel.getSelectedColumn(), true);
                    break;
                case KeyCodes.KEY_RIGHT:
                case KeyCodes.KEY_TAB:
                    event.preventDefault();
                    selectNextCell();
                    break;
                case KeyCodes.KEY_LEFT:
                    event.preventDefault();
                    selectPreviousCell();
                    break;
                default:
            }
        }
    }

    /**
     * Возвращает ширину столбца
     * @param column столбец
     * @return ширина; если не задана, то -1
     */
    public int getColumnWidth(ArtaColumn<T, ?> column) {
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
     */
    void initWidths() {
        for (int i = 0; i < columns.size() - 1; i++) {
            setColumnWidth(i, table.getFlexCellFormatter().getElement(0, i).getOffsetWidth());
        }
        //последний столбец (на момент загрузки) подстраивается по ширине
        setColumnWidth(columns.size() - 1, -1);
    }

    /**
     * Возвращает только положительный результат модуля в отличии от %
     * @param value значение
     * @param mod модуль
     */
    static int positiveMod(int value, int mod) {
        int res = value;
        if (value < 0) {
            res += mod * (-value / mod + 1);
        }
        return res % mod;
    }

    /**
     * Возвращает соседний видимый объект.
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
        row = positiveMod(row, visibleSize);
        row += start;
        return objects.get(row);
    }

    /**
     * Выделяет/снимает выделение ячейки или ряда в таблице.
     * @param row номер ряда
     * @param column номер столбца, если -1, то операция производится для ряда
     * @param select выделить или снять выделение
     */
    void innerSelect(int row, int column, boolean select) {
        if (row < 0 || row >= Math.min(pageSize, objects.size() - start)) {
            return;
        }
        Element element;
        if (column != -1) {
            element = table.getFlexCellFormatter().getElement(row, column);
            if (select) {
                table.getWidget(row, column).getElement().focus();
            } else {
                table.getWidget(row, column).getElement().blur();
            }
        } else {
            element = table.getRowFormatter().getElement(row);
            table.getElement().focus();
        }
        if (select) {
            element.addClassName(SynergyComponents.resources.cssComponents().selected());
            element.scrollIntoView();
        } else {
            element.removeClassName(SynergyComponents.resources.cssComponents().selected());
        }
    }

    /**
     * Выделяет ячейку или ряд соответствующую объекту и столбцу.
     * @param object объект
     * @param column столбец
     */
    public void select(T object, ArtaColumn<T, ?> column) {
        if (selectedRow != -1) {
            innerSelect(selectedRow, selectedColumn, false);
        }

        if (object != null && objects.contains(object)) {
            selectedRow = objects.indexOf(object) - start;
            if (column != null) {
                selectedColumn = columns.indexOf(column);
            } else {
                selectedColumn = -1;
            }
        } else {
            selectedRow = -1;
            selectedColumn = -1;
        }
        if (selectedRow != -1) {
            innerSelect(selectedRow, selectedColumn, true);
        }
    }

    /**
     * Выделяет следующую ячейку, возможен переход на следующий ряд
     * Выделяются только ячейки из изменяемых столбцов.
     */
    private void selectNextCell() {
        if (selectionModel.getSelectedColumn() == null) {
            return;
        }
        int columnNum = columns.indexOf(selectionModel.getSelectedColumn());
        int nextColumn = getNextEditableColumn(columnNum);
        if (nextColumn == -1) {
            nextColumn = getNextEditableColumn(0);
            if (nextColumn != -1) {
                selectionModel.setSelected(getNextVisibleObject(selectionModel.getSelectedObject(), true), columns.get(nextColumn), true);
            }
        } else {
            selectionModel.setSelected(selectionModel.getSelectedObject(), columns.get(nextColumn), true);
        }
    }

    /**
     * Выделяет предыдущую ячейку, возможен переход на предыдущий ряд.
     * Выделяются только ячейки из изменяемых столбцов.
     */
    private void selectPreviousCell() {
        if (selectionModel.getSelectedColumn() == null) {
            return;
        }
        int columnNum = columns.indexOf(selectionModel.getSelectedColumn());
        int previousColumn = getPreviousEditableColumn(columnNum);
        if (previousColumn == -1) {
            previousColumn = getPreviousEditableColumn(columns.size());
            if (previousColumn != -1) {
                selectionModel.setSelected(getNextVisibleObject(selectionModel.getSelectedObject(), false), columns.get(previousColumn), true);
            }
        } else {
            selectionModel.setSelected(selectionModel.getSelectedObject(), columns.get(previousColumn), true);
        }
    }

    /**
     * Возвращает предыдущий столбец, который можно изменять
     * @param startColumn столбец с которого начинается поиск (не включая)
     * @return столбец
     */
    int getPreviousEditableColumn(int startColumn) {
        if (startColumn < 0 || startColumn > columns.size()) {
            return -1;
        }
        for (int i = startColumn - 1; i >= 0; i--) {
            ArtaColumn<T, ?> column = columns.get(i);
            if (column.isEditable()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Возвращает следующий столбец, который можно изменять.
     * @param startColumn столбец с которого начинается поиск (не включая)
     * @return изменяемый столбец
     */
    public int getNextEditableColumn(int startColumn) {
        if (startColumn < 0 || startColumn >= columns.size()) {
            return -1;
        }
        for (int i = startColumn + 1; i < columns.size(); i++) {
            ArtaColumn<T, ?> column = columns.get(i);
            if (column.isEditable()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Переместить виджеты из одного стоблца в другой
     * @param table таблица
     * @param dest куда поместить виджеты
     * @param source откуда взять виджеты
     */
    void setColumnWidgets(FlexTable table, int dest, int source) {
        for (int row = 0; row < table.getRowCount(); row++) {
            table.setWidget(row, dest, table.getWidget(row, source));
        }
        if (columnHasWidth(source)) {
            int width = widths.get(columns.get(source));
            table.getFlexCellFormatter().getElement(0, dest).getStyle().setWidth(width, Style.Unit.PX);
        } else {
            table.getFlexCellFormatter().getElement(0, dest).getStyle().clearWidth();
        }
    }

    /**
     * Заменить виджеты столбца на указанной позиции
     * @param table таблица
     * @param index позиция столбца
     * @param widgets виджеты
     * @param width ширина
     */
    static void setColumnWidgets(FlexTable table, int index, List<Widget> widgets, int width) {
        for (int row = 0; row < table.getRowCount(); row++) {
            table.setWidget(row, index, widgets.get(row));

        }
        if (width != -1) {
            table.getFlexCellFormatter().getElement(0, index).getStyle().setWidth(width, Style.Unit.PX);
        } else {
            table.getFlexCellFormatter().getElement(0, index).getStyle().clearWidth();
        }
    }

    /**
     * Сдвигает лист вперед или назад на одну позицию.
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
     * На самом деле здесь происходит перемещение виджетов и (перемещение свойства ширина
     * у первого ряда таблиц заголовков) в dom-элементе table.
     * Столбец перемещается на новую позицию, остальные смещаются в нужную сторону.
     *
     * @param columnIndex позиция на которой находится столбец
     * @param targetPosition позиция на которую надо переместить столбец
     * @see {@link java.util.Collections#rotate(java.util.List, int)}
     */
    void changeColumnPosition(FlexTable table, int columnIndex, int targetPosition) {
        List<Widget> columnWidgets = new ArrayList<Widget>();
        int width = getColumnWidth(columnIndex);

        for (int i = 0; i < table.getRowCount(); i++) {
            columnWidgets.add(table.getWidget(i, columnIndex));
        }
        if (targetPosition < columnIndex) {
            for (int col = columnIndex; col > targetPosition; col--) {
                setColumnWidgets(table, col, col - 1);
            }
            setColumnWidgets(table, targetPosition, columnWidgets, width);
        } else if (targetPosition > columnIndex) {
            for (int col = columnIndex; col < targetPosition; col++) {
                setColumnWidgets(table, col, col + 1);
            }
            setColumnWidgets(table, targetPosition, columnWidgets, width);
        }
    }

    /**
     * Перемещает столбец на новую позицию, сохраняя относительный порядок остальных столбцов
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
    public boolean columnHasWidth(ArtaColumn<T, ?> column) {
        return widths.containsKey(column) && widths.get(column) != -1;
    }

    /**
     * Задана ли ширина для столбца
     * @param index позиция столбца
     */
    public boolean columnHasWidth(int index) {
        if (index >= 0 && index < columns.size()) {
            return columnHasWidth(columns.get(index));
        }
        return false;
    }

    /**
     * Задать ширину столбца.
     * Пользователю для задания ширины столбца надо использовать {@link Table#setColumnWidth(int, int)}
     * для сохранения соответствия ширины заголовков и положения разделителей.
     * @param column столбец
     * @param width ширина
     */
    public void setColumnWidth(ArtaColumn<T, ?> column, int width) {
        if (!columns.contains(column)) {
            return;
        }
        int index = columns.indexOf(column);
        if (width < 0) {
            table.getFlexCellFormatter().getElement(0, index).getStyle().clearWidth();
            widths.put(column, -1);
        } else {
            table.getFlexCellFormatter().getElement(0, index).getStyle().setWidth(width, Style.Unit.PX);
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
     * @param column столбец
     */
    public void sort(ArtaColumn<T, ?> column) {
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

    public ArtaColumn<T, ?> getSortedColumn() {
        return sortedColumn;
    }

    /**
     * Добавляет столбец.
     *
     * Повторное добавление одинаковых объектов {@link kz.arta.synergy.components.client.table.column.ArtaColumn}
     * запрещено. Одинаковые (по виду) столбцы возможны, но они должны быть разными объектами.
     * @param column столбец
     */
    public void addColumn(final ArtaColumn<T, ?> column) {
        if (columns.contains(column)) {
            return;
        }
        columns.add(column);
        for (int i = 0; i < Math.min(table.getRowCount(), objects.size() - start); i++) {
            table.addCell(i);
            int cellColumn = table.getCellCount(i) - 1;
            table.setWidget(i, cellColumn, column.createWidget(objects.get(cellColumn), bus));
        }
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

    public ArtaColumn<T, ?> getLastColumn() {
        return columns.get(columns.size() - 1);
    }
    public ArtaColumn<T, ?> getColumn(int index) {
        return columns.get(index);
    }

    public List<ArtaColumn<T, ?>> getColumns() {
        return columns;
    }

    public Element getElement(int row, int column) {
        return table.getFlexCellFormatter().getElement(row, column);
    }

    void setRow(int row, T value) {
        for (int i = 0; i < columns.size(); i++) {
            ArtaColumn<T, ?> column = columns.get(i);
            if (!table.isCellPresent(row, i) || table.getWidget(row, i) == null) {
                Widget widget = column.createWidget(value, bus);
                table.setWidget(row, i, widget);
            } else {
                column.updateWidget(table.getWidget(row, i), value);
            }
        }
    }

    public void redraw() {
        selectionModel.clear();
        if (pageSize > objects.size() - start) {
            table.addStyleName(SynergyComponents.resources.cssComponents().notFull());
        } else {
            table.removeStyleName(SynergyComponents.resources.cssComponents().notFull());
        }

        while (table.getRowCount() > pageSize) {
            table.getRowFormatter().getElement(table.getRowCount() - 1).removeFromParent();
        }

        table.getRowFormatter().getElement(pageSize - 1).addClassName(SynergyComponents.resources.cssComponents().last());

        if (!isHeightSet) {
            scroll.getElement().getStyle().setHeight(table.getOffsetHeight(), Style.Unit.PX);
        }
    }

    @Override
    public void setHeight(String height) {
        throw new UnsupportedOperationException();
    }

    public void setHeight(int height) {
        isHeightSet = true;
        scroll.getElement().getStyle().setHeight(height, Style.Unit.PX);
    }

    /**
     * Если высота была задана {@link #setHeight(int)}, убирает заданную высоту и таблица растягивается
     */
    public void clearHeight() {
        isHeightSet = false;
        scroll.getElement().getStyle().clearHeight();
    }

    /**
     * Изменяет значения объектов, нужные изменения сразу отображаются на текущей странице.
     * Здесь предполагается правильное количество объектов, которое задается через {@link #setRowCount(int)}
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
        return new Range(start, pageSize);
    }

    @Override
    public boolean isRowCountExact() {
        return isRowCountExact;
    }

    /**
     * Переменная отображает количество добавленных объектов.
     *
     * При уменьшении количества добавленных объектов размер objects не уменьшится.
     * Это пример случая, когда objects.size() не равен rowCount.
     */
    private int rowCount;

    @Override
    public void setRowCount(int count) {
        boolean rowCountChange = false;
        if (objects.size() < count) {
            for (int i = objects.size(); i < count; i++) {
                objects.add(null);
            }
            rowCountChange = true;
        } else if (objects.size() > count) {
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
     * @param onlyRows true - можно выбирать ряды, false - можно выбирать ячейки
     */
    public void setOnlyRows(boolean onlyRows) {
        if (onlyRows) {
            table.addStyleName(SynergyComponents.resources.cssComponents().onlyRows());
        } else {
            table.removeStyleName(SynergyComponents.resources.cssComponents().onlyRows());
        }
        this.onlyRows = onlyRows;
        selectionModel.clear();
    }

    /**
     * Возвращает номер ряда в котором расположен объект с заданным ключем.
     * Если объект расположен не на текущей странице или такого объекта
     * просто нет - возвращает -1.
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

    public int getPageSize() {
        return pageSize;
    }
}
