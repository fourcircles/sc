package kz.arta.synergy.components.client.stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.ArtaHasText;

/**
 * User: vsl
 * Date: 19.08.14
 * Time: 17:51
 *
 * Одна стек-панель.
 *
 * Никаких событий не генерирует, вся обработка действий пользователя
 * в StackPanel. Управляется через open, close.
 */
public class Stack extends Composite implements HasEnabled, ArtaHasText, HasClickHandlers {
    /**
     * Корневая панель
     */
    private ArtaFlowPanel root;

    /**
     * Включена ли панель
     */
    private boolean isEnabled = true;

    /**
     * Текст
     */
    private final InlineLabel label;

    /**
     * Открыта ли панель
     */
    private boolean isOpen = false;

    /**
     * Панель для содержимого
     */
    private FlowPanel contentPanel;

    SimplePanel contentContainer;

    /**
     * @param text текст панели
     */
    public Stack(String text) {
        root = new ArtaFlowPanel();
        initWidget(root);

        root.setStyleName(SynergyComponents.resources.cssComponents().stack());

        label = GWT.create(InlineLabel.class);
        label.setText(text);
        label.setStyleName(SynergyComponents.resources.cssComponents().title());
        label.addStyleName(getFontStyle());
        root.add(label);

        FlowPanel indicator = new FlowPanel();
        indicator.setStyleName(SynergyComponents.resources.cssComponents().indicator());
        root.add(indicator);

        contentPanel = new FlowPanel();
        contentPanel.setStyleName(SynergyComponents.resources.cssComponents().stackContent());

        contentContainer = new SimplePanel(contentPanel);
        contentContainer.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);

        root.add(contentContainer);

        close();
    }

    /**
     * Включена ли панель
     */
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Включить/выключить панель
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;

        if (enabled) {
            root.removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            root.addStyleName(SynergyComponents.resources.cssComponents().disabled());
        }
    }

    @Override
    public String getFontStyle() {
        return SynergyComponents.resources.cssComponents().bigText();
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Открыть панель
     */
    public void open() {
        isOpen = true;
        root.addStyleName(SynergyComponents.resources.cssComponents().open());
    }

    /**
     * Закрыть панель
     */
    public void close() {
        isOpen = false;
        root.removeStyleName(SynergyComponents.resources.cssComponents().open());
    }

    /**
     * Возвращает панель содержимого
     * @return панель
     */
    public FlowPanel getPanel() {
        return contentPanel;
    }

    public void setContentHeight(int height) {
        contentPanel.setHeight(height + "px");
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return label.addClickHandler(handler);
    }
}
