package kz.arta.synergy.components.client.table;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ProvidesKey;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.table.column.ArtaColumn;
import kz.arta.synergy.components.client.table.events.TableSortEvent;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 02.09.14
 * Time: 11:50
 *
 * Таблица
 */
public class Table<T> extends Composite {
    /**
     * Ширина разделительной линии, в хэдерах, при перетаскивании столбцов
     * Очень желательно, чтобы эта величина была нечетной
     */
    private static final int HEADER_DIVIDER_WIDTH = 11;

    /**
     * Ширина интервала дропа для заголовка.
     * Вне этого интервала дроп не будет срабатывать и
     * индикатор не будет появляться
     */
    public static final double HEADER_DROP_SIZE = 0.25;

    private EventBus bus = new SimpleEventBus();

    /**
     * Корневая панель таблицы
     */
    private FlowPanel root;

    /**
     * Таблица заголовков
     */
    private final FlexTable headersTable;

    /**
     * Невидимые разделители для изменения ширины столбцов
     */
    private List<ArtaFlowPanel> dividers = new ArrayList<ArtaFlowPanel>();

    /**
     * Самая левая позиция для перемещения разделителя.
     * Определяется минимальной шириной столбца слева от разделителя
     */
    private int leftDividerLimit;
    private int rightDividerLimit;

    /**
     * Начальная позиция разделителя до drag'а
     */
    private int oldDividerPosition;

    /**
     * Производится ли изменение ширины столбцов в данный момент
     */
    private boolean resizing = false;

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
     * Столбец, который перемещается
     */
    private ArtaColumn<T, ?> movingColumn;

    /**
     * Позиция на которую будет перемещен столбец при drop'е
     */
    private int targetPosition;

    /**
     * Указатель новой позиции стоблца при завершении перетаскивания
     */
    private FlowPanel headerDivider;

    /**
     * Задана ли высота. Если не задана, то таблица растягивается
     */
    private boolean isHeightSet = false;

    /**
     * Заданная высота
     */
    private int wholeTableHeight;

    /**
     * Шапка
     */
    private TableHat hat;

    /**
     * Таблица
     */
    private TableCore<T> tableCore;

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

        tableCore = new TableCore<T>(pageSize, keyProvider, bus);

        headersTable = new FlexTable();
        headersTable.setStyleName(SynergyComponents.resources.cssComponents().headersTable());
        root.add(headersTable);

