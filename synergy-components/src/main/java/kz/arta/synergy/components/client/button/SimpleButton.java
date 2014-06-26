package kz.arta.synergy.components.client.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.resources.Messages;

/**
 * User: user
 * Date: 23.06.14
 * Time: 11:11
 * Кнопка простая
 */
public class SimpleButton extends ButtonBase {

    /**
     * Текст кнопки
     */
    private String text = Messages.i18n.tr("Кнопка");

    /**
     * Иконка
     */
    private ImageResource iconResource;

    /**
     * Ширина кнопки
     */
    private int width = 32;

    private FlowPanel textPanel = GWT.create(FlowPanel.class);
    private InlineLabel textLabel = GWT.create(InlineLabel.class);

    /**
     * Кпопка простая с текстом
     * @param text  текст кнопки
     */
    public SimpleButton(String text) {
        super();
        this.text = text;
        init();
    }

    /**
     * Кнопка с иконкой
     * @param text  текст кнопки
     * @param iconResource  иконка кнопки
     */
    public SimpleButton(String text, ImageResource iconResource) {
        super();
        this.text = text;
        this.iconResource = iconResource;
        init();
    }

    protected void init() {
        super.init();

        setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
        addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());

        if (iconResource != null) {
            textPanel.add(new Image(iconResource.getSafeUri()));
        }
        textLabel.setText(text);
        textLabel.setStyleName(SynergyComponents.resources.cssComponents().paddingElement());
        textPanel.add(textLabel);
        add(textPanel);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        textLabel.setText(text);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
            addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        } else {
            setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleDisabled());
            addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
        }
    }

    public void onBrowserEvent(Event event) {
        if (!enabled){
            return;
        }
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
                setStyleName(SynergyComponents.resources.cssComponents().buttonSimplePressed());
                addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
                break;
            case Event.ONMOUSEOVER:
                setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleOver());
                addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
                break;
            case Event.ONMOUSEUP:
                setStyleName(SynergyComponents.resources.cssComponents().buttonSimpleOver());
                addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
                break;
            case Event.ONMOUSEOUT:
                setStyleName(SynergyComponents.resources.cssComponents().buttonSimple());
                addStyleName(SynergyComponents.resources.cssComponents().mainTextBold());
                break;
            case Event.ONCLICK:
                super.onBrowserEvent(event);

        }
    }


}
