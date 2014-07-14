package kz.arta.synergy.components.client.dialog;

import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
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
 * Диалог с кнопкой "сохранить" и кнопками навигации.
 */
public class Dialog extends DialogSimple {
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

    public Dialog() {
        super();
        buttonsPanel = new FlowPanel();
        panel.add(buttonsPanel);

        leftButton = makeButton(Messages.i18n.tr("Назад"), ImageResources.IMPL.navigationLeft(), ButtonBase.IconPosition.LEFT);
//        leftButton.getElement().getStyle().setFloat(Style.Float.LEFT);
        saveButton = makeButton(Messages.i18n.tr("Сохранить"));
        rightButton = makeButton(Messages.i18n.tr("Еще"), ImageResources.IMPL.navigationRight(), ButtonBase.IconPosition.RIGHT);
//        rightButton.getElement().getStyle().setFloat(Style.Float.RIGHT);

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

    SimpleButton makeButton(String title) {
        return new SimpleButton(title);
    }

    SimpleButton makeButton(String title, ImageResource img, ButtonBase.IconPosition position) {
        return new SimpleButton(title, img, position);
    }

    public Dialog(String title, Widget content) {
        this();
        setText(title);
        setContent(content);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        adjustSaveButtonMargin();
    }

    /**
     * Изменяет значения левого и правого отступов для кнопки "сохранить", чтобы она оставалась в середине
     * вне зависимости от отображения и ширины кнопок навигации. При необходимости ширина диалога изменяется
     * на минимальную удовлетворяющую требованиям.
     */
    protected void adjustSaveButtonMargin() {
        double freeSpace = (double) getOffsetWidth() -
                Constants.DIALOG_BORDER_WIDTH * 2 -
                saveButton.getOffsetWidth();

        double leftMargin;
        double rightMargin;

        if (leftButton.isVisible() && rightButton.isVisible()) {
            freeSpace -= leftButton.getOffsetWidth() + Constants.DIALOG_NAV_BUTTON_HMARGIN;
            freeSpace -= rightButton.getOffsetWidth() + Constants.DIALOG_NAV_BUTTON_HMARGIN;
            freeSpace /= 2;
            if (freeSpace < MIN_INNER_MARGIN) {
                setWidth(getOffsetWidth() - Constants.DIALOG_BORDER_WIDTH * 2 +
                        (MIN_INNER_MARGIN - freeSpace) * 2 + "px");
                leftMargin = rightMargin = MIN_INNER_MARGIN;
            } else {
                leftMargin = rightMargin = freeSpace;
            }
        } else {
            if (!leftButton.isVisible() && !rightButton.isVisible()) {
                freeSpace /= 2;
                if (freeSpace < MIN_INNER_MARGIN) {
                    setWidth(saveButton.getOffsetWidth() + Constants.DIALOG_NAV_BUTTON_HMARGIN * 2 + "px");
                    leftMargin = rightMargin = MIN_INNER_MARGIN;
                } else {
                    leftMargin = rightMargin = freeSpace;
                }
            } else {
                double noButtonSideMargin = freeSpace / 2;
                double buttonWidth;

                if (leftButton.isVisible()) {
                    buttonWidth = leftButton.getOffsetWidth();
                } else {
                    buttonWidth = rightButton.getOffsetWidth();
                }

                double buttonSideMargin = noButtonSideMargin - buttonWidth - Constants.DIALOG_NAV_BUTTON_HMARGIN;

                if (buttonSideMargin < MIN_INNER_MARGIN) {
                    setWidth(saveButton.getOffsetWidth() +
                            (MIN_INNER_MARGIN + buttonWidth + Constants.DIALOG_NAV_BUTTON_HMARGIN) * 2 +
                            "px");
                    leftMargin = MIN_INNER_MARGIN;
                    rightMargin = ((double) getOffsetWidth() -
                            Constants.DIALOG_BORDER_WIDTH * 2 -
                            saveButton.getOffsetWidth()) / 2;
                } else {
                    leftMargin = buttonSideMargin;
                    rightMargin = noButtonSideMargin;
                }
                if (rightButton.isVisible()) {
                    double tmp = leftMargin;
                    leftMargin = rightMargin;
                    rightMargin = tmp;
                }
            }
        }
        saveButton.getElement().getStyle().setMarginLeft(leftMargin, Style.Unit.PX);
        //ширина элементов + отступы равна ширине содержащего элемента
        //ie в этом случае переносит последний элемент на следующую строку, для этого -0.5
        saveButton.getElement().getStyle().setMarginRight(rightMargin - 0.5, Style.Unit.PX);
    }

    public void setLeftButtonVisible(boolean visible) {
        leftButton.setVisible(visible);
        adjustSaveButtonMargin();
    }

    public void setRightButtonVisible(boolean visible) {
        rightButton.setVisible(visible);
        adjustSaveButtonMargin();
    }
}
