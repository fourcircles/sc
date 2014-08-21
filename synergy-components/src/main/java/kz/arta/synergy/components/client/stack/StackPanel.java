package kz.arta.synergy.components.client.stack;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.stack.events.HasStackOpenHandlers;
import kz.arta.synergy.components.client.stack.events.StackOpenEvent;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * User: vsl
 * Date: 19.08.14
 * Time: 17:23
 *
 * Стек-панель
 */
public class StackPanel extends Composite implements HasStackOpenHandlers {
    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Открытая панель
     */
    private Stack openedStack;

    /**
     * Список стек-панелей
     */
    private List<Stack> stacks;

    /**
     * @param texts текст панелей
     * @param offsetHeight общая высота панели
     */
    public StackPanel(List<String> texts, int offsetHeight) {
        if (texts.size() == 0) {
            throw new UnsupportedOperationException("стек панель не может быть пустой");
        }

        root = new FlowPanel();
        initWidget(root);
        root.setHeight(offsetHeight + "px");

        root.setStyleName(SynergyComponents.resources.cssComponents().stackPanel());

        stacks = new ArrayList<Stack>();

        int contentHeight = offsetHeight - (texts.size() * (Constants.STACK_HEIGHT + 1)) - Constants.BORDER_WIDTH * 2;

        for (String text : texts) {
            final Stack stack = new Stack(text);
            stack.setContentHeight(contentHeight);
            stack.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (stack.isEnabled()) {
                        openStack(stack, true);
                    }
                }
            });
            stacks.add(stack);
            root.add(stack);
        }
        openStack(0, false);
    }

    /**
     * @param type тип панели (белая или черная)
     */
    public StackPanel(List<String> texts, int offsetHeight, Type type) {
        this(texts, offsetHeight);
        if (type == Type.WHITE) {
            root.addStyleName(SynergyComponents.resources.cssComponents().white());
        }
    }

    /**
     * Открыть панель на заданной позиции
     * @param index позиция
     * @param fireEvents создавать ли события
     */
    public void openStack(int index, boolean fireEvents) {
        openStack(stacks.get(index), fireEvents);
    }

    /**
     * Открыть панель
     * @param stack панель
     * @param fireEvents создавать ли события
     */
    public void openStack(Stack stack, boolean fireEvents) {
        if (openedStack != null) {
            openedStack.close();
        }
        openedStack = stack;
        openedStack.open();
        if (fireEvents) {
            fireEvent(new StackOpenEvent(stack, stacks.indexOf(stack)));
        }
    }

    public List<Stack> getStacks() {
        return stacks;
    }

    @Override
    public HandlerRegistration addStackOpenHandler(StackOpenEvent.Handler handler) {
        return addHandler(handler, StackOpenEvent.TYPE);
    }

    /**
     * Тип панели. Черная или белая.
     */
    public static enum Type {
        WHITE, BLACK
    }
}
