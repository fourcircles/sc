import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.synergy.components.client.input.SearchResultInput;
import kz.arta.synergy.components.client.menu.DropDownList;
import kz.arta.synergy.components.client.menu.MenuItem;

public class Sample {
    public static void main(String[] args) {
        SearchResultInput<String> search = new SearchResultInput<String>(true);

        DropDownList<String> list = new DropDownList<String>();

        for (String name : createShuffledNames()) {
            list.add(new MenuItem<String>(name, name));
        }
        search.setList(list);

        search.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                System.out.println(event.getValue());
            }
        });
    }
}
