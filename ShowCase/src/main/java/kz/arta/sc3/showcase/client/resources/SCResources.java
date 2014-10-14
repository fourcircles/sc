package kz.arta.sc3.showcase.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;

/**
 * User: vsl
 * Date: 13.10.14
 * Time: 16:03
 */
public interface SCResources extends ClientBundle {

    @ClientBundle.Source("js/highlight.pack.js")
    TextResource highlightJs();

    @ClientBundle.Source("css/idea.css")
    @CssResource.NotStrict
    CssResource idea();

    @ClientBundle.Source("css/github.css")
    @CssResource.NotStrict
    CssResource gitHub();

    @ClientBundle.Source("java-samples/simple-button.java")
    TextResource simpleButton();
    @ClientBundle.Source("java-samples/simple-button-click.java")
    TextResource simpleButtonClick();
    @ClientBundle.Source("java-samples/simple-button-disabled.java")
    TextResource simpleButtonDisabled();
    @ClientBundle.Source("java-samples/simple-button-menu.java")
    TextResource simpleButtonMenu();

}
