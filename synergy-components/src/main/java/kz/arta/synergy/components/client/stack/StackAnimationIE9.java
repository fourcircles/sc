package kz.arta.synergy.components.client.stack;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;

/**
 * User: vsl
 * Date: 22.08.14
 * Time: 11:21
 *
 * Анимация для открытия-закрытия стек-панелей (IE9)
 */
public class StackAnimationIE9 extends Animation {
    /**
     * Продолжительность анимации
     */
    private static final int DURATION = 250;

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

    /**
     * @param contentHeight высота контента
     */
    public StackAnimationIE9(int contentHeight) {
        this.contentHeight = contentHeight;
    }

    @Override
    protected void onUpdate(double progress) {
        int openingHeight = (int) (contentHeight * progress);

        if (closingStack != null) {
            closingStack.contentContainer.setHeight(contentHeight - openingHeight + "px");
        }
        if (openingStack != null) {
            openingStack.contentContainer.setHeight(openingHeight + "px");
        }
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        closingStack.contentContainer.getElement().getStyle().clearHeight();
        openingStack.contentContainer.getElement().getStyle().setHeight(contentHeight, Style.Unit.PX);
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

    /**
     * Закрывает closingStack, открывает openingStack
     * @param closingStack панель, которую надо закрыть
     * @param openingStack панель, которую надо открыть
     */
    public void openStack(Stack closingStack, Stack openingStack) {
        this.closingStack = closingStack;
        this.openingStack = openingStack;
        run(DURATION);
    }
}
