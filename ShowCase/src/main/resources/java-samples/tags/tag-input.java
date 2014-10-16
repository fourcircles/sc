import kz.arta.synergy.components.client.input.tags.TagInput;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;

public class Sample {
    public static void main(String[] args) {
        TagInput<String> tagInput = new TagInput<String>();
        tagInput.addTagAddHandler(new TagAddEvent.Handler<String>() {
            @Override
            public void onTagAdd(TagAddEvent<String> event) {
                //тэг добавлен
                System.out.println(event.getTag().getValue() + " добавлен");
            }
        });
        tagInput.addTagRemoveHandler(new TagRemoveEvent.Handler<String>() {
            @Override
            public void onTagRemove(TagRemoveEvent<String> event) {
                //тег удален
                System.out.println(event.getTag().getValue() + " удален");
            }
        })

    }
}

