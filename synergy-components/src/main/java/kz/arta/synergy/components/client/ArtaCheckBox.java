package kz.arta.synergy.components.client;

/**
 * User: vsl
 * Date: 22.08.14
 * Time: 8:51
 *
 * Чекбокс
 */
public class ArtaCheckBox extends com.google.gwt.user.client.ui.CheckBox{
    public ArtaCheckBox() {
        super();
        setStyleName(SynergyComponents.resources.cssComponents().checkbox());
    }

    /**
     * Включает-выключает чекбокс
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
