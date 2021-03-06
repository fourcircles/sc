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

    ImageResources IMPL = GWT.create(ImageResources.class);

    @Source("images/buttons/zoom-original.16.png")
    ImageResource zoom();

    @Source("images/buttons/zoom-original.16_transparent.png")
    ImageResource zoomTransparent();

    @Source("images/dialog/d_close.png")
    ImageResource dialogCloseButton();

    @Source("images/dialog/d_close_mouseover.png")
    ImageResource dialogCloseButtonOver();

    @Source("images/dialog/d_collapse.png")
    ImageResource dialogCollapseButton();

    @Source("images/dialog/d_collapse_mouseover.png")
    ImageResource dialogCollapseButtonOver();

    @Source("images/buttons/navigation_right.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource navigationRight();

    @Source("images/buttons/navigation_left.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource navigationLeft();

    @Source("images/buttons/navigation_up.png")
    ImageResource navigationUp();

    @Source("images/buttons/navigation_down.png")
    ImageResource navigationDown();

    @Source("images/buttons/white_button_dropdown_icon.png")
    ImageResource whiteButtonDropdown();

    @Source("images/buttons/green_button_dropdown_icon.png")
    ImageResource greenButtonDropdown();

    @Source("images/buttons/combobox_dropdown_icon.png")
    ImageResource comboBoxDropDown();

    @Source("images/scroll/scrollbar_down.png")
    ImageResource scrollBarDown();

    @Source("images/scroll/scrollbar_up.png")
    ImageResource scrollBarUp();

    @Source("images/scroll/scrollbar_left.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarLeft();

    @Source("images/scroll/scrollbar_right.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarRight();

    @Source("images/scroll/scrollbar_down_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarDownPressed();

    @Source("images/scroll/scrollbar_up_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarUpPressed();

    @Source("images/scroll/scrollbar_left_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarLeftPressed();

    @Source("images/scroll/scrollbar_right_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarRightPressed();

    @Source("images/tag_close.png")
    ImageResource tagClose();

    @Source("images/buttons/calendar_icon.png")
    ImageResource calendarIcon();

    @Source("images/magzhan.png")
    ImageResource magzhan();

    @Source("images/tabs/tabs-button-left.png")
    ImageResource tabsLeft();
    @Source("images/tabs/tabs-button-left-over.png")
    ImageResource tabsLeftOver();
    @Source("images/tabs/tabs-button-right.png")
    ImageResource tabsRight();
    @Source("images/tabs/tabs-button-right-over.png")
    ImageResource tabsRightOver();

    @Source("images/tree/folder_icon.png")
    ImageResource folder();

    @Source("images/tree/node-closed-16.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource nodeClosed16();
    @Source("images/tree/node-open-16.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource nodeOpen16();
    @Source("images/tree/black_tree_node_closed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource blackNodeClosed();
    @Source("images/tree/black_tree_node_opened.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource blackNodeOpened();


    @Source("images/tree/favourite_icon.png")
    ImageResource favouriteFolder();
    @Source("images/tree/project_icon.png")
    ImageResource project();
    @Source("images/tree/project_portfolio_icon.png")
    ImageResource portfolio();

    @Source("images/post.png")
    ImageResource post();

    @Source("images/pager/left-enabled.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource pagerLeft();
    @Source("images/pager/right-enabled.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource pagerRight();

    @Source("images/comments/delete_comment.png")
    ImageResource deleteComment();
    @Source("images/comments/negative_comment.png")
    ImageResource negativeComment();
    @Source("images/comments/positive_comment.png")
    ImageResource positiveComment();
    @Source("images/comments/delete_note.png")
    ImageResource deleteNote();
    @Source("images/comments/submit_note.png")
    ImageResource submitNote();

    @Source("images/scroll/dark/scrollbar_dark_down.png")
    ImageResource scrollBarDarkDown();

    @Source("images/scroll/dark/scrollbar_dark_up.png")
    ImageResource scrollBarDarkUp();

    @Source("images/scroll/dark/scrollbar_dark_left.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarDarkLeft();

    @Source("images/scroll/dark/scrollbar_dark_right.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarDarkRight();

    @Source("images/scroll/dark/scrollbar_dark_down_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarDarkDownPressed();

    @Source("images/scroll/dark/scrollbar_dark_up_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarDarkUpPressed();

    @Source("images/scroll/dark/scrollbar_dark_left_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarDarkLeftPressed();

    @Source("images/scroll/dark/scrollbar_dark_right_pressed.png")
    @ImageResource.ImageOptions(flipRtl = true)
    ImageResource scrollBarDarkRightPressed();

    @ClientBundle.Source("images/notifications/failure.png")
    ImageResource notificationFailure();
    @ClientBundle.Source("images/notifications/question.png")
    ImageResource notificationQuestion();
    @ClientBundle.Source("images/notifications/yellow_question.png")
    ImageResource notificationYellowQuestion();
    @ClientBundle.Source("images/notifications/success.png")
    ImageResource notificationSuccess();

    @ClientBundle.Source("images/file/from_pc.png")
    ImageResource fromPC();
    @ClientBundle.Source("images/file/from_storage.png")
    ImageResource fromStorage();
    @ClientBundle.Source("images/file/new.png")
    ImageResource createNew();
    @ClientBundle.Source("images/file/scan.png")
    ImageResource scan();
    @ClientBundle.Source("images/file/add.png")
    ImageResource add();

    @ClientBundle.Source("images/arrow_up.png")
    ImageResource arrow_up();
    @ClientBundle.Source("images/arrow_down.png")
    ImageResource arrow_down();
}
