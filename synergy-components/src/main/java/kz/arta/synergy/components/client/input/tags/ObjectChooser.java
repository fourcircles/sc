package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.resources.ImageResources;
import kz.arta.synergy.components.style.client.Constants;

/**
 * User: vsl
 * Date: 31.07.14
 * Time: 18:00
 *
 * Компонент выбора объекта
 */
public class ObjectChooser extends Composite{
    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Кнопка
     */
    private ImageButton button;

    /**
     * Панель с тегами
     */
    private TagsPanel tagsPanel;

    /**
     * Длина компонента без margins
     */
    private int offsetWidth;

    public ObjectChooser(final EventBus bus) {
        root = new FlowPanel();
        initWidget(root);

        addStyleName(SynergyComponents.resources.cssComponents().mainText());
        addStyleName(SynergyComponents.resources.cssComponents().tagInput());
        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);

        offsetWidth = Constants.FIELD_WITH_BUTTON_MIN_WIDTH;
        super.setWidth(offsetWidth - 2 * Constants.BORDER_WIDTH + "px");

        button = new ImageButton(ImageResources.IMPL.zoom());
        button.getElement().getStyle().setFloat(Style.Float.RIGHT);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Tag tag = new Tag("t");
                tag.setBus(bus);
                bus.fireEvent(new TagAddEvent(tag));
            }
        });

        tagsPanel = new TagsPanel(bus, getAvailableWidth());

        root.add(tagsPanel);
        root.add(button);
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
        tagsPanel.setMaxWidth(getAvailableWidth());
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("надо использовать setWidth(int) для задания ширины");
    }
}
