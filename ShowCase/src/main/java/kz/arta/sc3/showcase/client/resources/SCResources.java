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

    @ClientBundle.Source("java-samples/buttons/buttongroup.java")
    TextResource buttonGroup();
    @ClientBundle.Source("java-samples/buttons/buttongroup-multitoggle.java")
    TextResource buttonGroupMultiToggle();
    @ClientBundle.Source("java-samples/buttons/buttongroup-toggle.java")
    TextResource buttonGroupToggle();
    @ClientBundle.Source("java-samples/buttons/toggle-button.java")
    TextResource toggleButton();

    @ClientBundle.Source("java-samples/fields/search-result.java")
    TextResource search();
    @ClientBundle.Source("java-samples/fields/number-input-max.java")
    TextResource numberInputMax();
    @ClientBundle.Source("java-samples/fields/number-input-double.java")
    TextResource numberInputDouble();
    @ClientBundle.Source("java-samples/fields/number-input-integer.java")
    TextResource numberInputInteger();
    @ClientBundle.Source("java-samples/fields/text-input.java")
    TextResource textInput();
    @ClientBundle.Source("java-samples/fields/text-input-disabled.java")
    TextResource textInputDisabled();
    @ClientBundle.Source("java-samples/fields/text-input-nonempty.java")
    TextResource textInputNonEmpty();
    @ClientBundle.Source("java-samples/fields/textarea.java")
    TextResource textarea();
    @ClientBundle.Source("java-samples/fields/combobox.java")
    TextResource combobox();


    @ClientBundle.Source("java-samples/checkbox/checkbox.java")
    TextResource checkbox();
    @ClientBundle.Source("java-samples/checkbox/checkbox-disabled.java")
    TextResource checkboxDisabled();
    @ClientBundle.Source("java-samples/checkbox/radiobutton.java")
    TextResource radiobutton();

    @ClientBundle.Source("java-samples/tags/multicombobox.java")
    TextResource multiComboBox();
    @ClientBundle.Source("java-samples/tags/tag-input.java")
    TextResource tagInput();
    @ClientBundle.Source("java-samples/tags/tag-input-list.java")
    TextResource tagInputList();
    @ClientBundle.Source("java-samples/tags/tag-input-nobutton.java")
    TextResource tagInputNoButton();

    @ClientBundle.Source("java-samples/fields/period-input.java")
    TextResource periodInput();

    @ClientBundle.Source("java-samples/collapsing-panel.java")
    TextResource collapsingPanel();
    @ClientBundle.Source("java-samples/stack-panel.java")
    TextResource stackPanel();
    @ClientBundle.Source("java-samples/tabs.java")
    TextResource tabs();
    @ClientBundle.Source("java-samples/tree.java")
    TextResource tree();
    @ClientBundle.Source("java-samples/table-cells.java")
    TextResource tableCells();
    @ClientBundle.Source("java-samples/table-rows.java")
    TextResource tableRows();
    @ClientBundle.Source("java-samples/treetable.java")
    TextResource treetable();
    @ClientBundle.Source("java-samples/comments.java")
    TextResource comments();
    @ClientBundle.Source("java-samples/comments-dark.java")
    TextResource commentsDark();

    @ClientBundle.Source("java-samples/dialogs/dialog-buttons-nomodal.java")
    TextResource dialogButtons();
    @ClientBundle.Source("java-samples/dialogs/dialog-modal-medium.java")
    TextResource dialogModalMedium();
    @ClientBundle.Source("java-samples/dialogs/dialog-modal-small.java")
    TextResource dialogModalSmall();
    @ClientBundle.Source("java-samples/dialogs/dialog-nomodal.java")
    TextResource dialog();


}
