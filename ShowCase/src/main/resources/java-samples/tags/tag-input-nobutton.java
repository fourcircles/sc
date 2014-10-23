import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.sc3.showcase.client.ShowCase;
import kz.arta.sc3.showcase.client.resources.SCMessages;
import kz.arta.synergy.components.client.input.tags.TagInput;

public class Sample {
    public static void main(String[] args) {
        TagInput<String> tagInput = new TagInput<String>(false);

        for (String name : createShuffledNames()) {
            tagInput.addListItem(name, name);
        }
        tagInput.setListEnabled(true);
    }
}

