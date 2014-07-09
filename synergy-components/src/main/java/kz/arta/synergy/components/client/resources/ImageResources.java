package kz.arta.synergy.components.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * User: user
 * Date: 25.06.14
 * Time: 17:46
 * Рисунки
 */
public interface ImageResources extends ClientBundle {

    public static ImageResources IMPL = GWT.create(ImageResources.class);

    @Source("images/buttons/zoom-original.16.png")
    ImageResource zoom();

    @Source("images/dialog/d_close.png")
    ImageResource dialogCloseButton();

    @Source("images/dialog/d_close_mouseover.png")
    ImageResource dialogCloseButtonOver();

    @Source("images/dialog/d_collapse.png")
    ImageResource dialogCollapseButton();

    @Source("images/dialog/d_collapse_mouseover.png")
    ImageResource dialogCollapseButtonOver();

    @Source("images/buttons/navigation_right.png")
    ImageResource navigation_right();

    @Source("images/buttons/navigation_left.png")
    ImageResource navigation_left();
}
