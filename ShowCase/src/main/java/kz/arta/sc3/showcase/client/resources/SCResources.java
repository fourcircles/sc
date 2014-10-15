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

    @ClientBundle.Source("java-samples/buttons/simple-button.java")
    TextResource simpleButton();
    @ClientBundle.Source("java-samples/buttons/simple-button-click.java")
    TextResource simpleButtonClick();
    @ClientBundle.Source("java-samples/buttons/simple-button-disabled.java")
    TextResource simpleButtonDisabled();
    @ClientBundle.Source("java-samples/buttons/simple-button-menu.java")
    TextResource simpleButtonMenu();
    @ClientBundle.Source("java-samples/buttons/accept-button.java")
    TextResource acceptButton();
    @ClientBundle.Source("java-samples/buttons/decline-button.java")
    TextResource declineButton();
    @ClientBundle.Source("java-samples/buttons/accept-button-menu.java")
    TextResource acceptButtonMenu();
    @ClientBundle.Source("java-samples/buttons/icon-button.java")
    TextResource iconButton();
    @ClientBundle.Source("java-samples/buttons/icon-button-right.java")
    TextResource iconButtonRight();
    @ClientBundle.Source("java-samples/buttons/only-icon-button.java")
    TextResource onlyIconButton();
    @ClientBundle.Source("java-samples/buttons/icon-button-menu-wide.java")
    TextResource iconButtonMenuWide();


}
