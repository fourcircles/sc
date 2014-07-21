package kz.arta.synergy.components.client.button;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.MouseStyle;

/**
 * User: user
 * Date: 16.07.14
 * Time: 9:14
 * Кнопка с нажатым состоянием
 */
public class SimpleToggleButton extends SimpleButton {

    /**
     * Нажата ли кнопка
     */
    private boolean pressed = false;

    /**
     * Наведена ли кнопка
     */
    private boolean over = false;

    /**
     * Группа, к которой принадлежит кнопка
     */
    private GroupButtonPanel groupPanel;

    /**
     * Кнопка с текстом
     * @param text  текст
     */
    public SimpleToggleButton(String text) {
        super(text);
        setStyle();
    }

    /**
     * Кнопка с текстом и указанным типом границ
     * @param text  текст
     * @param borderType    тип границ
     */
    public SimpleToggleButton(String text, BorderType borderType) {
        super(text, borderType);
        setStyle();

    }

    /**
     * Изменение стиля кнопка
     */
    private void setStyle() {
        getElement().getStyle().setProperty("minWidth", "100px");
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            getElement().getStyle().setMarginRight(-1, Style.Unit.PX);
        } else {
            getElement().getStyle().setMarginLeft(-1, Style.Unit.PX);
        }
        textLabel.removeStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainText());
        getElement().getStyle().setPosition(Style.Position.RELATIVE);
    }

    public void onBrowserEvent(Event event) {
        if (!enabled){
            return;
        }
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOUT:
                over = false;
                setPressed(pressed);
                if (!pressed) {
                    getElement().getStyle().setZIndex(1);
                }
                break;
            case Event.ONMOUSEOVER:
                over = true;
                getElement().getStyle().setZIndex(5);
                break;
            case Event.ONCLICK:
                pressed = !pressed;
                setPressed(pressed);
                if (groupPanel != null) {
                    if (groupPanel.isMultiToggle()){
                        setState(isPressed());
                    } else if (groupPanel.isToggle() && groupPanel.isAllowEmptyToggle()){
                        setState(isPressed());
                    } else if (groupPanel.isToggle()){
                        setState(true);
                    }
                }
                super.onBrowserEvent(event);
                break;
            default:
                super.onBrowserEvent(event);
        }

    }

    private void setState(boolean state) {
        if (groupPanel != null) {
            if (!groupPanel.isMultiToggle()) {
                if (groupPanel.pressedButton == this && !groupPanel.isAllowEmptyToggle()) {
                    groupPanel.pressedButton.setPressed(true);
                    return;
                }
                if (state) {
                    if (groupPanel.pressedButton != null) {
                        groupPanel.pressedButton.setPressed(false);
                    }
                    groupPanel.pressedButton = this;
                    groupPanel.pressedButton.setPressed(true);
                } else {
                    setPressed(false);
                    if (groupPanel.isAllowEmptyToggle() && this.equals(groupPanel.pressedButton)) {
                        groupPanel.pressedButton = null;
                    }
                }
            } else {
                setPressed(state);
            }
        }
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
        if (pressed) {
            getElement().getStyle().setZIndex(5);
            MouseStyle.setPressed(this);
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
            textLabel.removeStyleName(SynergyComponents.resources.cssComponents().mainText());
        } else {
            if (!over) {
                getElement().getStyle().setZIndex(1);
            }
            MouseStyle.removeAll(this);
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainText());
            textLabel.removeStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        }
    }

    public void setGroupPanel(GroupButtonPanel groupPanel) {
        this.groupPanel = groupPanel;
    }

    public void onLoad() {

    }

    /**
     * Получаем ширину элемента
     * @param element   элемент
     * @return  ширина элемента
     */
    public int getWidth(Element element) {
        Element e = DOM.clone(element, true);
        e.getStyle().setVisibility(Style.Visibility.HIDDEN);
        e.setClassName(textLabel.getStyleName());
        Document.get().getBody().appendChild(e);
        int width = e.getOffsetWidth();
        Document.get().getBody().removeChild(e);
        return width;
    }
}
