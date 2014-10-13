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
     * минимальное расстояние между кнопкой "сохранить" и навигационными кнопками
     */
    private static final int MIN_INNER_MARGIN = 20;

    /**
     * панель для кнопок
     */
    FlowPanel buttonsPanel;

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
        buttonsPanel.setHeight(Constants.DIALOG_BUTTON_PANEL_HEIGHT + "px");
        panel.add(buttonsPanel);

        leftButton = makeButton(Messages.i18n().tr("Назад"), ImageResources.IMPL.navigationLeft(), ButtonBase.IconPosition.LEFT);
        saveButton = makeButton(Messages.i18n().tr("Сохранить"));
        rightButton = makeButton(Messages.i18n().tr("Еще"), ImageResources.IMPL.navigationRight(), ButtonBase.IconPosition.RIGHT);

        buttonsPanel.add(leftButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(rightButton);

        leftButton.addStyleName(SynergyComponents.getResources().cssComponents().dialogButton());
        saveButton.addStyleName(SynergyComponents.getResources().cssComponents().dialogButton());
        saveButton.addStyleName(SynergyComponents.getResources().cssComponents().approveButton());
        rightButton.addStyleName(SynergyComponents.getResources().cssComponents().dialogButton());

        leftButton.addStyleName(SynergyComponents.getResources().cssComponents().dialogBackButton());
        rightButton.addStyleName(SynergyComponents.getResources().cssComponents().dialogMoreButton());

        addStyleName(SynergyComponents.getResources().cssComponents().dialogWithButtons());

        leftButton.setVisible(true);
        rightButton.setVisible(true);
    }

    public Dialog(String title, Widget content) {
        this();
        setText(title);
        setContent(content);
    }

    SimpleButton makeButton(String title) {
        return new SimpleButton(title);
    }

    SimpleButton makeButton(String title, ImageResource img, ButtonBase.IconPosition position) {
        return new SimpleButton(title, img, position);
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
        if (!isAttached()) {
            return;
        }
        double freeSpace = (double) getOffsetWidth() -
                Constants.DIALOG_BORDER_WIDTH * 2 -
                saveButton.getOffsetWidth();

        double leftMargin;
        double rightMargin;

        if (leftButton.isVisible() && rightButton.isVisible()) {
            //случай, когда присутствуют обе навигационные кнопки
            //кнопка "сохранить" равноудалена от левой и правой навигационных кнопок
            freeSpace -= leftButton.getOffsetWidth() + Constants.DIALOG_NAV_BUTTON_HMARGIN;
            freeSpace -= rightButton.getOffsetWidth() + Constants.DIALOG_NAV_BUTTON_HMARGIN;
            freeSpace /= 2;
            if (freeSpace < MIN_INNER_MARGIN) {
                setWidth(getOffsetWidth() - Constants.DIALOG_BORDER_WIDTH * 2 +
                        (MIN_INNER_MARGIN - freeSpace) * 2 + "px");
                leftMargin = rightMargin = MIN_INNER_MARGIN - 1;
            } else {
                leftMargin = rightMargin = freeSpace;
            }
        } else {
            //в обоих следующих случаях кнопка "сохранить" равноудалена от левого и правого границ диалога
            if (!leftButton.isVisible() && !rightButton.isVisible()) {
                //случай, когда присутствует только кнопка "сохранить"
                freeSpace /= 2;
                if (freeSpace < MIN_INNER_MARGIN) {
                    setWidth(saveButton.getOffsetWidth() + Constants.DIALOG_NAV_BUTTON_HMARGIN * 2 + "px");
                    leftMargin = rightMargin = MIN_INNER_MARGIN;
                } else {
                    leftMargin = rightMargin = freeSpace;
                }
            } else {
                //случай, когда присутствует только одна навигационная кнопка
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
        //ширина элементов + отступы равна ширине содержащего элемента
        //ie в этом случае переносит последний элемент на следующую строку, для этого -0.5
        saveButton.getElement().getStyle().setMarginLeft(leftMargin - 0.5, Style.Unit.PX);
        saveButton.getElement().getStyle().setMarginRight(rightMargin - 0.5, Style.Unit.PX);
    }

    /**
     * Изменяет видимость левой кнопки
     */
    public void setLeftButtonVisible(boolean visible) {
        leftButton.setVisible(visible);
        adjustSaveButtonMargin();
    }

    /**
     * Изменяет видимость правой кнопки
     */
    public void setRightButtonVisible(boolean visible) {
        rightButton.setVisible(visible);
        adjustSaveButtonMargin();
    }
}
