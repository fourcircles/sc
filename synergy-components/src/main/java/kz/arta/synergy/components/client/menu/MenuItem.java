package kz.arta.synergy.components.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import kz.arta.synergy.components.client.ArtaFlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.events.SelectionEvent;
import kz.arta.synergy.components.client.util.Selection;

/**
 * Пункт меню
 */
public class MenuItem extends Composite {
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

    /**
     * EventBus на который публикуются события выделения пункта меню
     */
    protected EventBus bus;

    public MenuItem() {
        root = new ArtaFlowPanel();
        initWidget(root);

        root.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                focusItem();
            }
        });
        root.addMouseMoveHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                focusItem();
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
                selectItem();
            }
        });

        label = GWT.create(Label.class);
        Selection.disableTextSelectInternal(label.getElement());
        label.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        label.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        label.getElement().getStyle().setProperty("wordBreak", "break-all");

        iconImage = GWT.create(Image.class);
        iconImage.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
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
            iconImage.setResource(null);
            iconImage.getElement().getStyle().setMarginRight(0, Style.Unit.PX);
        } else {
            iconImage.setResource(icon);
            iconImage.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        }
        this.icon = icon;
    }

    /**
     * Вызывается когда элемент был выбран
     */
    protected void selectItem() {
        bus.fireEvent(new SelectionEvent<MenuItem>(this));
    }

    /**
     * Вызывается при выделении
     */
    protected void focusItem() {
        addStyleName(SynergyComponents.resources.cssComponents().over());
    }

    /**
     * Вызывается при снятии выделения
     */
    protected void blurItem() {
        removeStyleName(SynergyComponents.resources.cssComponents().over());
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
        return SynergyComponents.resources.cssComponents().contextMenuItem();
    }
}
