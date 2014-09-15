package kz.arta.synergy.components.client.tree;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

/**
 * User: vsl
 * Date: 15.09.14
 * Time: 12:05
 *
 * Анимация дерева для IE9
 */
public class TreeAnimationIE9 extends Animation {
    /**
     * Элемент у которого изменяется высота
     */
    private Element element;

    /**
     * Высота в начале анимации
     */
    private int oldHeight;

    /**
     * Высота в конце анимации
     */
    private int newHeight;

    public TreeAnimationIE9(Element element) {
        this.element = element;
    }

    @Override
    protected void onUpdate(double progress) {
        int delta = newHeight - oldHeight;
        element.getStyle().setHeight(oldHeight + progress * delta, Style.Unit.PX);
    }

    /**
     * Изменить высоту элемента
     */
    public void updateHeight(int oldHeight, int newHeight) {
        this.oldHeight = oldHeight;
        this.newHeight = newHeight;
        run(TreeItem.ANIMATION_DURATION);
    }

    @Override
    protected void onStart() {
        super.onStart();
        element.getStyle().setHeight(oldHeight, Style.Unit.PX);
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        element.getStyle().setHeight(newHeight, Style.Unit.PX);
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        element.getStyle().setHeight(newHeight, Style.Unit.PX);
    }
}
