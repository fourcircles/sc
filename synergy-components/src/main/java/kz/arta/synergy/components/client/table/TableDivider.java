package kz.arta.synergy.components.client.table;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.table.column.ArtaColumn;

/**
 * User: vsl
 * Date: 28.11.14
 * Time: 14:38
 */
public class TableDivider<T> extends Composite
        implements HasMouseDownHandlers, HasMouseUpHandlers, HasMouseMoveHandlers{
    private ArtaColumn<T> column;

    private boolean resizing;
    private int oldPosition;

    public TableDivider(ArtaColumn<T> column) {
        this.column = column;

        FlowPanel root = new FlowPanel();
        initWidget(root);

        root.setStyleName(SynergyComponents.getResources().cssComponents().tableDivider());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        oldPosition = getAbsoluteLeft();
    }

    public void setResizing(boolean resizing) {
        if (resizing && !column.isResizable()) {
            return;
        }
        this.resizing = resizing;
        if (resizing) {
            addStyleName(SynergyComponents.getResources().cssComponents().drag());
        } else {
            removeStyleName(SynergyComponents.getResources().cssComponents().drag());
        }
        oldPosition = getAbsoluteLeft();
    }

    public boolean isResizing() {
        return resizing;
    }

    public ArtaColumn<T> getColumn() {
        return column;
    }

    public int getOldPosition() {
        return oldPosition;
    }

    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler(handler, MouseUpEvent.getType());
    }
}
