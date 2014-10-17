package kz.arta.synergy.components.client.input.tags;

import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.DropDownListMulti;
import kz.arta.synergy.components.client.resources.ImageResources;

import java.util.ArrayList;
import java.util.List;
// todo test
/**
 * User: vsl
 * Date: 18.08.14
 * Time: 15:06
 */
public class MultiComboBox<V> extends TagInput<V> {
    public MultiComboBox() {
        super(true);
        DropDownListMulti<V> newList = new DropDownListMulti<V>(this, null);
        setDropDownList(newList);

        mainButton.setIcon(ImageResources.IMPL.comboBoxDropDown());
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

    public void select(V value) {
        dropDownList.selectValue(value);
    }

    public void deselect(V value) {
        if (dropDownList.contains(value) && contains(value)) {
            ((DropDownListMulti.Item) dropDownList.get(value)).setSelected(false, true);
        }
    }

    public void addItem(String text, V value) {
        dropDownList.addItem(text, value);
    }

    public void removeItem(V value) {
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
