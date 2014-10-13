package kz.arta.synergy.components.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.util.Selection;

/**
 * Пункт меню
 */
public abstract class MenuItem extends Composite {
    /**
     * Корневая панель
     */
    ArtaFlowPanel root;

    /**
     * Иконка пункта меню
     */
    protected ImageResource icon;

    /**
     * Иконка
     */
    protected Image iconImage;

    /**
     * Текст пункта меню
     */
    protected Label label;

    public MenuItem() {
        root = new ArtaFlowPanel();
        initWidget(root);

        root.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                focus();
            }
        });
        root.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                focus();
            }
        });
        root.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                blurItem();
            }
        });
        root.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }
                select();
            }
        });

        label = GWT.create(Label.class);
        Selection.disableTextSelectInternal(label.getElement());
        label.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        label.getElement().getStyle().setProperty("wordBreak", "break-all");

        iconImage = GWT.create(Image.class);
        setIcon(null);
        root.add(iconImage);

        root.add(label);
        root.addStyleName(getMainStyle());
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String text) {
        label.setText(text);
    }

    public ImageResource getIcon() {
        return icon;
    }

    public void setIcon(ImageResource icon) {
        if (icon == null) {
            iconImage.getElement().getStyle().setDisplay(Style.Display.NONE);
        } else {
            iconImage.setResource(icon);
            iconImage.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        }
        this.icon = icon;
    }

    /**
     * Вызывается когда элемент был выбран
     */
    protected abstract void select();

    /**
     * Вызывается при выделении
     */
    protected void focus() {
        addStyleName(SynergyComponents.getResources().cssComponents().over());
    }

    /**
     * Вызывается при снятии выделения
     */
    protected void blurItem() {
        removeStyleName(SynergyComponents.getResources().cssComponents().over());
    }

    /**
     * Надо ли пропускать этот элемент при выборе пунктов меню клавиатурой.
     * Например, разделитель в контекстном меню - надо.
     */
    public boolean shouldBeSkipped() {
        return false;
    }

    /**
     * Название стиля для пункта меню
     * @return название стиля
     */
    protected String getMainStyle() {
        return SynergyComponents.getResources().cssComponents().contextMenuItem();
    }
}
