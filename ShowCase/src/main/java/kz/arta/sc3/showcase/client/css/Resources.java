package kz.arta.sc3.showcase.client.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

/**
 * User: vsl
 * Date: 25.06.14
 * Time: 10:45
 */
public interface Resources extends ClientBundle {
    Resources IMPL = GWT.create(Resources.class);

    @ClientBundle.Source("red.css")
    Red red();

    @ClientBundle.Source("blue.css")
    Blue blue();
}
