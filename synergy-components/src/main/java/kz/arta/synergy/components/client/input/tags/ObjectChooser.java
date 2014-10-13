package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

import java.util.List;

/**
 * User: vsl
 * Date: 31.07.14
 * Time: 18:00
 *
 * Компонент выбора объекта
 */
public class ObjectChooser<T> extends TagsContainer<T> implements HasEnabled{

    public static final ImageResource DEFAULT_ICON = ImageResources.IMPL.zoom();
    /**
     * Панель с тегами
     */
    private TagsPanel<T> tagsPanel;

    /**
     * Длина компонента без margins
     */
    private int offsetWidth;

    /**
     * Кнопка
     */
    private final ImageButton button;

    public ObjectChooser(final EventBus bus, ImageResource icon) {
        super(bus);
        //корневая панель
        FlowPanel root = new FlowPanel();
        initWidget(root);

        addStyleName(SynergyComponents.getResources().cssComponents().mainText());
        addStyleName(SynergyComponents.getResources().cssComponents().tagInput());
        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        offsetWidth = Constants.FIELD_WITH_BUTTON_MIN_WIDTH;
        super.setWidth(offsetWidth - 2 * Constants.BORDER_WIDTH + "px");

        //кнопка
        button = new ImageButton(icon);
        button.getElement().getStyle().setFloat(Style.Float.RIGHT);

        tagsPanel = new TagsPanel<T>(bus, getAvailableWidth());

        root.add(tagsPanel);
        root.add(button);
    }

    @Override
    public List<Tag<T>> getTags() {
        return tagsPanel.getTags();
    }

    public ObjectChooser(EventBus bus) {
        this(bus, DEFAULT_ICON);
    }

    public void setIcon(ImageResource icon) {
        button.setIcon(icon);
    }

    public HandlerRegistration addButtonClickHandler(ClickHandler handler) {
        return button.addClickHandler(handler);
    }

    /**
     * Возвращает максимальную ширину поля с тегами
     * @return ширина поля с тегами
     */
    private int getAvailableWidth() {
        int res = offsetWidth;
        res -= Constants.IMAGE_BUTTON_WIDTH;
        res -= 3 * Constants.BORDER_WIDTH;
        return res;
    }

    /**
     * Задать offset-ширину.
     * Задание ширины должно производиться через этот метод.
     * @param width offset-ширина
     */
    public void setWidth(int width) {
        offsetWidth = width;
        super.setWidth(width + "px");
        tagsPanel.setMaxWidth(getAvailableWidth());
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("надо использовать setWidth(int) для задания ширины");
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
        tagsPanel.setEnabled(enabled);
        if (!enabled) {
            addStyleName(SynergyComponents.getResources().cssComponents().disabled());
        } else {
            removeStyleName(SynergyComponents.getResources().cssComponents().disabled());
        }
    }
}
