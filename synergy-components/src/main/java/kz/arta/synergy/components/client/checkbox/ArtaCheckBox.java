package kz.arta.synergy.components.client.checkbox;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.ImageResources;

/**
 * User: vsl
 * Date: 15.10.14
 * Time: 17:42
 *
 * Чекбокс
 */
public class ArtaCheckBox extends Composite implements HasValue<Boolean>, HasEnabled {
    private Image image;

    /**
     * Значение чекбокса
     */
    private boolean value = false;
    /**
     * Активен ли чекбокс
     */
    private boolean enabled = true;

    public ArtaCheckBox() {
        image = new Image();
        initWidget(image);

        setStyleName(SynergyComponents.getResources().cssComponents().checkbox());

        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (NativeEvent.BUTTON_LEFT == event.getNativeButton() && isEnabled()) {
                    setValue(!value);
                }
            }
        });

        updateIcon();
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        setValue(value, true);
    }

    /**
     * Меняет значение чекбокса
     * @param value новое значение
     * @param fireEvents создавать ли события
     */
    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        boolean changed = this.value != value;
        this.value = value;

        if (changed) {
            updateIcon();
            if (fireEvents) {
                ValueChangeEvent.fire(this, value);
            }
        }
    }

    /**
     * Обновляет вид чекбокса в соответствии с состоянием
     */
    private void updateIcon() {
        if (isEnabled()) {
            image.setResource(value ? ImageResources.IMPL.checkboxOn() : ImageResources.IMPL.checkboxOff());
        } else {
            image.setResource(value ? ImageResources.IMPL.checkboxOnDisabled() : ImageResources.IMPL.checkboxOffDisabled());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateIcon();
    }
}
