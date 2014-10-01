package kz.arta.synergy.components.client.input.tags;

import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.DropDownListMulti;
import kz.arta.synergy.components.client.resources.ImageResources;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 18.08.14
 * Time: 15:06
 */
public class MultiComboBox<V> extends TagInput<V> {
    public MultiComboBox() {
        super(true);
        button.setIcon(ImageResources.IMPL.comboBoxDropDown());
    }

    @Override
    protected void newListSelection() {
        input.setText("");
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
    }

    public void addItem(String text, V value) {
        super.addTag(new Tag<V>(text, value));
    }
    public boolean contains(V value) {
        return getTag(value) != null;
    }
    public void remove(V value) {
        Tag<V> tag = getTag(value);
        if (tag != null) {
            removeTag(getTag(value));
        }
    }

    public List<V> getSelectedValues() {
        ArrayList<V> result = new ArrayList<V>();
        for (DropDownList<V>.Item item : dropDownList.getItems()) {
            if (((DropDownListMulti.Item) item).isSelected()) {
                result.add(item.getValue());
            }
        }

        return result;
    }
}
