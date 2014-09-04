package kz.arta.synergy.components.client.checkbox;


import kz.arta.synergy.components.client.SynergyComponents;

/**
 * User: vsl
 * Date: 21.08.14
 * Time: 17:35
 *
 * Радиокнопка
 */
public class ArtaRadioButton extends com.google.gwt.user.client.ui.RadioButton {
    public ArtaRadioButton(String name) {
        super(name);
        setStyleName(SynergyComponents.resources.cssComponents().radio());
    }

    /**
     * Включить-выключить радиокнопку
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            removeStyleName(SynergyComponents.resources.cssComponents().disabled());
        } else {
            addStyleName(SynergyComponents.resources.cssComponents().disabled());
        }
    }
}
