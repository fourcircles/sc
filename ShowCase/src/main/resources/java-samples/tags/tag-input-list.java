import kz.arta.synergy.components.client.input.tags.TagInput;

public class Sample {
    public static void main(String[] args) {

        final TagInput<String> tagInput = new TagInput<String>();
        for (String name : createShuffledNames()) {
            tagInput.addListItem(name, name);
        }
        tagInput.setListEnabled(true);
    }
}

