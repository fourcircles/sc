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

    public static ImageResource IMPL = GWT.create(ImageResources.class);

    @Source("images/buttons/zoom-original.16.png")
    ImageResource zoom();

}
