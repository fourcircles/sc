import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.synergy.components.client.input.tags.TagInput;
import kz.arta.synergy.components.client.menu.DropDownListMulti;

public class Sample {
    public static void main(String[] args) {

        //создание списка
        DropDownListMulti<String> multiList = new DropDownListMulti<String>(parent, new SimpleEventBus());
        String[] names = createShuffledNames();
        for (String name : names) {
            multiList.addItem(name, name);
        }

        TagInput<String> tagInput = new TagInput<String>();
        tagInput.setDropDownList(multiList);
    }
}

