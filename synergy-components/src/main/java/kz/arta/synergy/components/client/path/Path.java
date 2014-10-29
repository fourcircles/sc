package kz.arta.synergy.components.client.path;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.button.ImageButton;
import kz.arta.synergy.components.client.resources.ImageResources;

import java.util.List;

/**
 * User: vsl
 * Date: 28.10.14
 * Time: 15:31
 *
 * Компонент путь
 */
public class Path extends Composite {
    /**
     * Корневая панель
     */
    FlowPanel root;

    private EventBus bus;

    /**
     * Кнопка элемента
     */
    ImageButton button;

    public Path() {
        bus = new SimpleEventBus();

        root = new FlowPanel();
        initWidget(root);
        root.setStyleName(SynergyComponents.getResources().cssComponents().path());

        button = new ImageButton(ImageResources.IMPL.favouriteFolder());
        root.add(button);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        bus.fireEventFromSource(event, this);
    }

    /**
     * Удалить все элементы пути из компонента
     */
    public void clear() {
        while (root.getWidgetCount() > 1) {
            root.getWidget(0).removeFromParent();
        }
    }

    /**
     * Загрузить новый путь.
     * Предполагается, что ширина компонента позволяет добавить хотя бы один элемент пути (последний).
     * Будут отображаться только последние элементы, которые входят в компонент
     * @param items список элементов пути
     */
    public void setPath(List<PathItem> items) {
        clear();
        root.insert(items.get(items.size() - 1), 0);
        for (int i = items.size() - 2; i >= 0; i--) {
            PathItem item = items.get(i);
            root.insert(item, 0);
            if (item.getAbsoluteTop() != items.get(items.size() - 1).getAbsoluteTop()) {
                item.removeFromParent();
                break;
            }
        }
    }

    /**
     * Добавляет хэнедлер для клика по кнопке компонента
     * @param handler хэндлер
     */
    public HandlerRegistration addButtonClickHandler(ClickHandler handler) {
        return button.addClickHandler(handler);
    }
}
