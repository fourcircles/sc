package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * User: vsl
 * Date: 31.07.14
 * Time: 18:00
 */
public class TagsWithButton extends Composite{
    private FlowPanel root;

    public TagsWithButton() {
        root = new FlowPanel();
        initWidget(root);
    }
}
