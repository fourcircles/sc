package kz.arta.synergy.components.client.dialog;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ButtonBase;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.resources.Messages;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 01.07.14
 * Time: 11:38
 */
public class ArtaDialogBox extends ArtaDialogBoxSimple {
    /**
     * margin слева для навигационных кнопок
     */
    private static final int HORIZONTAL_BUTTON_MARGIN = 20;

    private static final int MIN_INNER_MARGIN = 20;

    /**
     * панель для кнопок
     */
    FlowPanel buttonsPanel;

    /**
     * margin кнопки "сохранить", когда кнопки по бокам видны
     */
    private double saveButtonBaseMargin;

    /**
     * кнопка "назад"
     */
    private SimpleButton leftButton;

    /**
     * кнопка сохранить
     */
    private SimpleButton saveButton;

    /**
     * кнопка "еще"
     */
    private SimpleButton rightButton;

    private boolean isLeftButtonVisible;
    private boolean isRightButtonVisible;

    public ArtaDialogBox(String title, Widget content) {
        super(title, content);
        buttonsPanel = new FlowPanel();
        panel.add(buttonsPanel);

        leftButton = new SimpleButton(Messages.i18n.tr("Назад"), ImageResources.IMPL.back(), ButtonBase.IconPosition.LEFT);
        leftButton.getElement().getStyle().setFloat(Style.Float.LEFT);
        saveButton = new SimpleButton(Messages.i18n.tr("Сохранить"));
        rightButton = new SimpleButton(Messages.i18n.tr("Еще"), ImageResources.IMPL.forward(), ButtonBase.IconPosition.RIGHT);
        rightButton.getElement().getStyle().setFloat(Style.Float.RIGHT);

        buttonsPanel.add(leftButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(rightButton);

        leftButton.addStyleName(SynergyComponents.resources.cssComponents().dialogButton());
        saveButton.addStyleName(SynergyComponents.resources.cssComponents().dialogButton());
        saveButton.addStyleName(SynergyComponents.resources.cssComponents().approveButton());
        rightButton.addStyleName(SynergyComponents.resources.cssComponents().dialogButton());

        leftButton.addStyleName(SynergyComponents.resources.cssComponents().dialogBackButton());
        rightButton.addStyleName(SynergyComponents.resources.cssComponents().dialogMoreButton());

        addStyleName(SynergyComponents.resources.cssComponents().dialogWithButtons());

        leftButton.setVisible(true);
        rightButton.setVisible(true);

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        adjustMargins();
    }

    private void adjustMargins() {
        //2 extra pixels for border
        saveButtonBaseMargin = ((double) getOffsetWidth() - saveButton.getOffsetWidth() - Constants.DIALOG_BORDER_WIDTH * 2) / 2;
        adjustSaveButtonLeftMargin();
        adjustSaveButtonRightMargin();
    }

    private void adjustWidth() {
        int minLeft = (isLeftButtonVisible ? leftButton.getOffsetWidth() + HORIZONTAL_BUTTON_MARGIN: 0) + HORIZONTAL_BUTTON_MARGIN;
        int minRight = (isRightButtonVisible ? rightButton.getOffsetWidth() + HORIZONTAL_BUTTON_MARGIN : 0) + HORIZONTAL_BUTTON_MARGIN;

        int minSaveButtonBaseMargin = Math.max(minLeft, minRight);
        int minButtonsPanelWidth = minSaveButtonBaseMargin * 2 + saveButton.getOffsetWidth();

        setWidth(minButtonsPanelWidth + "px");
        adjustMargins();
    }

    public boolean isLeftButtonVisible() {
        return isLeftButtonVisible;
    }

    public boolean isRightButtonVisible() {
        return isRightButtonVisible;
    }

    private void adjustSaveButtonLeftMargin() {
        if (isLeftButtonVisible) {
            double newLeftMargin = saveButtonBaseMargin - leftButton.getOffsetWidth() - HORIZONTAL_BUTTON_MARGIN;
            if (newLeftMargin < MIN_INNER_MARGIN) {
                adjustWidth();
                return;
            }
            saveButton.getElement().getStyle().setMarginLeft(newLeftMargin, Style.Unit.PX);
        } else {
            saveButton.getElement().getStyle().setMarginLeft(saveButtonBaseMargin, Style.Unit.PX);
        }
    }

    private void adjustSaveButtonRightMargin() {
        if (isRightButtonVisible) {
            double newRightMargin = saveButtonBaseMargin - rightButton.getOffsetWidth() - HORIZONTAL_BUTTON_MARGIN;
            if (newRightMargin < MIN_INNER_MARGIN) {
                adjustWidth();
                return;
            }
            saveButton.getElement().getStyle().setMarginRight(newRightMargin, Style.Unit.PX);
        } else {
            saveButton.getElement().getStyle().setMarginRight(saveButtonBaseMargin, Style.Unit.PX);
        }
    }

    public void setLeftButtonVisible(boolean isBackButtonVisible) {
        this.isLeftButtonVisible = isBackButtonVisible;
        leftButton.setVisible(isBackButtonVisible);
        if (isAttached()) {
            adjustSaveButtonLeftMargin();
        }
    }

    public void setRightButtonVisible(boolean isMoreButtonVisible) {
        this.isRightButtonVisible = isMoreButtonVisible;
        rightButton.setVisible(isMoreButtonVisible);
        if (isAttached()) {
            adjustSaveButtonRightMargin();
        }
    }
}
