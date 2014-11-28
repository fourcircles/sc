package kz.arta.synergy.components.client.table;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ProvidesKey;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.table.column.ArtaColumn;
import kz.arta.synergy.components.client.table.events.ColumnLockEvent;
import kz.arta.synergy.components.client.table.events.TableHeaderMenuEvent;
import kz.arta.synergy.components.client.table.events.TableSortEvent;
import kz.arta.synergy.components.style.client.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * User: vsl
 * Date: 02.09.14
 * Time: 11:50
 *
 * Таблица
 *
 * Если размер таблицы зависит от контейнера, то после изменения размеров контейнера необходимо вызывать методы
 * {@link #heightUpdated()} и {@link #widthUpdated()}.
 * При изменении размеров окна браузера таблица сама вызывает эти методы.
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
    private ArtaColumn<T> movingColumn;

    /**
     * Позиция на которую будет перемещен столбец при drop'е
     */
    private int targetPosition;

    /**
     * Указатель новой позиции стоблца при завершении перетаскивания
     */
    private FlowPanel headerDivider;

    /**
     * Шапка
     */
    private TableHat hat;

    /**
     * Таблица
     */
    private TableCore<T> tableCore;
    private MouseDownHandler headerMouseDownHandler;

    /**
     * Панель скролла для внутренней таблицы.
     * Разделители и заголовки не находятся в ней и поддерживаются в правильном
     * состоянии через {@link com.google.gwt.event.dom.client.ScrollHandler}
     */
    private ArtaScrollPanel scroll;

    /**
     * Сдвиг разделителей, соответствующий горизонтальному скроллу
     */
    private int dividersOffset = 0;

    /**
     * Разделители
     */
    private Map<ArtaColumn<T>, TableDivider<T>> dividers = new HashMap<ArtaColumn<T>, TableDivider<T>>();

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
        root.addStyleName(SynergyComponents.getResources().cssComponents().tableWhole());

        tableCore = new TableCore<T>(pageSize, keyProvider, bus);
        scroll = new ArtaScrollPanel();
        scroll.setWidget(tableCore);
        scroll.getElement().getStyle().setWidth(100, Style.Unit.PCT);

        final SimplePanel headersContainer = new SimplePanel();
        headersContainer.setStyleName(SynergyComponents.getResources().cssComponents().headersTableContainer());

        headersTable = new FlexTable();
        headersTable.setStyleName(SynergyComponents.getResources().cssComponents().headersTable());
        headersContainer.setWidget(headersTable);

        root.add(headersContainer);
        root.add(scroll);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                heightUpdated();
                widthUpdated();
            }
        });

        scroll.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                // сдвиг заголовков
                headersTable.getElement().getStyle().setLeft(-scroll.getHorizontalScrollPosition(), Style.Unit.PX);

                // сдвиг разделителей
                int offsetChange = dividersOffset - scroll.getHorizontalScrollPosition();
                for (TableDivider<T> divider : dividers.values()) {
                    String left = divider.getElement().getStyle().getLeft();
                    int leftPx = Integer.parseInt(left.substring(0, left.length() - 2), 10);
                    divider.getElement().getStyle().setLeft(leftPx + offsetChange, Style.Unit.PX);
                }
                dividersOffset -= offsetChange;
            }
        });
    }

    public void widthUpdated() {
        redrawDividers();
    }

    public void heightUpdated() {
        int tableHeight = getOffsetHeight();
        tableHeight -= Constants.BORDER_WIDTH * 2;
        if (hasHat()) {
            tableHeight -= hat.getOffsetHeight();
        }
        tableHeight -= headersTable.getOffsetHeight();
        scroll.getElement().getStyle().setHeight(tableHeight, Style.Unit.PX);
    }

    /**
     * Перерисовывает таблицу
     */
    public void redraw() {
        tableCore.redraw();
        heightUpdated();
    }

    public void redrawDividers() {
        for (TableDivider<T> divider : dividers.values()) {
            Element headerTd = divider.getColumn().getHeader().getElement().getParentElement();
            int left = headerTd.getAbsoluteLeft() + headerTd.getOffsetWidth() - headersTable.getAbsoluteLeft();
            left -= Constants.TABLE_DIVIDER_WIDTH / 2 + 1;

            divider.getElement().getStyle().setLeft(left, Style.Unit.PX);

            // здесь надо обновить верхнюю границу разделителей,
            // потому что они могли быть добавлены после добавления шляпы
            if (hasHat()) {
                if (hasHat()) {
                    divider.getElement().getStyle().setTop(40, Style.Unit.PX);
                } else {
                    divider.getElement().getStyle().setTop(0, Style.Unit.PX);
                }
            }
            if (tableCore.getOffsetWidth() > getOffsetWidth()) {
                divider.getElement().getStyle().setBottom(Constants.SCROLL_BAR_HEIGHT, Style.Unit.PX);
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

                int fullWidth = getOffsetWidth();
                fullWidth -= 2 * Constants.BORDER_WIDTH;

                widthUpdated();

                int unsetColumns = 0;

                for (ArtaColumn<T> column : tableCore.getColumns()) {
                    if (tableCore.columnHasWidth(column)) {
                        fullWidth -= tableCore.getColumnWidth(column);
                        if (tableCore.getColumns().indexOf(column) < tableCore.getColumns().size() - 1) {
                            // граница
                            fullWidth--;
                        }
                    } else {
                        unsetColumns++;
                    }
                }

                if (unsetColumns > 0) {
                    double unsetColumnWidth = (double) fullWidth / unsetColumns;

                    for (ArtaColumn<T> column : tableCore.getColumns()) {
                        if (!tableCore.columnHasWidth(column)) {
                            tableCore.setColumnWidth(column, (int) Math.max(column.getMinWidth(), unsetColumnWidth));
                        }
                    }
                }

                for (int i = 0; i < tableCore.getColumns().size(); i++) {
                    updateHeaderWidth(i);
                }

                redraw();
                redrawDividers();
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
        //ширина виджета Header
        int width = tableCore.getElement(0, index).getOffsetWidth();
        width--;
        tableCore.getColumns().get(index).getHeader().setWidth(width);
    }

    public void setColumnWidth(ArtaColumn<T> column, int width) {
        if (tableCore.getColumns().contains(column)) {
            setColumnWidth(tableCore.getColumns().indexOf(column), width);
        }
    }

    /**
     * Изменяет ширину столбца на заданной позиции
     * @param index позиция
     * @param width ширина, если -1, то свойство width убирается и столбец растягивается
     */
    public void setColumnWidth(int index, int width) {
        tableCore.setColumnWidth(index, width);

        //при обновлении ширины столбца необходимо обновить ширину у всех виджетов заголовка,
        //у которых ширина не указана явна, потому что она изменится
        if (isAttached()) {
            for (int i = 0; i < tableCore.getColumns().size(); i++) {
                if (!tableCore.columnHasWidth(i) || index == i) {
                    updateHeaderWidth(i);
                }
            }
        }
    }

    private void startResizing(TableDivider<T> divider) {
        if (!divider.getColumn().isResizable()) {
            return;
        }
        Event.setCapture(divider.getElement());
        divider.setResizing(true);
        RootPanel.get().getElement().getStyle().setCursor(Style.Cursor.COL_RESIZE);
    }

    private void dragResize(TableDivider<T> divider, int x) {
        if (!divider.getColumn().isResizable() || !divider.isResizing()) {
            return;
        }
        int left = x - headersTable.getAbsoluteLeft();

        int columnIndex = tableCore.getColumns().indexOf(divider.getColumn());
        int leftLimit = headersTable.getFlexCellFormatter().getElement(0, columnIndex).getAbsoluteLeft()
                - headersTable.getAbsoluteLeft() + divider.getColumn().getMinWidth();


        divider.getElement().getStyle().setLeft(Math.max(left, leftLimit), Style.Unit.PX);
    }

    private void stopResizing(TableDivider<T> divider) {
        Event.releaseCapture(divider.getElement());
        RootPanel.get().getElement().getStyle().clearCursor();

        int delta = divider.getAbsoluteLeft() - divider.getOldPosition();
        Element cellElement = tableCore.getElement(0, tableCore.getColumns().indexOf(divider.getColumn()));
        setColumnWidth(divider.getColumn(), cellElement.getOffsetWidth() + delta);

        divider.setResizing(false);

        redrawDividers();
    }

    private TableDivider<T> createDivider(ArtaColumn<T> column) {
        final TableDivider<T> divider = new TableDivider<T>(column);
        divider.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (divider.getColumn().isResizable()) {
                    event.stopPropagation();
                    event.preventDefault();
                    startResizing(divider);
                }
            }
        });

        divider.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (divider.getColumn().isResizable()) {
                    dragResize(divider, event.getClientX());
                }
            }
        });

        divider.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (divider.getColumn().isResizable()) {
                    stopResizing(divider);
                }
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
            ArtaColumn<T> column = tableCore.getColumn(i);
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
            headerDivider.addStyleName(SynergyComponents.getResources().cssComponents().headerDivider());
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

    private boolean isOverHeader(int x, ArtaColumn<T> column) {
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
    private void stopColumnDragging() {
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
                        stopColumnDragging();
                    }
                }
            });
        }
        headerProxy.setText(header.getText());
        headerProxy.setSorted(header.isSorted(), header.isAscending());
        headerProxy.getElement().getStyle().setWidth(header.getOffsetWidth(), Style.Unit.PX);
    }


    private MouseDownHandler getHeaderDownHandler() {
        if (headerMouseDownHandler == null) {
            headerMouseDownHandler = new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                        return;
                    }
                    event.preventDefault();
                    event.stopPropagation();

                    headerMouseDown = true;
                }
            };
        }
        return headerMouseDownHandler;
    }

    private MouseMoveHandler createMouseMoveHandler(final Header header) {
        return new MouseMoveHandler() {
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
        };
    }

    /**
     * Добавляет столбец
     * @param column столбец
     */
    public void addColumn(final ArtaColumn<T> column) {
        tableCore.addColumn(column);
        column.setBus(bus);

        final Header header = column.getHeader();
        headersTable.setWidget(0, tableCore.getColumns().size() - 1, header);

        header.addMouseDownHandler(getHeaderDownHandler());
        header.addMouseMoveHandler(createMouseMoveHandler(header));
        header.addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
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

        header.addDomHandler(new ContextMenuHandler() {
            @Override
            public void onContextMenu(ContextMenuEvent event) {
                event.preventDefault();
                bus.fireEventFromSource(new TableHeaderMenuEvent<T>(column,
                        event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY()), Table.this);
            }
        }, ContextMenuEvent.getType());

        final TableDivider<T> divider = createDivider(column);
        dividers.put(column, divider);
        root.add(divider);

        column.addLockHandler(new ColumnLockEvent.Handler() {
            @Override
            public void onColumnLock(ColumnLockEvent event) {
                if (event.isLocked()) {
                    divider.addStyleName(SynergyComponents.getResources().cssComponents().disabled());
                } else {
                    divider.removeStyleName(SynergyComponents.getResources().cssComponents().disabled());
                }
            }
        });
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        heightUpdated();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        widthUpdated();
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

        for (TableDivider<T> divider : dividers.values()) {
            if (hasHat()) {
                divider.getElement().getStyle().setTop(40, Style.Unit.PX);
            } else {
                divider.getElement().getStyle().setTop(0, Style.Unit.PX);
            }
        }
//        for (FlowPanel divider : dividers) {
//            Style dividerStyle = divider.getElement().getStyle();
//            if (hasHat()) {
//                dividerStyle.setTop(40, Style.Unit.PX);
//            } else {
//                dividerStyle.setTop(0, Style.Unit.PX);
//            }
//        }
    }

    /**
     * Включить-выключить перенос на следующую строку
     */
    public void setMultiLine(boolean multiLine) {
        tableCore.setMultiLine(multiLine);
    }

    public HandlerRegistration addHeaderMenuHandler(TableHeaderMenuEvent.Handler<T> handler) {
        return bus.addHandlerToSource(TableHeaderMenuEvent.TYPE, this, handler);
    }
}