        root.add(tableCore);
    }

    /**
     * Перерисовывает таблицу
     */
    public void redraw() {
        tableCore.redraw();

        if (isHeightSet) {
            int tableHeight = wholeTableHeight;
            if (hasHat()) {
                tableHeight -= 40; //шапка
            }
            tableHeight -= 32; //хедеры
            tableCore.setHeight(tableHeight);
        }
        redrawDividers();
    }

    /**
     * Изменяет количество и положение разделителей в соответствии со столбцами
     */
    public void redrawDividers() {
        if (dividers.size() != tableCore.getColumns().size() - 1) {
            while (dividers.size() > tableCore.getColumns().size() - 1) {
                ArtaFlowPanel divider = dividers.get(tableCore.getColumns().size());
                divider.removeFromParent();
                dividers.remove(divider);
            }
            while (dividers.size() < tableCore.getColumns().size() - 1) {
                ArtaFlowPanel divider = createDivider();
                dividers.add(divider);
                root.add(divider);
            }
        }

        for (int i = 0; i < tableCore.getColumns().size() - 1; i++) {
            Style dividerStyle = dividers.get(i).getElement().getStyle();

            int left = getHeaderEnd(tableCore.getColumn(i).getHeader());
            left -= root.getAbsoluteLeft();
            left -= Constants.TABLE_DIVIDER_WIDTH / 2 + 1;

            dividerStyle.setLeft(left, Style.Unit.PX);
            if (hasHat()) {
                dividerStyle.setTop(40, Style.Unit.PX);
            } else {
                dividerStyle.setTop(0, Style.Unit.PX);
            }
        }
    }

    /**
     * При загрузке инициализируется ширина всех столбцов, кроме последнего
     */
    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                tableCore.initWidths();
                for (int i = 0; i < tableCore.getColumns().size(); i++) {
                    updateHeaderWidth(i);
                }
                redraw();
            }
        });
    }

    /**
     * Обновляет ширину заголовка на заданной позиции
     * Ширина полностью зависит от соответствующего столбца внутренней таблицы.
     *
     * Ширина виджета заголовка задается вне зависимости от того задана ли ширина столбца.
     * Это необходимо для правильного отображения градиента.
     * @param index позиция заголовка
     */
    private void updateHeaderWidth(int index) {
        Style tdstyle = headersTable.getFlexCellFormatter().getElement(0, index).getStyle();
        //ширина элемента td
        if (tableCore.columnHasWidth(index)) {
            tdstyle.setWidth(tableCore.getColumnWidth(index), Style.Unit.PX);
        } else {
            tdstyle.clearWidth();
        }

        //ширина виджета Header
        tableCore.getColumns().get(index).getHeader().
                setWidth(headersTable.getFlexCellFormatter().getElement(0, index).getOffsetWidth());
    }

    /**
     * Изменяет ширину столбца на заданной позиции
     * @param index позиция
     * @param width ширина, если -1, то свойство width убирается и столбец растягивается
     */
    private void setColumnWidth(int index, int width) {
        tableCore.setColumnWidth(index, width);

        //при обновлении ширины столбца необходимо обновить ширину у всех виджетов заголовка,
        //у которых ширина не указана явна, потому что она изменится
        for (int i = 0; i < tableCore.getColumns().size(); i++) {
            if (!tableCore.columnHasWidth(i) || index == i) {
                updateHeaderWidth(i);
            }
        }
    }

    /**
     * Начало drag'а разделителя
     * @param divider разделитель
     */
    private void startResizing(ArtaFlowPanel divider) {
        resizing = true;
        divider.addStyleName(SynergyComponents.resources.cssComponents().drag());
        RootPanel.get().getElement().getStyle().setCursor(Style.Cursor.COL_RESIZE);

        int index = dividers.indexOf(divider);

        oldDividerPosition = divider.getAbsoluteLeft();

        if (LocaleInfo.getCurrentLocale().isRTL()) {
            ArtaColumn<T, ?> leftColumn = tableCore.getColumns().get(index + 1);
            ArtaColumn<T, ?> rightColumn = tableCore.getColumns().get(index);
            leftDividerLimit = getHeaderEnd(leftColumn.getHeader()) + leftColumn.getMinWidth();
            rightDividerLimit = getHeaderStart(rightColumn.getHeader()) - rightColumn.getMinWidth();
        } else {
            ArtaColumn<T, ?> leftColumn = tableCore.getColumns().get(index);
            ArtaColumn<T, ?> rightColumn = tableCore.getColumns().get(index + 1);
            leftDividerLimit = getHeaderStart(leftColumn.getHeader()) + leftColumn.getMinWidth();
            rightDividerLimit = getHeaderEnd(rightColumn.getHeader()) - rightColumn.getMinWidth();
        }
    }

    /**
     * Перемещение разделителя
     * @param divider разделитель
     * @param x абсолютная x-координата
     */
    private void resizingDrag(ArtaFlowPanel divider, int x) {
        if (resizing) {
            int left = Math.max(x, leftDividerLimit);
            left = Math.min(left, rightDividerLimit);
            divider.getElement().getStyle().setLeft(left - root.getAbsoluteLeft(), Style.Unit.PX);
        }
    }

    /**
     * Drop разделителя, завершение изменения ширины.
     * @param divider разделитель
     */
    private void stopResizing(ArtaFlowPanel divider) {
        resizing = false;
        divider.removeStyleName(SynergyComponents.resources.cssComponents().drag());
        RootPanel.get().getElement().getStyle().clearCursor();

        int index = dividers.indexOf(divider);

        int delta = divider.getAbsoluteLeft() - oldDividerPosition;
        oldDividerPosition = divider.getAbsoluteLeft();

        int leftIndex = index;
        int rightIndex = index;
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            leftIndex++;
        } else {
            rightIndex++;
        }

        setColumnWidth(leftIndex, tableCore.getElement(0, leftIndex).getOffsetWidth() + delta);
        setColumnWidth(rightIndex, tableCore.getElement(0, rightIndex).getOffsetWidth() - delta);
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
                Event.setCapture(divider.getElement());
                startResizing(divider);
            }
        });
        divider.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                resizingDrag(divider, event.getClientX());
            }
        });
        divider.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                Event.releaseCapture(divider.getElement());
                stopResizing(divider);
            }
        });

        return divider;
    }

    /**
     * Действия при начале перетаскивания столбца
     */
    private void startDragging(int x, int y) {
        Event.setCapture(headerProxy.getElement());
        headerProxy.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        isDragging = true;

        for (int i = 0; i < tableCore.getColumns().size(); i++) {
            ArtaColumn<T, ?> column = tableCore.getColumn(i);
            if (isOverHeader(x, column)) {
                movingColumn = column;
                break;
            }
        }

        targetPosition = -1;

        drag(x, y);
    }

    /**
     * Возвращает указатель новой позиции стобца при завершении перетаскивания
     */
    private FlowPanel getHeaderDivider() {
        if (headerDivider == null) {
            headerDivider = new FlowPanel();
            headerDivider.addStyleName(SynergyComponents.resources.cssComponents().headerDivider());
            headerDivider.getElement().getStyle().setWidth(HEADER_DIVIDER_WIDTH, Style.Unit.PX);
            root.add(headerDivider);
        }
        return headerDivider;
    }

    /**
     * Показывает индикатор позиции куда будет перемещен столбец
     * @param position позиция; позиция 0 - между первым и вторым столбцами и т.д.
     */
    private void showHeaderDivider(int position) {
        int left;

        if (position == tableCore.getColumns().size()) {
            Header lastHeader = tableCore.getLastColumn().getHeader();
            left = getHeaderEnd(lastHeader);
            if (!LocaleInfo.getCurrentLocale().isRTL()) {
                left -= HEADER_DIVIDER_WIDTH;
            }
        } else if (position == 0) {
            left = getHeaderStart(tableCore.getColumn(0).getHeader());
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                left -= HEADER_DIVIDER_WIDTH;
            }
        } else {
            left = getHeaderStart(tableCore.getColumn(position).getHeader());
            left -= HEADER_DIVIDER_WIDTH / 2 + 1;
        }
        getHeaderDivider().getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        getHeaderDivider().getElement().getStyle().setLeft(left, Style.Unit.PX);
        getHeaderDivider().getElement().getStyle().setTop(headersTable.getAbsoluteTop(), Style.Unit.PX);

        targetPosition = position;
    }

    /**
     * Скрывает указатель нового положения столбца
     */
    private void hideHeaderDivider() {
        getHeaderDivider().getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        targetPosition = -1;
    }

    private boolean isOverHeader(int x, ArtaColumn<?, ?> column) {
        Header header = column.getHeader();
        return x >= header.getAbsoluteLeft() &&
                x <= (header.getAbsoluteLeft() + header.getOffsetWidth());
    }

    /**
     * Находится ли x "около" borderX.
     * Около определяется как внутри "следующего" и "предыдущего" отступа.
     * "Следующий" это правый для LTR, левый для RTL.
     * @param borderX граница
     * @param previousD предыдущий отступ
     * @param nextD следущий отступ
     */
    private boolean inside(double borderX, double previousD,
                           double nextD, double x) {
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            return x >= borderX - nextD && x <= borderX + previousD;
        } else {
            return x >= borderX - previousD && x <= borderX + nextD;
        }
    }

    /**
     * Абсолютная x-координата начала заголовка
     * @param header заголовок
     */
    private int getHeaderStart(Header header) {
        int start = header.getAbsoluteLeft();
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            start += header.getOffsetWidth();
        }
        return start;
    }

    /**
     * Абсолютная x-координата конца заголовка
     * @param header заголовок
     */
    private int getHeaderEnd(Header header) {
        int start = header.getAbsoluteLeft();
        if (!LocaleInfo.getCurrentLocale().isRTL()) {
            start += header.getOffsetWidth();
        }
        return start;
    }

    /**
     * Если мышь на позиции x, можно ли переместить заголовок на позицию i.
     */
    private boolean canBeDraggedTo(int i, int x) {
        double borderX;
        double previousD;
        double nextD;

        if (i == 0) {
            previousD = 0;
        } else {
            previousD = tableCore.getColumn(i - 1).getHeader().getOffsetWidth() * HEADER_DROP_SIZE;
        }

        if (i == tableCore.getColumns().size()) {
            borderX = getHeaderEnd(tableCore.getLastColumn().getHeader());
            nextD = 0;
        } else {
            borderX = getHeaderStart(tableCore.getColumn(i).getHeader());
            nextD = tableCore.getColumn(i).getHeader().getOffsetWidth() * HEADER_DROP_SIZE;
        }

        return inside(borderX, previousD, nextD, x);
    }

    /**
     * Действия при перетаскивании заголовка
     * @param x x координата мыши
     * @param y y координата мыши
     */
    private void drag(int x, int y) {
        //прокси заголовок начинается в правом нижнем угла мыши
        headerProxy.getElement().getStyle().setLeft(x + 10, Style.Unit.PX);
        headerProxy.getElement().getStyle().setTop(y + 10, Style.Unit.PX);

        if (isOverHeader(x, movingColumn)) {
            hideHeaderDivider();
            return;
        }

        hideHeaderDivider();

        int movingHeaderPosition = tableCore.getColumns().indexOf(movingColumn);
        for (int i = 0; i <= tableCore.getColumns().size(); i++) {
            if (i != movingHeaderPosition && i != movingHeaderPosition + 1
                    && canBeDraggedTo(i, x)) {
                showHeaderDivider(i);
                break;
            }
        }
        if (movingColumn != tableCore.getLastColumn() &&
                canBeDraggedTo(tableCore.getColumns().size(), x)) {
            showHeaderDivider(tableCore.getColumns().size());
        }

    }

    /**
     * Изменяет позицию стоблца.
     * На самом деле здесь происходит перемещение виджетов и перемещение свойства ширина
     * у первого ряда таблиц заголовков и главной таблицы.
     * Столбец перемещается на новую позицию, остальные смещаются в нужную сторону.
     *
     * @param columnIndex позиция на которой находится столбец
     * @param targetPosition позиция на которую надо переместить столбец
     * @see {@link kz.arta.synergy.components.client.table.TableCore#changeColumnPosition(com.google.gwt.user.client.ui.FlexTable, int, int)}
     */
    private void changeColumnPosition(int columnIndex, int targetPosition) {
        int target = targetPosition;
        if (columnIndex < target) {
            target--;
        }
        tableCore.changeColumnPosition(headersTable, columnIndex, target);
        tableCore.changeColumnPosition(columnIndex, target);
        redrawDividers();
    }

    /**
     * Действия при завершении перемещения столбца.
     */
    private void stopDragging() {
        isDragging = false;
        headerProxy.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        Event.releaseCapture(headerProxy.getElement());


        if (targetPosition != -1) {
            changeColumnPosition(tableCore.getColumns().indexOf(movingColumn), targetPosition);
        }
        hideHeaderDivider();
    }

    /**
     * Инициализирует заголовок, который появляется при начале перемещения.
     * @param header заголовок, который надо перемещать
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
     */
    public void addColumn(final ArtaColumn<T, ?> column) {
        tableCore.addColumn(column);

        final Header header = column.getHeader();
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

                    initMovableHeader(header);
                    startDragging(event.getClientX(), event.getClientY());
                    headerMouseDown = false;
                }
            }
        });
        headersTable.setWidget(0, tableCore.getColumns().size() - 1, header);
        header.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                headerMouseDown = false;
                if (isDragging) {
                    return;
                }
                if (column.isSortable()) {
                    tableCore.sort(column);
                    bus.fireEventFromSource(new TableSortEvent<T>(column, header.isAscending()), Table.this);
                }
            }
        });
    }

    /**
     * Высота задается через setHeight(int)
     */
    @Override
    public void setHeight(String height) {
        throw new UnsupportedOperationException();
    }

    /**
     * Обновляет высоту в соответствии с заданной или незаданной высотой
     */
    private void updateHeight() {
        if (isHeightSet) {
            int tableHeight = wholeTableHeight;
            if (hasHat()) {
                tableHeight -= 40; //шапка
            }
            tableHeight -= 32; //хедеры
            tableCore.setHeight(tableHeight);
        } else {
            tableCore.clearHeight();
        }
    }

    /**
     * Задает высоту
     * @param height высота
     */
    public void setHeight(int height) {
        isHeightSet = true;
        this.wholeTableHeight = height;
        root.getElement().getStyle().setHeight(height, Style.Unit.PX);
        updateHeight();
    }

    /**
     * Снять заданную высоту
     */
    public void clearHeight() {
        isHeightSet = false;
        root.getElement().getStyle().clearHeight();
        updateHeight();
    }

    /**
     * Возвращает внутреннюю таблицу
     */
    public TableCore<T> getCore() {
        return tableCore;
    }

    public TableHat getHat() {
        return hat;
    }

    public boolean hasHat() {
        return hat != null && hat.isAttached();
    }

    /**
     * Добавляет/удаляет шапку
     */
    public void enableHat(boolean enabled) {
        if (enabled) {
            if (hat == null) {
                hat = new TableHat(tableCore);
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

    /**
     * Включить-выключить перенос на следующую строку
     */
    public void setMultiLine(boolean multiLine) {
        tableCore.setMultiLine(multiLine);
    }
}
