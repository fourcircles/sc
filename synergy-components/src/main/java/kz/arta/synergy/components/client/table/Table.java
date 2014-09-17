package kz.arta.synergy.components.client.table;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.table.column.ArtaColumn;
import kz.arta.synergy.components.client.table.events.CellEditEvent;
import kz.arta.synergy.components.client.table.events.TableSortEvent;
import kz.arta.synergy.components.style.client.Constants;

import java.util.*;

/**
 * User: vsl
 * Date: 02.09.14
 * Time: 11:50
 *
 * Таблица
 */
public class Table<T> extends Composite implements HasData<T> {
    /**
     * Ширина разделительной линии, в хэдерах, при перетаскивании столбцов
     * Очень желательно, чтобы эта величина была нечетной
     */
    private static final int HEADER_DIVIDER_WIDTH = 11;

    private EventBus bus = new SimpleEventBus();

    private final ProvidesKey<T> keyProvider;

    /**
     * Корневая панель таблицы
     */
    private FlowPanel root;

    /**
     * Таблица
     */
    private FlexTable table;

    /**
     * Объекты добавленные в таблицу
     */
    private ArrayList<T> objects;

    /**
     * Столбцы
     */
    private List<ArtaColumn<T, ?>> columns;

    /**
     * Таблица заголовков
     */
    private final FlexTable headersTable;

    /**
     * Ширина заголовков
     */
    private Map<ArtaColumn<T, ?>, Integer> widths = new HashMap<ArtaColumn<T, ?>, Integer>();

    /**
     * Невидимые разделители для изменения ширины столбцов
     */
    private List<ArtaFlowPanel> dividers = new ArrayList<ArtaFlowPanel>();

    /**
     * Производится ли изменение ширины столбцов в данный момент
     */
    private boolean resizing = false;

    /**
     * Столбец, по которому отсортирована таблица
     */
    private ArtaColumn<T, ?> sortedColumn;
    /**
     * Соответствие между столбцами и заголовками
     */
    private HashMap<ArtaColumn<T, ?>, Header> headersMap = new HashMap<ArtaColumn<T, ?>, Header>();

    /**
     * Заголовок, который отображается при перетаскивании
     */
    private Header headerProxy;
    /**
     * Нажата ли кнопка мыши на заголовке.
     */
    private boolean headerMouseDown;
    /**
     * Производится ли перетаскивание заголовка
     */
    private boolean isDragging;

    /**
     * Расположения границ заголовков
     */
    private TreeSet<Integer> borderLocations = new TreeSet<Integer>();

    /**
     * Граница заголовка куда должен переместиться перетаскиваемый заголовок
     */
    private int selectedHeadersBorder;

    /**
     * Указатель новой позиции стоблца при завершении перетаскивания
     */
    private FlowPanel headerDivider;

    /**
     * Предыдущая координата мыши при перемещении заголовка.
     * Используется для определения направления перемещения.
     */
    private int oldX;

    /**
     * Заголовок, который надо перенести при завершении перетаскивания
     */
    private Header headerToMove;

    /**
     * Модель выбора объекта
     */
    private TableSelectionModel<T> selectionModel;

    /**
     * Начало отображаемого множества объектов
     */
    private int start;

    /**
     * Количество объектов на странице
     */
    private int pageSize;

    /**
     * Внутренний скролл таблицы
     */
    private final ArtaScrollPanel tableContainer;

    /**
     * Задана ли высота. Если не задана, то таблица растягивается
     */
    private boolean isHeightSet = false;

    /**
     * Заданная высота
     */
    private int wholeTableHeight;

    /**
     * Режим выбора в таблице.
     * true - выбираются ряды, false - ячейки
     */
    private boolean onlyRows = false;

    /**
     * Выбранный ряд.
     */
    private int selectedRow = -1;

    /**
     * Выбранный столбец
     */
    private int selectedColumn = -1;

    private TableHat hat;

