package kz.arta.synergy.components.client.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Класс для тега <p>
 */
public class PPanel extends SimplePanel {
    public PPanel() {
        super(Document.get().createPElement());
    }

    public PPanel(int lineHeight) {
        this();
        StyleUtils.setLineHeight(getElement(), lineHeight, Style.Unit.PX);
    }
}

