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

