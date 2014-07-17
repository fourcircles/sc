package kz.arta.synergy.components.client.button;

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
     * Группа, к которой принадлежит кнопка
     */
    private GroupButtonPanel groupPanel;

    public SimpleToggleButton(String text) {
        super(text);
        setStyle();
    }

    public SimpleToggleButton(String text, BorderType borderType) {
        super(text, borderType);
        setStyle();

    }

    public void setStyle() {
        getElement().getStyle().setProperty("minWidth", "100px");
        textLabel.removeStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainText());

    }

    public void onBrowserEvent(Event event) {
        if (!enabled){
            return;
        }
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOUT:
                setPressed(pressed);
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
            MouseStyle.setPressed(this);
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
            textLabel.removeStyleName(SynergyComponents.resources.cssComponents().mainText());
        } else {
            MouseStyle.removeAll(this);
            textLabel.addStyleName(SynergyComponents.resources.cssComponents().mainText());
            textLabel.removeStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        }
    }

    public void setGroupPanel(GroupButtonPanel groupPanel) {
        this.groupPanel = groupPanel;
    }
}
