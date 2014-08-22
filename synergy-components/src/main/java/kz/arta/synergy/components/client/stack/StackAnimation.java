package kz.arta.synergy.components.client.stack;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 22.08.14
 * Time: 11:21
 *
 * Анимация для открытия-закрытия стек-панелей
 */
public class StackAnimation extends Animation {
    /**
     * Закрывающаяся панель
     */
    private Stack closingStack;

    /**
     * Открывающаяся панель
     */
    private Stack openingStack;

    /**
     * Высота контента
     */
    private int contentHeight;

    public StackAnimation(Stack closingStack, Stack openingStack, int contentHeight) {
        this.closingStack = closingStack;
        this.openingStack = openingStack;
        this.contentHeight = contentHeight;
    }

    @Override
    protected void onUpdate(double progress) {
        int height = (int) (contentHeight * progress);

        if (closingStack != null) {
            closingStack.contentContainer.setHeight(contentHeight - height + "px");
        }
        if (openingStack != null) {
            openingStack.contentContainer.setHeight(height + "px");
        }
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        cleanUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (openingStack != null) {
            openingStack.open();
            openingStack.contentContainer.setHeight("0px");
        }
        if (closingStack != null) {
            closingStack.close();
            closingStack.contentContainer.setHeight(contentHeight + "px");
            closingStack.getPanel().getElement().getStyle().setDisplay(Style.Display.BLOCK);
        }
    }

    /**
     * Убирает все inline свойства, которые были нужны для анимации
     */
    private void cleanUp() {
        if (openingStack != null) {
            openingStack.contentContainer.getElement().getStyle().clearHeight();
        }
        if (closingStack != null) {
            closingStack.contentContainer.getElement().getStyle().clearHeight();
            closingStack.getPanel().getElement().getStyle().clearDisplay();
        }
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        cleanUp();
    }

    public void setClosingStack(Stack closingStack) {
        this.closingStack = closingStack;
    }

    public void setOpeningStack(Stack openingStack) {
        this.openingStack = openingStack;
    }

    public void setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
    }
}
