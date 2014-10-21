package kz.arta.synergy.components.client.dagger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.dagger.events.DaggerItemSelectionEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * User: vsl
 * Date: 21.10.14
 * Time: 11:06
 */
public class DaggerDropDownListMulti<V> extends DaggerDropDownList<V> {

//    private Set<DaggerItem<V>> selectedItems = new HashSet<DaggerItem<V>>();
//    private Set<V> selectedValues = new HashSet<V>();

    @Override
    protected ValueChangeHandler<Boolean> getSelectionHandler(DaggerItem<V> newItem) {
        if (selectionHandler == null) {
            selectionHandler = new ValueChangeHandler<Boolean>() {
                @Override
                @SuppressWarnings({"unchecked"})
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    DaggerItem<V> item = (DaggerItem) event.getSource();
//                    if (event.getValue()) {
//                        selectedItems.add(item);
//                        selectedValues.add(item.getUserValue());
//                    } else {
//                        selectedItems.remove(item);
//                        selectedValues.remove(item.getUserValue());
//                    }
                    fireEvent(new DaggerItemSelectionEvent<V>(item, event.getValue()));
                }
            };
        }
        return selectionHandler;
    }
}
