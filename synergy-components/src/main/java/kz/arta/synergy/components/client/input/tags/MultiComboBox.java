package kz.arta.synergy.components.client.input.tags;

import kz.arta.synergy.components.client.menu.MenuItem;
import kz.arta.synergy.components.client.resources.ImageResources;

import java.util.HashSet;
import java.util.Set;
/**
 * User: vsl
 * Date: 18.08.14
 * Time: 15:06
 */
public class MultiComboBox<V> extends TagInput<V> {
    public MultiComboBox() {
        super(true);

        setListEnabled(true);
        mainButton.setIcon(ImageResources.IMPL.comboBoxDropDown());
    }

    @Override
    protected void tagRemoved() {
        input.setText("");
        setInputOffset(tagsPanel.getOffsetWidth());
    }

    /**
     * В мультикомбобоксе нельзя добавлять произвольные значения
     */
    @Override
    protected void keyEnter() {
        // nope
    }

    public void select(V value) {
        MenuItem<Tag<V>> item = getListItem(value);
        if (item != null) {
            item.setValue(true, true);
        }
    }

    public void deselect(V value) {
        MenuItem<Tag<V>> item = getListItem(value);
        if (item != null) {
            item.setValue(false, true);
        }
    }

    public Set<V> getSelectedValues() {
        Set<V> selected = new HashSet<V>();

        for (int i = 0; i < list.size(); i++) {
            MenuItem<Tag<V>> item = list.getItemAt(i);
            if (item.isSelected()) {
                selected.add(item.getUserValue().getValue());
            }
        }
        return selected;
    }
}
