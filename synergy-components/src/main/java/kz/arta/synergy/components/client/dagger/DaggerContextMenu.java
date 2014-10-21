package kz.arta.synergy.components.client.dagger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 20.10.14
 * Time: 11:25
 *
 * Контекстное меню. Скролла нет.
 */
public class DaggerContextMenu extends DaggerMenu<Command> {
    private ValueChangeHandler<Boolean> selectionHandler;

    public DaggerContextMenu() {
        super();
        popup.setWidget(root);

        popup.setStyleName(SynergyComponents.getResources().cssComponents().contextMenu());
        selectionHandler = new ValueChangeHandler<Boolean>() {
            @Override
            @SuppressWarnings({"unchecked"})
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                DaggerItem<Command> item = (DaggerItem) event.getSource();
                if (item.getUserValue() != null) {
                    item.getUserValue().execute();
                }
                item.setValue(false, false);
                noFocused();
                hide();
            }
        };
    }

    @Override
    protected ValueChangeHandler<Boolean> getSelectionHandler(DaggerItem<Command> newItem) {
        return selectionHandler;
    }
}