    /**
     * Переривывает таблицу
     */
    public void redraw() {
        selectionModel.clear();
        for (int i = 0; i < Math.min(pageSize, objects.size() - start); i++) {
            setRow(i, objects.get(start + i));
        }
        if (pageSize > objects.size() - start) {
            table.addStyleName(SynergyComponents.resources.cssComponents().notFull());
        } else {
            table.removeStyleName(SynergyComponents.resources.cssComponents().notFull());
        }

        if (table.getRowCount() > pageSize) {
            for (int i = pageSize; i < table.getRowCount(); i++) {
                table.getRowFormatter().getElement(objects.size()).removeFromParent();
            }
        }
        table.getRowFormatter().getElement(pageSize - 1).addClassName(SynergyComponents.resources.cssComponents().last());
        redrawDividers();

        if (isHeightSet) {
            int tableHeight = wholeTableHeight;
            if (hasHat()) {
                tableHeight -= 40; //шапка
            }
            tableHeight -= 32; //хедеры
            tableContainer.getElement().getStyle().setHeight(tableHeight, Style.Unit.PX);
        } else {
            tableContainer.getElement().getStyle().setHeight(table.getOffsetHeight(), Style.Unit.PX);
        }
    }

    /**
     * Изменяет количество и положение разделителей в соответствии со столбцами
     */
    public void redrawDividers() {
        if (dividers.size() != columns.size() - 1) {
            while (dividers.size() > columns.size() - 1) {
                ArtaFlowPanel divider = dividers.get(columns.size());
                divider.removeFromParent();
                dividers.remove(divider);
            }
            while (dividers.size() < columns.size() - 1) {
                ArtaFlowPanel divider = createDivider();
                dividers.add(divider);
                root.add(divider);
            }
        }

        int first;
        int last;
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            first = 0;
            last = columns.size() - 1;
        } else {
            first = 1;
            last = columns.size();
        }
        int dividersCount = 0;
        for (int i = first; i < last; i++) {
            int absoluteLeft = table.getFlexCellFormatter().getElement(0, i).getAbsoluteLeft() - 1;
            absoluteLeft -= root.getAbsoluteLeft();
            absoluteLeft -= Constants.TABLE_DIVIDER_WIDTH / 2;

            ArtaFlowPanel divider = dividers.get(dividersCount++);
            divider.getElement().getStyle().setLeft(absoluteLeft, Style.Unit.PX);

            if (hasHat()) {
                divider.getElement().getStyle().setTop(40, Style.Unit.PX);
            } else {
                divider.getElement().getStyle().setTop(0, Style.Unit.PX);
            }
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        for (int i = 0; i < columns.size(); i++) {
            widths.put(columns.get(i), table.getFlexCellFormatter().getElement(0, i).getOffsetWidth());
        }
        widths.put(columns.get(columns.size() - 1), -1);
        redraw();
    }

    /**
     * Изменяет ширину столбца на заданной позиции
     * @param index позиция
     * @param width ширина, если -1, то свойство width убирается и столбец растягивается
     */
    private void setColumnWidth(int index, int width) {
        Style tableStyle = table.getFlexCellFormatter().getElement(0, index).getStyle();
        Style headersStyle = headersTable.getFlexCellFormatter().getElement(0, index).getStyle();
        widths.put(columns.get(index), width);
        if (width == -1) {
            tableStyle.clearWidth();
            headersStyle.clearWidth();
        } else {
            tableStyle.setWidth(width, Style.Unit.PX);
            headersStyle.setWidth(width, Style.Unit.PX);
        }
        headersMap.get(columns.get(index)).setWidth(headersTable.getFlexCellFormatter().getElement(0, index).getOffsetWidth());
    }

    /**
     * Изменяет ширину столбцов в соответствии с положением разделителей.
     * Обычно вызывается после перетаскивания разделителя.
     */
    public void resizeToDividers() {
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            for (int i = 0; i < dividers.size(); i++) {
                if (widths.get(columns.get(i)) != -1) {
                    setColumnWidth(i, root.getAbsoluteLeft() + root.getOffsetWidth() - dividers.get(i).getAbsoluteLeft());
                } else {
                    headersMap.get(columns.get(i)).setWidth(headersTable.getFlexCellFormatter().getElement(0, i).getOffsetWidth());
                }

            }
        }
        int lastColumnEnd = 0;
        for (int i = 0; i < dividers.size(); i++) {
            int offset;
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                offset = root.getAbsoluteLeft() + root.getOffsetWidth() - dividers.get(i).getAbsoluteLeft();
            } else {
                offset = dividers.get(i).getAbsoluteLeft() - root.getAbsoluteLeft();
            }

