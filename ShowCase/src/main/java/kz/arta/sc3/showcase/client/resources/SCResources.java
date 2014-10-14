package kz.arta.sc3.showcase.client.resources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;

/**
 * User: vsl
 * Date: 13.10.14
 * Time: 16:03
 */
public interface SCResources extends ClientBundle {
    SCResources IMPL = GWT.create(SCResources.class);

    @ClientBundle.Source("js/highlight.pack.js")
    TextResource highlightJs();

    @ClientBundle.Source("css/idea.css")
    @CssResource.NotStrict
    CssResource idea();

    @ClientBundle.Source("css/github.css")
    @CssResource.NotStrict
    CssResource gitHub();

    @ClientBundle.Source("java-samples/simple-buttons.java")
    TextResource buttons();
}
