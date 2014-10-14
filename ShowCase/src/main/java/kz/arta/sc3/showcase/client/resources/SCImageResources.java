package kz.arta.sc3.showcase.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * User: user
 * Date: 26.06.14
 * Time: 17:20
 */
public interface SCImageResources extends ClientBundle {

    @Source("images/zoom-original.16.png")
    ImageResource zoom();

    @ClientBundle.Source("images/code-sample.png")
    ImageResource code();
}
