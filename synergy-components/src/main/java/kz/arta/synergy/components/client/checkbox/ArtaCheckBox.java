package kz.arta.synergy.components.client.checkbox;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import kz.arta.synergy.components.client.SynergyComponents;

import java.util.HashSet;
import java.util.Set;

/**
 * User: vsl
 * Date: 22.08.14
 * Time: 8:51
 *
 * Чекбокс
 */
public class ArtaCheckBox extends com.google.gwt.user.client.ui.CheckBox {
    /**
     * Множество чекбоксов в группе со значением true
     */
    private Set<ArtaCheckBox> on;
    /**
     * Множество чекбоксов в группе со значением false
     */
    private Set<ArtaCheckBox> off;
    /**
     * Хендлер для изменения значений чекбоксов в группе при изменении главного
     */
    private ValueChangeHandler<Boolean> groupHandler;
    /**
     * Хендлер для чекбоксов группы
     */
    ValueChangeHandler<Boolean> handler;

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

    /**
     * Изменяет вид чекбокса в соответствии с состоянием чекбоксов в группе
     */
    private void update() {
        if (off.isEmpty()) {
            setValue(true, false);
            removeStyleName(SynergyComponents.resources.cssComponents().group());
            return;
        }
        if (on.isEmpty()) {
            setValue(false, false);
            removeStyleName(SynergyComponents.resources.cssComponents().group());
            return;
        }
        setValue(true, false);
        addStyleName(SynergyComponents.resources.cssComponents().group());
    }

    /**
     * Создает хендлер для чекбоксов группы
     */
    private ValueChangeHandler<Boolean> getHandler() {
        if (handler == null) {
            handler = new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    ArtaCheckBox box = (ArtaCheckBox) event.getSource();
                    if (box.getValue()) {
                        off.remove(box);
                        on.add(box);
                    } else {
                        on.remove(box);
                        off.add(box);
                    }
                    update();
                }
            };
        }
        return handler;
    }

    /**
     * Добавляет чекбокс в группу
     * @param checkBox чекбокс, который надо добавить в группу
     */
    public void add(ArtaCheckBox checkBox) {
        if (groupHandler == null) {
            groupHandler = new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {

                    for (ArtaCheckBox box: on) {
                        box.setValue(event.getValue(), false);
                    }
                    for (ArtaCheckBox box: off) {
                        box.setValue(event.getValue(), false);
                    }
                    if (event.getValue()) {
                        on.addAll(off);
                        off.clear();
                    } else {
                        off.addAll(on);
                        on.clear();
                    }
                    removeStyleName(SynergyComponents.resources.cssComponents().group());
                }
            };
            addValueChangeHandler(groupHandler);
        }
        if (on == null) {
            on = new HashSet<ArtaCheckBox>();
        }
        if (off == null) {
            off = new HashSet<ArtaCheckBox>();
        }
        if (checkBox.getValue()) {
            on.add(checkBox);
        } else {
            off.add(checkBox);
        }
        checkBox.addValueChangeHandler(getHandler());
    }

    /**
     * Удаляет чекбокс из группы
     * @param checkBox чекбокс
     */
    public void remove(ArtaCheckBox checkBox) {
        on.remove(checkBox);
        off.remove(checkBox);
    }

}