            if (widths.get(columns.get(i)) != -1) {
                setColumnWidth(i, offset - lastColumnEnd);
            } else {
                headersMap.get(columns.get(i)).setWidth(headersTable.getFlexCellFormatter().getElement(0, i).getOffsetWidth());
            }

            lastColumnEnd = offset;
        }
        if (widths.get(columns.get(columns.size() - 1)) != -1) {
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                setColumnWidth(columns.size() - 1, lastColumnEnd - root.getAbsoluteLeft());
            } else {
                setColumnWidth(columns.size() - 1, table.getOffsetWidth() - lastColumnEnd);
            }
        } else {
            headersMap.get(columns.get(columns.size() - 1)).setWidth(headersTable.getFlexCellFormatter().getElement(0, columns.size() - 1).getOffsetWidth());
        }
    }

    /**
     * Создает разделитель с необходимыми событиями
     */
    private ArtaFlowPanel createDivider() {
        final ArtaFlowPanel divider = new ArtaFlowPanel();
        divider.setStyleName(SynergyComponents.resources.cssComponents().tableDivider());

        divider.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
                event.preventDefault();
                resizing = true;
                Event.setCapture(divider.getElement());
                divider.addStyleName(SynergyComponents.resources.cssComponents().drag());
                RootPanel.get().getElement().getStyle().setCursor(Style.Cursor.COL_RESIZE);
            }
        });
        divider.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (resizing) {
                    int index = dividers.indexOf(divider);

                    Element previousTd = table.getFlexCellFormatter().getElement(0, index);
                    Element nextTd = table.getFlexCellFormatter().getElement(0, index + 1);

                    int leftMax;
                    int rightMax;
                    if (LocaleInfo.getCurrentLocale().isRTL()) {
                        leftMax = nextTd.getAbsoluteLeft() + columns.get(index + 1).getMinWidth();
                        rightMax = previousTd.getAbsoluteLeft() + previousTd.getOffsetWidth() - columns.get(index).getMinWidth();
                    } else {
                        leftMax = previousTd.getAbsoluteLeft() + columns.get(index).getMinWidth();
                        rightMax = nextTd.getAbsoluteLeft() + nextTd.getOffsetWidth() - columns.get(index + 1).getMinWidth();
                    }

                    int x = event.getClientX();
                    x = Math.max(x, leftMax);
                    x = Math.min(x, rightMax);

                    divider.getElement().getStyle().setLeft(x - root.getAbsoluteLeft(), Style.Unit.PX);
                }
            }
        });
        divider.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                Event.releaseCapture(divider.getElement());
                resizing = false;
                divider.removeStyleName(SynergyComponents.resources.cssComponents().drag());
                RootPanel.get().getElement().getStyle().clearCursor();
                resizeToDividers();
            }
        });

        return divider;
    }

    public Table(int pageSize) {
        this(pageSize, null);
    }

    /**
     * @param pageSize количество объектов на одной странице
     * @param keyProvider предоставляет ключи для объекта таблицы
     */
    public Table(int pageSize, ProvidesKey<T> keyProvider) {
        root = new FlowPanel();
        initWidget(root);
        root.addStyleName(SynergyComponents.resources.cssComponents().tableWhole());

        this.keyProvider = keyProvider;
        selectionModel = new TableSelectionModel<T>(bus, keyProvider);

        headersTable = new FlexTable();
        headersTable.setStyleName(SynergyComponents.resources.cssComponents().headersTable());
        root.add(headersTable);

        tableContainer = new ArtaScrollPanel();
//        tableContainer.setStyleName(SynergyComponents.resources.cssComponents().tableContainer());

        table = new FlexTable();
        table.addStyleName(SynergyComponents.resources.cssComponents().table());
        for (int i = 0; i < pageSize; i++) {
            table.insertRow(0);
        }
        tableContainer.setWidget(table);
        root.add(tableContainer);

        this.pageSize = pageSize;
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
                enableBorder(event, false);
            }

            @Override
            public void onCancel(CellEditEvent<T> event) {
                enableBorder(event, false);
            }

            @Override
            public void onEdit(CellEditEvent<T> event) {
                enableBorder(event, true);
            }

            private void enableBorder(CellEditEvent<T> event, boolean enable) {
                int row = objects.indexOf(event.getObject());
                int column = columns.indexOf(event.getColumn());
                Element td = table.getFlexCellFormatter().getElement(row, column);
                Element tdUnder = null;
                if (row + 1 < objects.size()) {
                    tdUnder = table.getFlexCellFormatter().getElement(row + 1, column);
                }
                if (enable) {
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
                    if (enable) {
                        td.getStyle().setHeight(25, Style.Unit.PX);
                    } else {
                        td.getStyle().clearHeight();
                    }
                }

                if (event.jumpForward()) {
                    selectNextCell();
//                    selectionModel.setSelected(selectedObject, getNextEditableColumn(), true);
                }
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
     * Возвращает только положительный результат модуля в
     * отличии от %
     * @param value значение
     * @param mod модуль
     */
    private int positiveMod(int value, int mod) {
        if (value < 0) {
            value += mod * (-value / mod + 1);
        }
        return value % mod;
    }

    /**
     * Возвращает соседний видимый объект.
     * @param startObject объект, начиная с которого (не включая) начинается поиск
     * @param forward true - следующий, false - предыдущий
     * @return соседний видимый объект
     */
    private T getNextVisibleObject(T startObject, boolean forward) {
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
    private void innerSelect(int row, int column, boolean select) {
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
        innerSelect(selectedRow, selectedColumn, false);

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
        innerSelect(selectedRow, selectedColumn, true);
    }

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
            }
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
    private int getPreviousEditableColumn(int startColumn) {
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
        for (int i = startColumn + 1; i < columns.size(); i++) {
            ArtaColumn<T, ?> column = columns.get(i);
            if (column.isEditable()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Действия при начале перетаскивания стоблца
     */
    private void startDragging(int x, int y) {
        Event.setCapture(headerProxy.getElement());
        headerProxy.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        isDragging = true;

        borderLocations.clear();
        int i = 0;
        while (i < columns.size()) {
            Header header = headersMap.get(columns.get(i));
            if (header == headerToMove) {
                i += 2;
                continue;
            }
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                borderLocations.add(header.getAbsoluteLeft() + header.getOffsetWidth());
            } else {
                borderLocations.add(header.getAbsoluteLeft());
            }
            i++;
        }
        Header lastHeader = headersMap.get(columns.get(columns.size() - 1));
        if (lastHeader != headerToMove) {
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                borderLocations.add(lastHeader.getAbsoluteLeft());
            } else {
                borderLocations.add(lastHeader.getAbsoluteLeft() + lastHeader.getOffsetWidth());
            }
        }
        oldX = x;

        drag(x, y);
    }

    /**
     * Возвращает указатель новой позиции стобца при завершении перетаскивания
     */
    private FlowPanel getHeaderDivider() {
        if (headerDivider == null) {
            headerDivider = new FlowPanel();
            headerDivider.addStyleName(SynergyComponents.resources.cssComponents().headerDivider());
            root.add(headerDivider);
        }
        return headerDivider;
    }

    /**
     * Показывает указатель нового положения столбца
     * @param left расстояние от левого края экрана
     * @param top расстояние от верхнего края экрана
     * @param width ширина
     */
    private void showHeaderDivider(int left, int top, int width) {
        getHeaderDivider().getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        getHeaderDivider().getElement().getStyle().setLeft(left, Style.Unit.PX);
        getHeaderDivider().getElement().getStyle().setTop(top, Style.Unit.PX);
        getHeaderDivider().getElement().getStyle().setWidth(width, Style.Unit.PX);
    }

    /**
     * Скрывает указатель нового положения столбца
     */
    private void hideHeaderDivider() {
        getHeaderDivider().getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        selectedHeadersBorder = -1;
    }

    /**
     * Действия при перетаскивании заголовка
     * @param x x координата мыши
     * @param y y координата мыши
     */
    private void drag(int x, int y) {
        headerProxy.getElement().getStyle().setLeft(x + 10, Style.Unit.PX);
        headerProxy.getElement().getStyle().setTop(y + 10, Style.Unit.PX);

        int borderToSelect;

        if (x > headerToMove.getAbsoluteLeft() && x < headerToMove.getAbsoluteLeft() + headerToMove.getOffsetWidth()) {
            hideHeaderDivider();
            return;
        }
        if (oldX >= x) {
            SortedSet<Integer> headSet = borderLocations.headSet(x);
            if (headSet.isEmpty()) {
                borderToSelect = borderLocations.first();
            } else {
                borderToSelect = headSet.last();
            }
        } else {
            SortedSet<Integer> tailSet = borderLocations.tailSet(x);
            if (tailSet.isEmpty()) {
                borderToSelect = borderLocations.last();
            } else {
                borderToSelect = tailSet.first();
            }
        }
        oldX = x;

        Header firstHeader = headersMap.get(columns.get(0));
        Header lastHeader = headersMap.get(columns.get(columns.size() - 1));

        selectedHeadersBorder = borderToSelect;

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            if (borderToSelect != lastHeader.getAbsoluteLeft()) {
                if (borderToSelect == firstHeader.getAbsoluteLeft() + firstHeader.getOffsetWidth()) {
                    borderToSelect -= HEADER_DIVIDER_WIDTH;
                } else {
                    borderToSelect -= HEADER_DIVIDER_WIDTH / 2 + 1;
                }
            }
        } else {
            if (borderToSelect != firstHeader.getAbsoluteLeft()) {
                if (borderToSelect == lastHeader.getAbsoluteLeft() + lastHeader.getOffsetWidth()) {
                    //последняя граница
                    borderToSelect -= HEADER_DIVIDER_WIDTH;
                } else {
                    //обычная граница
                    borderToSelect -= HEADER_DIVIDER_WIDTH / 2 + 1;
                }
            }
        }

        showHeaderDivider(borderToSelect, headerToMove.getAbsoluteTop(), HEADER_DIVIDER_WIDTH);
    }

    /**
     * Переместить виджеты из одного стоблца в другой
     * @param table таблица
     * @param dest куда поместить виджеты
     * @param source откуда взять виджеты
     */
    private static void setColumnWidgets(FlexTable table, int dest, int source) {
        for (int row = 0; row < table.getRowCount(); row++) {
            table.setWidget(row, dest, table.getWidget(row, source));
        }
    }

    /**
     * Заменить виджеты столбца на указанной позиции
     * @param table таблица
     * @param index позиция столбца
     * @param widgets виджеты
     */
    private void setColumnWidgets(FlexTable table, int index, List<Widget> widgets) {
        for (int row = 0; row < table.getRowCount(); row++) {
            table.setWidget(row, index, widgets.get(row));
        }
    }

    /**
     * Замена Collections.rotate
     * @param list лист
     */
    private static <V> void rotate(List<V> list, boolean forward) {
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
     * На самом деле здесь происходит перемещение виджетов и перемещение свойства ширина
     * у первого ряда таблиц заголовков и главной таблицы.
     * Столбец перемещается на новую позицию, остальные смещаются в нужную сторону.
     * Очень похоже на Collections.rotate
     *
     * @param columnIndex позиция на которой находится столбец
     * @param targetPosition позиция на которую надо переместить столбец
     */
    private void changeColumnPosition(int columnIndex, int targetPosition) {
        ArrayList<Widget> columnWidgets = new ArrayList<Widget>();
        Widget headerWidget = headersTable.getWidget(0, columnIndex);

        for (int i = 0; i < table.getRowCount(); i++) {
            columnWidgets.add(table.getWidget(i, columnIndex));
        }
        if (targetPosition < columnIndex) {
            for (int col = columnIndex; col > targetPosition; col--) {
                setColumnWidgets(table, col, col - 1);
                headersTable.setWidget(0, col, headersTable.getWidget(0, col - 1));
            }
            setColumnWidgets(table, targetPosition, columnWidgets);
            headersTable.setWidget(0, targetPosition, headerWidget);
        } else if (targetPosition > columnIndex) {
            for (int col = columnIndex; col < targetPosition; col++) {
                setColumnWidgets(table, col, col + 1);
                headersTable.setWidget(0, col, headersTable.getWidget(0, col + 1));
            }
            setColumnWidgets(table, targetPosition, columnWidgets);
            headersTable.setWidget(0, targetPosition, headerWidget);
        }
    }

    /**
     * Действия при завершении перетаскивания столбца.
     */
    private void stopDragging() {
        isDragging = false;
        headerProxy.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        Event.releaseCapture(headerProxy.getElement());

        if (selectedHeadersBorder == -1) {
            //случай, когда заголовок никуда не перенесли
            return;
        }
        int targetColumn = -1;
        int srcColumn = -1;

        for (ArtaColumn<T, ?> column : columns) {
            if (headersMap.get(column) == headerToMove) {
                srcColumn = columns.indexOf(column);
                break;
            }
        }

        for (int i = 0; i < columns.size(); i++) {
            Header header = headersMap.get(columns.get(i));
            if (LocaleInfo.getCurrentLocale().isRTL() && header.getAbsoluteLeft() + header.getOffsetWidth() == selectedHeadersBorder
                    || !LocaleInfo.getCurrentLocale().isRTL() && header.getAbsoluteLeft() == selectedHeadersBorder) {
                targetColumn = i;
                if (i > srcColumn) {
                    targetColumn--;
                }
                break;
            }
        }

        if (targetColumn == -1) {
            targetColumn = columns.size() - 1;
        }
        changeColumnPosition(srcColumn, targetColumn);

        if (targetColumn < srcColumn) {
            rotate(columns.subList(targetColumn, srcColumn + 1), true);
        } else {
            rotate(columns.subList(srcColumn, targetColumn + 1), false);
        }
        updateTableWidths();

        hideHeaderDivider();
        redrawDividers();
    }

    /**
     * Обновляет таблицу в соответствии с логическими значениями ширины столбцов
     */
    private void updateTableWidths() {
        for (int i = 0; i < columns.size(); i++) {
            ArtaColumn<T, ?> column = columns.get(i);
            Style tdStyle = table.getFlexCellFormatter().getElement(0, i).getStyle();
            Style headerTdStyle = headersTable.getFlexCellFormatter().getElement(0, i).getStyle();
            if (widths.get(column) != -1) {
                tdStyle.setWidth(widths.get(column), Style.Unit.PX);
                headerTdStyle.setWidth(widths.get(column), Style.Unit.PX);
            } else {
                tdStyle.clearWidth();
                headerTdStyle.clearWidth();
            }
        }
    }

    /**
     * Инициализирует заголовок, который появляется при начале перемещения.
     * @param header заголовок, который надо перетаскиваться
     */
    private void initMovableHeader(final Header header) {
        if (headerProxy == null) {
            headerProxy = new Header(header.getText());
            RootPanel.get().add(headerProxy);
            headerProxy.getElement().getStyle().setOpacity(0.6);
            headerProxy.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
            headerProxy.getElement().getStyle().setPosition(Style.Position.FIXED);

            headerProxy.addMouseMoveHandler(new MouseMoveHandler() {
                @Override
                public void onMouseMove(MouseMoveEvent event) {
                    if (isDragging) {
                        event.preventDefault();
                        event.stopPropagation();
                        drag(event.getClientX(), event.getClientY());
                    }
                }
            });
            headerProxy.addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event) {
                    if (isDragging) {
                        event.preventDefault();
                        event.stopPropagation();
                        stopDragging();
                    }
                }
            });
        }
        headerProxy.setText(header.getText());
        headerProxy.setSorted(header.isSorted(), header.isAscending());
        headerProxy.getElement().getStyle().setWidth(header.getOffsetWidth(), Style.Unit.PX);
    }

    /**
     * Добавляет столбец
     * @param column столбец
     * @param headerText текст соответствующего заголовка
     */
    public void addColumn(final ArtaColumn<T, ?> column, String headerText) {
        columns.add(column);
        for (int i = 0; i < Math.min(table.getRowCount(), objects.size()); i++) {
            table.addCell(i);
            int cellColumn = table.getCellCount(i) - 1;
            table.setWidget(i, cellColumn, column.createWidget(objects.get(cellColumn), bus));
        }

        final Header header = new Header(headerText);
        header.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
                event.stopPropagation();

                headerMouseDown = true;
            }
        });
        header.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (headerMouseDown) {
                    event.preventDefault();
                    event.stopPropagation();

                    headerToMove = header;
                    initMovableHeader(header);
                    startDragging(event.getClientX(), event.getClientY());
                    headerMouseDown = false;
                }
            }
        });
        headersTable.setWidget(0, columns.size() - 1, header);
        headersMap.put(column, header);
        if (column.isSortable()) {
            header.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (!headerMouseDown) {
                        return;
                    }
                    headerMouseDown = false;
                    if (sortedColumn != column) {
                        if (sortedColumn != null) {
                            headersMap.get(sortedColumn).setSorted(false);
                        }
                    }
                    header.setSorted(true);
                    sortedColumn = column;
                    bus.fireEventFromSource(new TableSortEvent<T>(column, header.isAscending()), Table.this);
                }
            });
        } else {
            header.addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event) {
                    headerMouseDown = false;
                }
            });
        }
    }

    @Override
    public SelectionModel<? super T> getSelectionModel() {
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
                visibleRange.getStart() + visibleRange.getLength() - 1);
    }

    public void setRow(int row, T value) {

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

    @Override
    public void setRowData(int start, List<? extends T> values) {
        for (T value : values) {
            objects.set(start++, value);
        }
        redraw();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setSelectionModel(SelectionModel<? super T> selectionModel) {
        if (!(selectionModel instanceof TableSelectionModel)) {
            throw new IllegalArgumentException();
        }
        this.selectionModel = (TableSelectionModel) selectionModel;
    }

    @Override
    public void setVisibleRangeAndClearData(final Range range, boolean forceRangeChangeEvent) {
        boolean changed = false;
        if (start != range.getStart()) {
            start = range.getStart();
            changed = true;
        }
        if (pageSize != range.getLength()) {
            pageSize = range.getLength();
            changed = true;
        }

        if (changed || forceRangeChangeEvent) {
            RangeChangeEvent.fire(this, range);
        }
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

    private boolean isRowCountExact;

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

    @Override
    public void setRowCount(int count) {
        if (objects.size() < count) {
            objects.ensureCapacity(count);
            for (int i = objects.size(); i < count; i++) {
                objects.add(null);
            }
        } else if (objects.size() > count) {
            objects.subList(count - 1, objects.size()).clear();
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
        setVisibleRangeAndClearData(range, false);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
    }

    public void addSortHandler(TableSortEvent.Handler<T> handler) {
        bus.addHandlerToSource(TableSortEvent.TYPE, this, handler);
    }

    /**
     * Высота задается через setHeight(int)
     */
    @Override
    public void setHeight(String height) {
        throw new UnsupportedOperationException();
    }

    public void setHeight(int height) {
        isHeightSet = true;
        this.wholeTableHeight = height;
        root.getElement().getStyle().setHeight(height, Style.Unit.PX);
        if (isAttached()) {
            redraw();
        }
    }

    public void clearHeight(int height) {
        isHeightSet = false;
        this.wholeTableHeight = height;
        root.getElement().getStyle().clearHeight();
        if (isAttached()) {
            redraw();
        }
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
     * Возвращает номер ряда в котором расположен объект с
     * заданным ключем.
     * @param key ключ
     * @return номер ряда с объектом
     */
    public int getRowById(Object key) {
        for (int i = start; i < Math.min(objects.size() - start, pageSize); i++) {
            if (keyProvider.getKey(objects.get(i)).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public TableHat getHat() {
        return hat;
    }

    public boolean hasHat() {
        return hat != null && hat.isAttached();
    }

    public void enableHat(boolean enabled) {
        if (enabled) {
            if (hat == null) {
                hat = new TableHat(this);
            }
            root.insert(hat, 0);
        } else {
            if (hat != null) {
                hat.removeFromParent();
            }
        }

        for (FlowPanel divider : dividers) {
            Style dividerStyle = divider.getElement().getStyle();
            if (hasHat()) {
                dividerStyle.setTop(40, Style.Unit.PX);
            } else {
                dividerStyle.setTop(0, Style.Unit.PX);
            }
        }
    }
}
