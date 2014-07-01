package kz.arta.synergy.components.client.dialog;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.SimpleButton;

/**
 * User: vsl
 * Date: 01.07.14
 * Time: 11:38
 */
public class ArtaDialogBox extends ArtaDialogBoxSimple {
    /**
     * margin слева для кнопки "назад" и справа для кнопки "еще"
     */
    private static final int HORIZONTAL_BUTTON_MARGIN = 20;

    /**
     * панель для кнопок
     */
    FlowPanel buttonsPanel;

    /**
     * margin кнопки "сохранить", когда кнопки по бокам видны
     */
    private Integer saveButtonBaseMargin;

    /**
     * кнопка "назад"
     */
    private SimpleButton backButton;

    /**
     * кнопка сохранить
     */
    private SimpleButton saveButton;

    /**
     * кнопка "еще"
     */
    private SimpleButton moreButton;

    private boolean isBackButtonVisible;
    private boolean isMoreButtonVisible;

    private Integer backButtonOffsetWidth;
    private Integer moreButtonOffsetWidth;

    /**
     * количество кнопок, ширина которых уже определена
     */
    private int setButtonsCnt = 0;

    public ArtaDialogBox(String title, Widget content) {
        super(title, content);
        buttonsPanel = new FlowPanel();
        panel.add(buttonsPanel);

        backButton = new SimpleButton("back");
        backButton.getElement().getStyle().setFloat(Style.Float.LEFT);
        saveButton = new SimpleButton("save");
        moreButton = new SimpleButton("more");
        moreButton.getElement().getStyle().setFloat(Style.Float.RIGHT);

        buttonsPanel.add(backButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(moreButton);

        backButton.addStyleName(SynergyComponents.resources.cssComponents().dialogButton());
        saveButton.addStyleName(SynergyComponents.resources.cssComponents().dialogButton());
        saveButton.addStyleName(SynergyComponents.resources.cssComponents().approveButton());
        moreButton.addStyleName(SynergyComponents.resources.cssComponents().dialogButton());

        backButton.addStyleName(SynergyComponents.resources.cssComponents().dialogBackButton());
        moreButton.addStyleName(SynergyComponents.resources.cssComponents().dialogMoreButton());

        addStyleName(SynergyComponents.resources.cssComponents().dialogWithButtons());

        backButton.setVisible(true);
        moreButton.setVisible(true);

        Command widthCallback = new Command() {
            @Override
            public void execute() {
                setUpWidthsAndMargins();
            }
        };

        backButton.setWidthCallback(widthCallback);
        moreButton.setWidthCallback(widthCallback);
        saveButton.setWidthCallback(widthCallback);
    }

    /**
     * запоминаем ширину всех кнопок, для последующей установки margins
     */
    private void setUpWidthsAndMargins() {
        setButtonsCnt++;
        if (setButtonsCnt == 3) {
            backButtonOffsetWidth = backButton.getOffsetWidth();
            moreButtonOffsetWidth = moreButton.getOffsetWidth();
            saveButtonBaseMargin = (titlePanel.getOffsetWidth() - saveButton.getOffsetWidth() -
                    backButtonOffsetWidth - moreButtonOffsetWidth - HORIZONTAL_BUTTON_MARGIN * 2) / 2;
            setBackButtonVisible(isBackButtonVisible);
            setMoreButtonVisible(isMoreButtonVisible);
        }
    }

    public boolean isBackButtonVisible() {
        return isBackButtonVisible;
    }

    public boolean isMoreButtonVisible() {
        return isMoreButtonVisible;
    }

    private void adjustSaveButtonLeftMargin(boolean isBackButtonVisible) {
        if (isBackButtonVisible) {
            saveButton.getElement().getStyle().setMarginLeft(saveButtonBaseMargin, Style.Unit.PX);
        } else {
            saveButton.getElement().getStyle().setMarginLeft(saveButtonBaseMargin + backButtonOffsetWidth + HORIZONTAL_BUTTON_MARGIN, Style.Unit.PX);
        }
    }

    private void adjustSaveButtonRightMargin(boolean isMoreButtonVisible) {
        if (isMoreButtonVisible) {
            saveButton.getElement().getStyle().setMarginRight(saveButtonBaseMargin, Style.Unit.PX);
        } else {
            saveButton.getElement().getStyle().setMarginRight(saveButtonBaseMargin + moreButtonOffsetWidth + HORIZONTAL_BUTTON_MARGIN, Style.Unit.PX);
        }
    }

    public void setBackButtonVisible(boolean isBackButtonVisible) {
        this.isBackButtonVisible = isBackButtonVisible;
        if (isAttached()) {
            backButton.setVisible(isBackButtonVisible);
            adjustSaveButtonLeftMargin(isBackButtonVisible);
        }
    }

    public void setMoreButtonVisible(boolean isMoreButtonVisible) {
        this.isMoreButtonVisible = isMoreButtonVisible;
        if (isAttached()) {
            moreButton.setVisible(isMoreButtonVisible);
            adjustSaveButtonRightMargin(isMoreButtonVisible);
        }
    }
}
