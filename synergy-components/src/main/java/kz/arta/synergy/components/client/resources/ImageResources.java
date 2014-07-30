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
    ImageResource navigationRight();

    @Source("images/buttons/navigation_left.png")
    ImageResource navigationLeft();

    @Source("images/buttons/white_button_dropdown_icon.png")
    ImageResource whiteButtonDropdown();

    @Source("images/buttons/green_button_dropdown_icon.png")
    ImageResource greenButtonDropdown();

    @Source("images/buttons/combobox_dropdown_icon.png")
    ImageResource comboBoxDropDown();

    /**
     * временная стрелка вверх
     * @return
     */
    @Source("images/buttons/navigation_up.png")
    ImageResource navigationUp();

    @Source("images/buttons/navigation_down.png")
    ImageResource navigationDown();

    @Source("images/scroll/scrollbar_down.png")
    ImageResource scrollBarDown();

    @Source("images/scroll/scrollbar_down_pressed.png")
    ImageResource scrollBarDownPress();

    @Source("images/scroll/scrollbar_up.png")
    ImageResource scrollBarUp();

    @Source("images/scroll/scrollbar_up_pressed.png")
    ImageResource scrollBarUpPress();

    @Source("images/scroll/scrollbar_left.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarLeft();

    @Source("images/scroll/scrollbar_left_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarLeftPress();

    @Source("images/scroll/scrollbar_right.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarRight();

    @Source("images/scroll/scrollbar_right_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarRightPress();
}
