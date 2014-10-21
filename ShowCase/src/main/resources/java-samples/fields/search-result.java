import com.google.gwt.event.shared.SimpleEventBus;
import kz.arta.synergy.components.client.input.SearchResultInput;

public class Sample {
    public static void main(String[] args) {
        SearchResultInput<String> search = new SearchResultInput<String>(true);

        DropDownList<String> list = new DropDownList<String>(relativeWidget, new SimpleEventBus());

        for (String name : createShuffledNames()) {
            list.addItem(name, name);
        }
        search.setList(list);

        search.addSelectionHandler(new SelectionEvent.Handler<String>() {
            @Override
            public void onSelection(SelectionEvent<String> event) {
                System.out.println(event.getValue());
            }

            @Override
            public void onDeselection(SelectionEvent<String> event) {
                System.out.println(event.getValue());
            }
        })
    }
}
