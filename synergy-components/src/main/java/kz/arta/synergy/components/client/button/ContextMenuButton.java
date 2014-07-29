package kz.arta.synergy.components.client.button;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.menu.ContextMenu;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 10.07.14
 * Time: 10:49
 * Кнопка с контекстным меню
 */
public class ContextMenuButton extends SimpleButton {
    /**
     * Кнопка для открытия контекстного меню
     */
    private ImageButton contextButton;

    /**
     * Контекстное меню
     */
    private ContextMenu contextMenu;

    public ContextMenuButton() {
    }

    public ContextMenuButton(String text) {
        super(text);
    }

    public ContextMenuButton(String text, ImageResource iconResource) {
        super(text, iconResource);
    }

    public ContextMenuButton(String text, ImageResource iconResource, IconPosition position) {
        super(text, iconResource, position);
    }

    public ContextMenuButton(String text, Type type) {
        super(text, type);
    }

    @Override
    protected void init() {
        super.init();
        if (type == Type.APPROVE) {
            contextButton = new ImageButton(ImageResources.IMPL.greenButtonDropdown());
        } else {
            contextButton = new ImageButton(ImageResources.IMPL.whiteButtonDropdown());
        }
        MouseDownHandler down = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        };
        ClickHandler click = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                if (contextMenu != null) {
                    if (contextMenu.isShowing()) {
                        contextMenu.hide();
                    } else {
                        contextMenu.show();
                    }
                }
            }
        };
        contextButton.addDomHandler(down, MouseDownEvent.getType());
        contextButton.addDomHandler(click, ClickEvent.getType());

        contextButton.setStyleName(SynergyComponents.resources.cssComponents().dropDownButton());
        add(contextButton);

        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (contextMenu != null) {
                    contextMenu.hide();
                }
            }
        });
    }

    /**
     * В кнопке новый элемент - кнопка для открытия контекстного меню, ее ширину надо учитывать
     * при вычислении длины текста.
     * @return
     */
    @Override
    protected int getTextLabelWidth() {
        int width = super.getTextLabelWidth();
        //-1 потому что правая граница перекрывает границу кнопки
        return width - (Constants.IMAGE_BUTTON_WIDTH + 1);
    }

    /**
     * Если ширина кнопки большая, то все элементы должны центрироваться. Кроме кнопки открытия контекстного
     * меню. В этом случае вычисляется необходимый сдвиг для этой кнопки.
     */
    @Override
    protected void adjustMargins() {
        if (!isAttached()) {
            return;
        }
        int textLabelWidth = getTextLabelWidth();
        int delta = textLabelWidth - textLabel.getOffsetWidth();
        if (delta > 0) {
            contextButton.getElement().getStyle().setPosition(Style.Position.RELATIVE);
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                contextButton.getElement().getStyle().setRight((double) delta / 2, Style.Unit.PX);
            } else {
                contextButton.getElement().getStyle().setLeft((double) delta / 2, Style.Unit.PX);
            }
        }
        super.adjustMargins();
    }

    /**
     * Указывает контекстное меню кнопки
     * @param contextMenu контекстное меню
     */
    public void setContextMenu(ContextMenu contextMenu) {
        if (type == Type.APPROVE) {
            contextMenu.addStyleName(SynergyComponents.resources.cssComponents().green());
        }
        contextMenu.setRelativeWidget(this);
        this.contextMenu = contextMenu;
    }


    @Override
    protected void buildButton() {
        super.buildButton();
        if (contextButton != null) {
            add(contextButton);
        }
    }
}
