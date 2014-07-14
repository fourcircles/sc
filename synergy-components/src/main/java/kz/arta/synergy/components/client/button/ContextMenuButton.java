package kz.arta.synergy.components.client.button;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ImageResource;
import kz.arta.synergy.components.client.ContextMenu;
import kz.arta.synergy.components.client.SynergyComponents;
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
                if (contextMenu != null) {
                    if (contextMenu.isShowing()) {
                        contextMenu.hide();
                    } else {
                        contextMenu.showUnderParent();
                    }
                }

            }
        };
        contextButton.addDomHandler(down, MouseDownEvent.getType());
        contextButton.setStyleName(SynergyComponents.resources.cssComponents().dropDownButton());
        add(contextButton);
    }

    /**
     * В кнопке новый элемент - кнопка для открытия контекстного меню, ее ширину надо учитывать
     * при вычислении длины текста.
     * @return
     */
    @Override
    protected int getTextLabelWidth() {
        int width = super.getTextLabelWidth();
        return width - Constants.IMAGE_BUTTON_WIDTH;
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
            contextButton.getElement().getStyle().setLeft((double) delta / 2, Style.Unit.PX);
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
}
