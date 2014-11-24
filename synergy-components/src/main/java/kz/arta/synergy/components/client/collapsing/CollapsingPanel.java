package kz.arta.synergy.components.client.collapsing;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.SimpleButton;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Navigator;

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
     * Анимация для ie9
     */
    private CollapsingAnimationIE9 ie9Animation;

    /**
     * Панель для надписи, стерлки и кнопки
     */
    private final ArtaFlowPanel title;

    /**
     * Кнопка коллапсинг панели
     */
    private SimpleButton button;

    /**
     * @param titleText текст для коллапсинг-панели
     */
    public CollapsingPanel(String titleText) {
        FlowPanel root = new FlowPanel();
        initWidget(root);
        root.addStyleName(SynergyComponents.getResources().cssComponents().collapsingPanel());

        title = new ArtaFlowPanel();

        title.addStyleName(SynergyComponents.getResources().cssComponents().collapsingTitle());
        title.getElement().getStyle().setCursor(Style.Cursor.POINTER);

        titleLabel = new InlineLabel();
        titleLabel.setText(titleText);
        titleLabel.setStyleName(getFontStyle());

        arrow = new Image();
        arrow.setResource(ImageResources.IMPL.navigationRight());
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
        contentPanel.setStyleName(SynergyComponents.getResources().cssComponents().collapsingContent());

        root.add(contentContainer);
    }

    /**
     * Создает кнопку для коллапсинг панели.
     * Кнопка создается один раз, затем надо пользоваться методами {@link #hideButton()} и {@link #showButton()}
     *
     * @param buttonText текст кнопки
     */
    public void addButton(String buttonText) {
        if (button == null) {
            button = new SimpleButton(buttonText);
            button.getElement().getStyle().setFloat(Style.Float.RIGHT);
            button.getElement().getStyle().setMarginLeft(14, Style.Unit.PX);
            button.getElement().getStyle().setMarginTop(3, Style.Unit.PX);
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    event.stopPropagation();
                }
            });
        }
    }

    /**
     * Изменяет текст кнопки
     *
     * @param buttonText новый текст кнопки
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setButtonText(String buttonText) {
        if (button != null) {
            button.setText(buttonText);
        }
    }

    /**
     * Показывает кнопку
     */
    public void showButton() {
        title.add(button);
    }

    /**
     * Убрать кнопку
     */
    public void hideButton() {
        button.removeFromParent();
    }

    /**
     * Добавить хэндлер на клик кнопки. Перед тем, как добавлять хэндлер надо добавить кнопку {@link #addButton(String)},
     * потому что по умолчанию ее нет.
     *
     * @param handler хэндлер
     * @return регистрация хэндлера
     */
    public HandlerRegistration addButtonClickHandler(ClickHandler handler) {
        if (button != null) {
            return button.addClickHandler(handler);
        } else {
            return null;
        }
    }

    /**
     * Открыть панель
     */
    public void open() {
        isOpen = true;
        addStyleName(SynergyComponents.getResources().cssComponents().open());
        contentContainer.getElement().getStyle().setHeight(contentPanel.getElement().getPropertyInt("scrollHeight"), Style.Unit.PX);

        if (Navigator.isIE()) {
            if (ie9Animation == null) {
                ie9Animation = new CollapsingAnimationIE9(contentContainer,
                        contentPanel.getElement().getPropertyInt("scrollHeight"), arrow);
            }
            ie9Animation.open();
        }
    }

    /**
     * Закрыть панель
     */
    public void close() {
        isOpen = false;
        removeStyleName(SynergyComponents.getResources().cssComponents().open());

        contentContainer.getElement().getStyle().clearHeight();

        if (Navigator.isIE()) {
            if (ie9Animation == null) {
                ie9Animation = new CollapsingAnimationIE9(contentContainer,
                        contentPanel.getElement().getPropertyInt("scrollHeight"), arrow);
            }
            ie9Animation.close();
        }
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
        return SynergyComponents.getResources().cssComponents().bigText();
    }

    @Override
    public String getText() {
        return titleLabel.getText();
    }

    @Override
    public void setText(String text) {
        titleLabel.setText(text);
    }
}
