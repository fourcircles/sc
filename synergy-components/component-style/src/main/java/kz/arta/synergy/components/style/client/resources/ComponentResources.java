package kz.arta.synergy.components.style.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * User: user
 * Date: 23.06.14
 * Time: 11:17
 */
public interface ComponentResources extends ClientBundle {

    @Source("css/Components.css")
    CssComponents cssComponents();

    @ClientBundle.Source("images/checkbox/checkbox_off.png")
    ImageResource checkboxOff();

    @ClientBundle.Source("images/checkbox/checkbox_on.png")
    ImageResource checkboxOn();

    @ClientBundle.Source("images/checkbox/checkbox_disabled.png")
    ImageResource checkboxDisabled();

    @ClientBundle.Source("images/radio_button/radiobutton_on.png")
    ImageResource radioOn();

    @ClientBundle.Source("images/radio_button/radiobutton_off.png")
    ImageResource radioOff();

    @ClientBundle.Source("images/radio_button/radiobutton_disabled.png")
    ImageResource radioDisabled();

}
