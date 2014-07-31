package kz.arta.sc3.showcase.client.buttons;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiFactory;
import kz.arta.synergy.components.client.button.ContextMenuButton;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.menu.ContextMenu;
import kz.arta.synergy.components.client.resources.ImageResources;

/**
 * User: vsl
 * Date: 23.07.14
 * Time: 16:08
 */
public class ButtonsBase {
    @UiFactory
    ImageButton simpleButton(ImageResource icon) {
        return new ImageButton(icon);
    }

    @UiFactory
    SimpleButton simpleButton(String text) {
        return new SimpleButton(text);
    }

    @UiFactory
    ContextMenuButton contextButton(String text) {
        ContextMenu menu = new ContextMenu();
        menu.addItem("Zoom", ImageResources.IMPL.zoom(), null);
        menu.addItem("Left", ImageResources.IMPL.navigationLeft(), null);
        menu.addSeparator();
        menu.addItem("Right", ImageResources.IMPL.navigationRight(), null);

        ContextMenuButton contextButton = new ContextMenuButton(text);
        contextButton.setContextMenu(menu);

        return contextButton;
    }

    @UiFactory
    ImageResources images() {
        return ImageResources.IMPL;
    }
}
