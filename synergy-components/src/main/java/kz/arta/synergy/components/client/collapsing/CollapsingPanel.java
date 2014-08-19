package kz.arta.synergy.components.client.collapsing;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.client.util.ArtaHasText;

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
        contentPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
        contentPanel.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);

        root.add(contentPanel);
    }

    /**
     * Открыть панель
     */
    public void open() {
        isOpen = true;
        contentPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        arrow.setResource(ImageResources.IMPL.whiteButtonDropdown());
    }

    /**
     * Закрыть панель
     */
    public void close() {
        isOpen = false;
        contentPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
        arrow.setResource(ImageResources.IMPL.navigationRight());
    }

    /**
     * Открыта ли панель
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Возвращает панель содержимого, на которую надо добавлять компоненты
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
}
