package kz.arta.synergy.components.client.collapsing;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * User: vsl
 * Date: 25.08.14
 * Time: 15:22
 *
 * Анимация закрытия и открытия коллапсинг панели для IE9
 * (для остальных браузеров это делается через css3 transitions)
 *
 * Желательно либо отменять уже выполняющуюся анимацию либо запрещать
 * клик по панели до завершения анимации.
 */
public class CollapsingAnimationIE9 extends Animation {
    /**
     * Длительность анимации
     */
    private static final int DURATION = 500;

    /**
     * Контейнер панели с контентом
     */
    private SimplePanel container;

    /**
     * Высота
     */
    private int height;

    /**
     * Анимация открытия или закрытия
     */
    private boolean isOpening;

    /**
     * Иконка, которую надо вертеть
     */
    private Image icon;

    public CollapsingAnimationIE9(SimplePanel container, int height, Image icon) {
        super();
        this.height = height;
        this.container = container;
        this.icon = icon;
    }

    @Override
    protected void onUpdate(double progress) {
        //иконка
        int degrees;
        if (isOpening) {
            degrees = (int) (90 * progress);
        } else {
            degrees = (int) (90 * (1 - progress));
        }
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            degrees = -degrees;
        }
        icon.getElement().getStyle().setProperty("msTransform", "rotate(" + degrees + "deg)");

        //панель
        progress *= 2;
        if (progress <= 1 && isOpening) {
            container.getElement().getStyle().setHeight(height * progress, Style.Unit.PX);
        } else if (progress > 1 && !isOpening) {
            progress -= 1;
            container.getElement().getStyle().setHeight(height * (1 - progress), Style.Unit.PX);
        } else if (progress > 1 && isOpening) {
            progress -= 1;
            container.getElement().getStyle().setOpacity(progress);
        } else if (progress <= 1 && !isOpening) {
            container.getElement().getStyle().setOpacity(1 - progress);
        }
        if (progress >= 0.5) {
            if (isOpening) {
                container.getElement().getStyle().setHeight(height, Style.Unit.PX);
            } else {
                container.getElement().getStyle().setOpacity(0);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isOpening) {
            container.getElement().getStyle().setOpacity(0);
            container.getElement().getStyle().setHeight(0, Style.Unit.PX);
        } else {
            container.getElement().getStyle().setOpacity(1);
            container.getElement().getStyle().setHeight(height, Style.Unit.PX);
        }
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        cleanUp();
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        cleanUp();
    }

    private void cleanUp() {
        container.getElement().getStyle().clearOpacity();
    }

    /**
     * Анимация открытия
     */
    public void open() {
        isOpening = true;
        run(DURATION);
    }

    /**
     * Анимация закрытия
     */
    public void close() {
        isOpening = false;
        run(DURATION);
    }
}
