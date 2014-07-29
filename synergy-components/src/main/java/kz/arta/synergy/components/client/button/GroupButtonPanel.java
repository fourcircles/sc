package kz.arta.synergy.components.client.button;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.WidthUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: user
 * Date: 16.07.14
 * Time: 9:15
 * Группа кнопок
 */
public class GroupButtonPanel extends Composite {

    FlowPanel panel;
    /**
     * режим переключателей
     */
    private boolean toggle = false;

    /**
     * множественный выбор при режиме переключателей
     */
    private boolean multiToggle = false;

    /**
     * обязательное нажатие хотя одной кнопки в группе
     */
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
     * Группа toggle кнопок
     */
    public GroupButtonPanel(boolean toggle) {
        this(toggle, false);
    }

    /**
     * Группа toggle кнопок с множественным выбором
     */
    public GroupButtonPanel(boolean toggle, boolean multiToggle) {
        panel = new FlowPanel();
        initWidget(panel);
        this.toggle = toggle;
        this.multiToggle = multiToggle;
        init();
    }

    private void init() {
        setStyleName(SynergyComponents.resources.cssComponents().groupButtonPanel());
    }

    /**
     * Добавление кнопки на панель
     * @param buttonText  текст кнопки
     * @param handler     хендлер
     */
    public void addButton(String buttonText, ClickHandler handler) {
        TempButton buttonBase = new TempButton(buttonText, handler);
        buttons.add(buttonBase);
    }

    /**
     * Построение панели групповых кнопок
     */
    public void buildPanel() {
        int i = 0;
        for (TempButton button: buttons) {
            SimpleButton simpleButton;
            if ((i == 0 && !LocaleInfo.getCurrentLocale().isRTL()) || (i == buttons.size() - 1 && LocaleInfo.getCurrentLocale().isRTL())) {
                if (toggle) {
                    simpleButton = new SimpleToggleButton(button.getText(), SimpleButton.BorderType.LEFT);
                } else {
                    simpleButton = new SimpleButton(button.getText(), SimpleButton.BorderType.LEFT);
                }
            } else if ((i == 0 && LocaleInfo.getCurrentLocale().isRTL()) || (i == buttons.size() - 1 && !LocaleInfo.getCurrentLocale().isRTL())) {
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
            if (i == 0 && !allowEmptyToggle) {
                ((SimpleToggleButton) simpleButton).setState(true);
            }
            panel.add(simpleButton);
            i++;
        }
    }

    public boolean isAllowEmptyToggle() {
        return allowEmptyToggle;
    }

    public void setAllowEmptyToggle(boolean allowEmptyToggle) {
        this.allowEmptyToggle = allowEmptyToggle;
        if (!allowEmptyToggle && panel.getWidgetCount() != 0) {
            try {
                ((SimpleToggleButton)panel.getWidget(0)).setState(true);
            } catch (Exception ignore) {

            }
        }
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

    /**
     * Выравниваем ширину кнопок
     */
    public void onLoad() {
        super.onLoad();
        int maxWidth = 0;
        int maxI = 0;
        for (int i = 0; i < panel.getWidgetCount(); i++) {
            if (((ButtonBase) panel.getWidget(i)).getText().length() > maxWidth) {
                maxWidth = ((ButtonBase) panel.getWidget(i)).getText().length();
                maxI = i;
            }
        }
        maxWidth = WidthUtil.getWidth(panel.getWidget(maxI).getElement());
        for (int i = 0; i < panel.getWidgetCount(); i++) {
            panel.getWidget(i).setWidth(maxWidth + "px");
        }
    }
}
