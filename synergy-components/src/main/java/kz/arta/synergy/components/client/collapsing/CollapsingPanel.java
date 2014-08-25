package kz.arta.synergy.components.client.collapsing;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Utils;

/**
 * User: vsl
 * Date: 19.08.14
 * Time: 12:35
 *
 * Коллапсинг-панель
 *
 * Высота в закрытом состоянии - постоянна. В открытом зависит от содержимого.
 *
 * Ширина.
 * 1. Может быть растянута до границ родителя.
 * 2. Ширина может быть задана явно.
 * В любом случае наполнение панели содержимого getPanel() производится пользователем, любое
 * переполнение скрывается.
 *
 */
public class CollapsingPanel extends Composite implements ArtaHasText {

    /**
     * Панель для содержимого
     */
    private FlowPanel contentPanel;

    /**
     * Открыта ли панель
     */
    private boolean isOpen;

    /**
     * Картинка для стрелки
     */
    private final Image arrow;

    /**
     * Текст названия
     */
    private final InlineLabel titleLabel;

    /**
     * Контейнер для контента.
     * При анимации изменяется только его размеры для ускорения работы.
     */
    private final SimplePanel contentContainer;

    /**
     * Изменение высоты
     */
    private SlideAnimation slideAnimation = new SlideAnimation();

    /**
     * Изменение прозрачности
     */
    private FadeAnimation fadeAnimation = new FadeAnimation();

    public CollapsingPanel(String titleText) {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.addStyleName(SynergyComponents.resources.cssComponents().collapsingPanel());

        ArtaFlowPanel title = new ArtaFlowPanel();

        title.addStyleName(SynergyComponents.resources.cssComponents().collapsingTitle());
        title.getElement().getStyle().setCursor(Style.Cursor.POINTER);

        titleLabel = new InlineLabel();
        titleLabel.setText(titleText);
        titleLabel.setStyleName(getFontStyle());

        arrow = new Image();
        arrow.setResource(ImageResources.IMPL.navigationRight());
//        if (LocaleInfo.getCurrentLocale().isRTL()) {
//            arrow.setResource(ImageResources.IMPL.navigationLeft());
//        } else {
//            arrow.setResource(ImageResources.IMPL.navigationRight());
//        }
        arrow.getElement().getStyle().setMarginTop(11, Style.Unit.PX);
        arrow.getElement().getStyle().setMarginLeft(14, Style.Unit.PX);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            arrow.getElement().getStyle().setFloat(Style.Float.LEFT);
        } else {
            arrow.getElement().getStyle().setFloat(Style.Float.RIGHT);
        }

        title.add(titleLabel);
        title.add(arrow);

        root.add(title);

        title.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (fadeAnimation.isRunning() || slideAnimation.isRunning()) {
                    return;
                }
                if (isOpen) {
                    close();
                } else {
                    open();
                }
            }
        });

        contentPanel = new FlowPanel();
        contentContainer = new SimplePanel(contentPanel);
        contentContainer.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        contentPanel.setStyleName(SynergyComponents.resources.cssComponents().collapsingContent());

        root.add(contentContainer);
    }

    /**
     * Открыть панель
     */
    public void open() {
        slideAnimation.open();
        isOpen = true;
        addStyleName(SynergyComponents.resources.cssComponents().open());
//        arrow.setResource(ImageResources.IMPL.whiteButtonDropdown());
    }

    /**
     * Закрыть панель
     */
    public void close() {
        fadeAnimation.fadeOut();
        isOpen = false;
        removeStyleName(SynergyComponents.resources.cssComponents().open());
//        arrow.setResource(ImageResources.IMPL.navigationRight());
    }

    /**
     * Открыта ли панель
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Возвращает панель содержимого, на которую надо добавлять компоненты
     *
     * Не рекомендуется изменять display на что-нибудь кроме block, потому
     * высота, которая используется для анимации вычисляется именно при этом
     * значении
     */
    public FlowPanel getPanel() {
        return contentPanel;
    }

    @Override
    public String getFontStyle() {
        return SynergyComponents.resources.cssComponents().bigText();
    }

    @Override
    public String getText() {
        return titleLabel.getText();
    }

    @Override
    public void setText(String text) {
        titleLabel.setText(text);
    }

    /**
     * Постепенное изменение прозрачности
     */
    public class FadeAnimation extends Animation {
        /**
         * Скрывать или проявлять контент
         */
        private boolean open;

        @Override
        protected void onUpdate(double progress) {
            if (open) {
                contentPanel.getElement().getStyle().setOpacity(progress);
            } else {
                contentPanel.getElement().getStyle().setOpacity(1.0 - progress);
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            if (!open) {
                contentContainer.setHeight(contentPanel.getOffsetHeight() + "px");
                contentPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            }
        }

        @Override
        protected void onComplete() {
            super.onComplete();
            contentPanel.getElement().getStyle().clearOpacity();

            if (!open) {
                slideAnimation.close();
            }
        }

        /**
         * Проявить контент
         */
        public void fadeIn() {
            open = true;
            run(300);
        }

        /**
         * Скрыть контент
         */
        public void fadeOut() {
            open = false;
            run(300);
        }
    }

    /**
     * Постепенное изменение размера контента
     */
    public class SlideAnimation extends Animation {
        /**
         * Выдвигать или задвигать контент
         */
        private boolean open;

        /**
         * Начальная высота
         */
        private int startHeight;

        /**
         * Показать контент
         */
        public void open() {
            open = true;
            run(300);
        }

        /**
         * Скрыть контент
         */
        public void close() {
            open = false;
            run(300);
        }

        @Override
        protected void onStart() {
            super.onStart();
            contentPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            startHeight = contentPanel.getElement().getPropertyInt("scrollHeight");

            if (open) {
                contentContainer.getElement().getStyle().setHeight(0, Style.Unit.PX);
                //скрываем контент перед открытием
                contentPanel.getElement().getStyle().setOpacity(0);

//                Utils.setRotate(arrow.getElement(), 0);
            } else {
                contentPanel.getElement().getStyle().setOpacity(0);
                contentContainer.getElement().getStyle().setHeight(startHeight, Style.Unit.PX);

//                Utils.setRotate(arrow.getElement(), 90);
            }
        }

        private void cleanUp() {
            contentPanel.getElement().getStyle().clearDisplay();
            contentContainer.getElement().getStyle().clearHeight();
            contentPanel.getElement().getStyle().clearOpacity();
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

            if (open) {
                fadeAnimation.fadeIn();
            }
        }

        @Override
        protected void onUpdate(double progress) {
            if (open) {
                contentContainer.getElement().getStyle().setHeight(startHeight * progress, Style.Unit.PX);
//                Utils.setRotate(arrow.getElement(), (int) (90 * progress));
            } else {
                contentContainer.getElement().getStyle().setHeight(startHeight * (1 - progress), Style.Unit.PX);
//                Utils.setRotate(arrow.getElement(), (int) (90 * (1 - progress)));
            }
        }
    }

}
