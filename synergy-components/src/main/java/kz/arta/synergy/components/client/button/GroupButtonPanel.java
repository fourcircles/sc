package kz.arta.synergy.components.client.button;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * User: user
 * Date: 16.07.14
 * Time: 9:15
 * Группа кнопок
 */
public class GroupButtonPanel extends FlowPanel {

    private boolean toggle = false;

    private boolean multiToggle = false;

    private boolean allowEmptyToggle = true;

    /**
     * Список надписей кнопок
     */
    private List<TempButton> buttons = new ArrayList<TempButton>();

    /**
     * Нажатая кнопка
     */
    SimpleToggleButton pressedButton = null;

    /**
     * Группа кнопок
     */
    public GroupButtonPanel() {
        this(false);
    }

    /**
     * Группа кнопок
     */
    public GroupButtonPanel(boolean toggle) {
        this(toggle, false);
    }

    /**
     * Группа кнопок
     */
    public GroupButtonPanel(boolean toggle, boolean multiToggle) {
        this.toggle = toggle;
        this.multiToggle = multiToggle;
        init();
    }

    private void init() {
        setStyleName(SynergyComponents.resources.cssComponents().groupButtonPanel());
    }

    public void addButton(String buttonText, ClickHandler handler) {
        TempButton buttonBase = new TempButton(buttonText, handler);
        buttons.add(buttonBase);
    }

    public void buildPanel() {
        int i = 0;
        for (TempButton button: buttons) {
            SimpleButton simpleButton;
            if (i == 0) {
                if (toggle) {
                    simpleButton = new SimpleToggleButton(button.getText(), SimpleButton.BorderType.LEFT);
                } else {
                    simpleButton = new SimpleButton(button.getText(), SimpleButton.BorderType.LEFT);
                }
            } else if (i == buttons.size() - 1) {
                if (toggle) {
                    simpleButton = new SimpleToggleButton(button.getText(), SimpleButton.BorderType.RIGHT);
                } else {
                    simpleButton = new SimpleButton(button.getText(), SimpleButton.BorderType.RIGHT);
                }
            } else {
                if (toggle) {
                    simpleButton = new SimpleToggleButton(button.getText(), SimpleButton.BorderType.EDGE);
                } else {
                    simpleButton = new SimpleButton(button.getText(), SimpleButton.BorderType.EDGE);
                }
            }
            simpleButton.addClickHandler(button.handler);
            if (simpleButton instanceof  SimpleToggleButton) {
                ((SimpleToggleButton) simpleButton).setGroupPanel(this);
            }
            add(simpleButton);
            i++;
        }
    }

    public boolean isAllowEmptyToggle() {
        return allowEmptyToggle;
    }

    public void setAllowEmptyToggle(boolean allowEmptyToggle) {
        this.allowEmptyToggle = allowEmptyToggle;
    }

    public boolean isMultiToggle() {
        return multiToggle;
    }

    public void setMultiToggle(boolean multiToggle) {
        this.multiToggle = multiToggle;
    }

    public boolean isToggle() {
        return toggle;
    }

    private class TempButton {

        String text;
        ClickHandler handler;

        public TempButton(String text, ClickHandler handler) {
            this.text = text;
            this.handler = handler;
        }

        public String getText() {
            return text;
        }

    }
}
