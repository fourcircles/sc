package kz.arta.synergy.components.client.button;

import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.MouseStyle;
import kz.arta.synergy.components.client.util.WidthUtil;

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
        setFontStyle(SynergyComponents.getResources().cssComponents().mainText());
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
                super.onBrowserEvent(event);
                break;
            case Event.ONMOUSEOVER:
                over = true;
                getElement().getStyle().setZIndex(5);
                super.onBrowserEvent(event);
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

    /**
     * Перевести кнопку в нажатое состояние
     * @param state нажатое состояние или нет
     */
    public void setState(boolean state) {
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
                    //noinspection NonJREEmulationClassesInClientCode
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

    /**
     * Меняется стиль в зависимости от состояния кнопки
     * @param pressed   нажатое состояние либо нет
     */
    private void setPressed(boolean pressed) {
        this.pressed = pressed;
        if (pressed) {
            getElement().getStyle().setZIndex(2);
            MouseStyle.setPressed(this);
            setFontStyle(SynergyComponents.getResources().cssComponents().mainTextBold());
        } else {
            if (!over) {
                getElement().getStyle().setZIndex(1);
            }
            MouseStyle.removeAll(this);
            setFontStyle(SynergyComponents.getResources().cssComponents().mainText());
        }
    }

    public void setGroupPanel(GroupButtonPanel groupPanel) {
        this.groupPanel = groupPanel;
    }

    public void onLoad() {
        super.onLoad();
        if (!isPressed()) {
            setFontStyle(SynergyComponents.getResources().cssComponents().mainTextBold());
        }
        setWidth(WidthUtil.getWidth(getElement()) + "px");
        if (!isPressed()) {
            setFontStyle(SynergyComponents.getResources().cssComponents().mainText());
        }
    }

}
