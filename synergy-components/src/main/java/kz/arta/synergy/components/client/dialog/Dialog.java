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

        leftButton = makeButton(Messages.i18n.tr("Назад"), ImageResources.IMPL.back(), ButtonBase.IconPosition.LEFT);
        leftButton.getElement().getStyle().setFloat(Style.Float.LEFT);
        saveButton = makeButton(Messages.i18n.tr("Сохранить"));
        rightButton = makeButton(Messages.i18n.tr("Еще"), ImageResources.IMPL.forward(), ButtonBase.IconPosition.RIGHT);
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
     * Метод вычисляет минимальную возможную ширину при которой могут выполняться следующие два условия:
     * 1. Кнопка "сохранить" должна быть равноудалена от левой и правой границы диалога.
     * 2. Минимальное расстояние между кнопкой навигации и кнопкой "сохранить" = {@value #MIN_INNER_MARGIN}
     */
    protected int getMinWidth() {
        int minLeft = (leftButton.isVisible() ? leftButton.getOffsetWidth() + HORIZONTAL_BUTTON_MARGIN: 0) + HORIZONTAL_BUTTON_MARGIN;
        int minRight = (rightButton.isVisible() ? rightButton.getOffsetWidth() + HORIZONTAL_BUTTON_MARGIN : 0) + HORIZONTAL_BUTTON_MARGIN;

        int minSaveButtonBaseMargin = Math.max(minLeft, minRight);
        int minButtonsPanelWidth = minSaveButtonBaseMargin * 2 + saveButton.getOffsetWidth();

        return minButtonsPanelWidth;
    }

    /**
     * Вычисляется величина соответствующего margin'а для кнопки "сохранить",
     * чтобы она оставалась в середине диалога.
     * @param navButton кнопка навигации
     * @return величина margin'а
     */
    protected double getSaveButtonMargin(SimpleButton navButton) {
        if (navButton.isVisible()) {
            return saveButtonBaseMargin - navButton.getOffsetWidth() - HORIZONTAL_BUTTON_MARGIN;
        } else {
            return saveButtonBaseMargin;
        }
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

        saveButtonBaseMargin = ((double) getOffsetWidth() -
                saveButton.getOffsetWidth() -
                Constants.DIALOG_BORDER_WIDTH * 2) / 2;

        double leftMargin = getSaveButtonMargin(leftButton);
        double rightMargin = getSaveButtonMargin(rightButton);

        if (leftMargin < MIN_INNER_MARGIN || rightMargin < MIN_INNER_MARGIN) {
            //в случае когда ширины диалога не хватает, ширина устанавливается минимальная возможная
            setWidth(getMinWidth() + "px");
            leftMargin = MIN_INNER_MARGIN;
            rightMargin = MIN_INNER_MARGIN;
        }

        saveButton.getElement().getStyle().setMarginLeft(leftMargin, Style.Unit.PX);
        saveButton.getElement().getStyle().setMarginRight(rightMargin, Style.Unit.PX);
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
