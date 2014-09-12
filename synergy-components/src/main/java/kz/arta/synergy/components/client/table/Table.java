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
    private ArrayList<T> visibleObjects;

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
    private TableSelection selectionModel;

    /**
     * Выбранный ряд
     */
    private int selectedRow = -1;

    /**
     * Выбранный столбец
     */
    private int selectedColumn = -1;

    /**
     * Начало отображаемого множества объектов
     */
    private int start;

    /**
     * Количество объектов на странице
     */
    private int pageSize;

    /**
     * Переривывает таблицу
     */
    public void redraw() {
        for (int i = 0; i < pageSize; i++) {
            setRow(i, visibleObjects.get(start + i));
        }
        if (table.getRowCount() > pageSize) {
            for (int i = pageSize; i < table.getRowCount(); i++) {
                table.getRowFormatter().getElement(visibleObjects.size()).removeFromParent();
            }
        }
        table.getRowFormatter().getElement(pageSize - 1).addClassName(SynergyComponents.resources.cssComponents().last());
        redrawDividers();
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
            dividers.get(dividersCount++).getElement().getStyle().setLeft(absoluteLeft, Style.Unit.PX);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        for (int i = 0; i < columns.size(); i++) {
            widths.put(columns.get(i), table.getFlexCellFormatter().getElement(0, i).getOffsetWidth());
        }
        widths.put(columns.get(columns.size() - 1), -1);
        redrawDividers();
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
//                divider.getElement().getStyle().clearBackgroundColor();
                RootPanel.get().getElement().getStyle().clearCursor();
                resizeToDividers();

                int sum = 0;
                for (int i = 0; i < table.getCellCount(0); i++) {
                    int tdWidth = table.getFlexCellFormatter().getElement(0, i).getOffsetWidth();
                    System.out.print(tdWidth + ":" + widths.get(columns.get(i)) + " ");
                    sum += tdWidth;
                }
                System.out.println(" " + sum);
            }
        });

        return divider;
    }

    /**
     * @param pageSize количество объектов на одной странице
     */
    public Table(int pageSize) {
        root = new FlowPanel();
        initWidget(root);
        root.addStyleName(SynergyComponents.resources.cssComponents().tableWhole());

        headersTable = new FlexTable();
        headersTable.setStyleName(SynergyComponents.resources.cssComponents().headersTable());
        root.add(headersTable);

        FocusPanel tableContainer = new FocusPanel();
        tableContainer.setStyleName(SynergyComponents.resources.cssComponents().tableContainer());

        table = new FlexTable();
        table.addStyleName(SynergyComponents.resources.cssComponents().table());
        for (int i = 0; i < pageSize; i++) {
            table.insertRow(0);
        }
        tableContainer.setWidget(table);
        root.add(tableContainer);

        this.pageSize = pageSize;
        columns = new ArrayList<ArtaColumn<T, ?>>();
        visibleObjects = new ArrayList<T>(pageSize);

        table.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                HTMLTable.Cell cell = table.getCellForEvent(event);
                select(cell.getRowIndex(), cell.getCellIndex());
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
                int row = visibleObjects.indexOf(event.getObject());
                int column = columns.indexOf(event.getColumn());
                Element td = table.getFlexCellFormatter().getElement(row, column);
                Element tdUnder = null;
                if (row + 1 < visibleObjects.size()) {
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
            }
        });

        sinkEvents(Event.ONCLICK);
        sinkEvents(Event.ONKEYDOWN);
        sinkEvents(Event.ONKEYUP);
        sinkEvents(Event.ONBLUR);
        sinkEvents(Event.ONFOCUS);
    }

    /**
     * Выделяет ячейку
     * @param row номер ряда
     * @param column номер столбца
     */
    public void select(int row, int column) {
        if (row < visibleObjects.size() && row >= 0
                && column < columns.size() && column >= 0) {
            row = row % pageSize;
            if (selectedRow != -1 && selectedColumn != -1) {
                table.getWidget(selectedRow, selectedColumn).getElement().blur();
                table.getFlexCellFormatter().getElement(selectedRow, selectedColumn).
                        removeClassName(SynergyComponents.resources.cssComponents().selected());
            }
            selectedRow = row;
            selectedColumn = column;

            table.getRowFormatter().getElement(row).addClassName(SynergyComponents.resources.cssComponents().selected());
            table.getFlexCellFormatter().getElement(row, column).addClassName(SynergyComponents.resources.cssComponents().selected());

            table.getWidget(row, column).getElement().focus();
            selectionModel.setSelected(visibleObjects.get(start + row), true);
        }
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
                    if (selectedRow == visibleObjects.size() - 1) {
                        return;
                    }
                    select(selectedRow + 1, selectedColumn);
                    break;
                case KeyCodes.KEY_UP:
                    event.preventDefault();
                    if (selectedRow == 0) {
                        return;
                    }
                    select(selectedRow - 1, selectedColumn);
                    break;
                case KeyCodes.KEY_RIGHT:
                    select(selectedRow, getNextEditableColumn(selectedColumn));
                    break;
                case KeyCodes.KEY_LEFT:
                    select(selectedRow, getPreviousEditableColumn(selectedColumn));
                    break;
            }
        }
    }

    /**
     * Возвращает первый столбец после заданной позиции, который можно изменять.
     * @param column номер столбца
     * @return номер следующего изменяемого столбца
     */
    public int getPreviousEditableColumn(int column) {
        for (int i = column - 1; i >= 0; i--) {
            if (columns.get(i).isEditable()) {
                return i;
            }
        }
        if (column != columns.size() - 1) {
            for (int i = columns.size() - 1; i > column; i--) {
                if (columns.get(i).isEditable()) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Возвращает последний слобец до заданной позиции, который можно изменять.
     * @param column позиция
     * @return номер предыдущего изменяемого стоблца
     */
    public int getNextEditableColumn(int column) {
        for (int i = column + 1; i < columns.size(); i++) {
            if (columns.get(i).isEditable()) {
                return i;
            }
        }
        if (column != 0) {
            for (int i = 0; i < column; i++) {
                if (columns.get(i).isEditable()) {
                    return i;
                }
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

        //на всякий случай этот кусок кода оставляю
        //перетаскивание как в sencha

//        boolean nearHeader = false;
//        int i = 0;
//        while (i < columns.size()) {
//            Header header = headersMap.get(columns.get(i));
//            if (header == headerToMove) {
//                i += 2;
//                continue;
//            }
//            if (near(header.getAbsoluteLeft(), x, HEADER_DIVIDER_WIDTH)) {
//                nearHeader = true;
//                int left = header.getAbsoluteLeft();
//                if (i > 0) {
//                    left -= HEADER_DIVIDER_WIDTH / 2 + 1;
//                }
//
//                showHeaderDivider(left, header.getAbsoluteTop(), HEADER_DIVIDER_WIDTH);
//                break;
//            }
//            i++;
//        }
//        Header lastHeader = headersMap.get(columns.get(columns.size() - 1));
//        if (lastHeader != headerToMove) {
//            if (near(lastHeader.getAbsoluteLeft() + lastHeader.getOffsetWidth(), x, HEADER_DIVIDER_WIDTH)) {
//                showHeaderDivider(lastHeader.getAbsoluteLeft() + lastHeader.getOffsetWidth() - HEADER_DIVIDER_WIDTH,
//                        lastHeader.getAbsoluteTop(), HEADER_DIVIDER_WIDTH);
//                nearHeader = true;
//            }
//        }
//        if (!nearHeader) {
//            hideHeaderDivider();
//        }
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
        for (int i = 0; i < Math.min(table.getRowCount(), visibleObjects.size()); i++) {
            table.addCell(i);
            int cellColumn = table.getCellCount(i) - 1;
            table.setWidget(i, cellColumn, column.createWidget(visibleObjects.get(cellColumn), bus));
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

    private class TableSelection implements SelectionModel<T> {
        @Override
        public HandlerRegistration addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSelected(T object) {
            return object == visibleObjects.get(selectedRow);
        }

        @Override
        public void setSelected(T object, boolean selected) {
            if (visibleObjects.contains(object)) {
                int row = visibleObjects.indexOf(object);
                if (selected && row != selectedRow) {
                    selectedRow = row;
                }
                if (!selected && row == selectedRow) {
                    selectedRow = -1;
                }
            }
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            bus.fireEvent(event);
        }

        @Override
        public Object getKey(T item) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public SelectionModel<? super T> getSelectionModel() {
        if (selectionModel == null) {
            selectionModel = new TableSelection();
        }
        return selectionModel;
    }

    @Override
    public T getVisibleItem(int indexOnPage) {
        return visibleObjects.get(getVisibleRange().getStart() + indexOnPage);
    }

    @Override
    public int getVisibleItemCount() {
        return getVisibleRange().getLength();
    }

    @Override
    public Iterable<T> getVisibleItems() {
        Range visibleRange = getVisibleRange();
        return visibleObjects.subList(visibleRange.getStart(),
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
            visibleObjects.set(start++, value);
        }
        redraw();
    }

    @Override
    public void setSelectionModel(SelectionModel<? super T> selectionModel) {
        throw new UnsupportedOperationException();
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
        return visibleObjects.size();
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
        if (visibleObjects.size() < count) {
            visibleObjects.ensureCapacity(count);
            for (int i = visibleObjects.size(); i < count; i++) {
                visibleObjects.add(null);
            }
        } else if (visibleObjects.size() > count) {
            visibleObjects.subList(count - 1, visibleObjects.size()).clear();
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
}
