import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.sc3.showcase.client.ShowCase;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.input.tags.TagInput;
import kz.arta.synergy.components.client.menu.DropDownListMulti;
import kz.arta.synergy.components.client.menu.MenuItem;

public class Sample {
    public static void main(String[] args) {

        final TagInput<String> tagInput = new TagInput<String>();
        for (String name : createShuffledNames()) {
            tagInput.addListItem(name, name);
        }
        tagInput.setListEnabled(true);
    }
}

