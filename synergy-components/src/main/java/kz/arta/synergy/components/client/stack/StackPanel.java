package kz.arta.synergy.components.client.stack;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
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
 * Стек-панели
 *
 * Генерирует события открытия стек-панелей.
 * Не может быть состояния, когда закрыты все панели, поэтому по умолчанию открывается
 * первая панель или это можно указать в конструкторе.
 */
public class StackPanel extends Composite implements HasStackOpenHandlers {

    EventBus bus = new SimpleEventBus();
    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Открытая панель
     */
    private SingleStack openedStack;

    /**
     * Список стек-панелей
     */
    private List<SingleStack> stacks;

    /**
     * Высота контента
     */
    private int contentHeight;

    /**
     * Анимация для ie9
     */
    private StackAnimationIE9 ie9Animation;

    /**
     * @param stacks текст панелей
     * @param offsetHeight общая высота панели
     * @param initialOpened номер стек-панели открытой в начале
     */
    public StackPanel(List<SingleStack> stacks, int offsetHeight, int initialOpened) {
        if (stacks.isEmpty()) {
            throw new UnsupportedOperationException("стек панель не может быть пустой");
        }

        root = new FlowPanel();
        initWidget(root);
        root.setHeight(offsetHeight + "px");

        root.setStyleName(SynergyComponents.resources.cssComponents().stackPanel());

        this.stacks = new ArrayList<SingleStack>();

        contentHeight = offsetHeight - (stacks.size() * (Constants.STACK_HEIGHT + 1)) - Constants.BORDER_WIDTH * 2;

        for (final SingleStack stack : stacks) {
            stack.setContentHeight(contentHeight);
            stack.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    openStack(stack, true);
                }
            });
            this.stacks.add(stack);
            root.add(stack);
        }
        openStack(initialOpened, false);
    }

    public StackPanel(List<SingleStack> stacks, int offsetHeight) {
        this(stacks, offsetHeight, 0);
    }

    /**
     * @param type тип панели (белая или черная)
     */
    public StackPanel(List<SingleStack> stacks, int offsetHeight, Type type) {
        this(stacks, offsetHeight);
        if (type == Type.WHITE) {
            root.addStyleName(SynergyComponents.resources.cssComponents().white());
        }
    }

    /**
     * Добавляет новый стек на заданную позицию
     * Т. к. высота стек-панель статична и задается при создании возможна ситуация,
     * когда места для контента не останется.
     * @param newStack стек
     * @param beforeIndex позиция
     */
    public void insertStack(final SingleStack newStack, int beforeIndex) {
        if (beforeIndex < 0 || beforeIndex > stacks.size() - 1) {
            return;
        }
        newStack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openStack(newStack, true);
            }
        });
        stacks.add(beforeIndex, newStack);
        root.insert(newStack, beforeIndex);
        contentHeight -= Constants.STACK_HEIGHT;

        for (SingleStack stack : stacks) {
            stack.setContentHeight(contentHeight);
        }
    }

    /**
     * Удаляет стек из стекпанели
     * @param oldStack стек, который надо убрать
     */
    public void removeStack(SingleStack oldStack) {
        //не разрешаем удалять последний стек
        if (!stacks.contains(oldStack) || stacks.isEmpty()) {
            return;
        }
        stacks.remove(oldStack);
        root.remove(oldStack);
        contentHeight += Constants.STACK_HEIGHT;

        for (SingleStack stack : stacks) {
            stack.setContentHeight(contentHeight);
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
    public void openStack(SingleStack stack, boolean fireEvents) {
        if (openedStack == stack || !stack.isEnabled()) {
            return;
        }
        if (Window.Navigator.getAppVersion().contains("MSIE")) {
            if (ie9Animation == null) {
                ie9Animation = new StackAnimationIE9(contentHeight);
            } else {
                if (ie9Animation.isRunning()) {
                    ie9Animation.cancel();
                }
            }

            ie9Animation.openStack(openedStack, stack);
            openedStack = stack;
        } else {
            if (openedStack != null) {
                openedStack.close();
                openedStack.contentContainer.getElement().getStyle().clearHeight();
            }
            openedStack = stack;
            openedStack.open();
            openedStack.contentContainer.getElement().getStyle().setHeight(contentHeight, Style.Unit.PX);
        }

        if (fireEvents) {
            bus.fireEventFromSource(new StackOpenEvent(stack, stacks.indexOf(stack)), this);
        }
    }

    public List<SingleStack> getStacks() {
        return stacks;
    }

    public SingleStack getOpenedStack() {
        return openedStack;
    }

    @Override
    public HandlerRegistration addStackOpenHandler(StackOpenEvent.Handler handler) {
        return bus.addHandlerToSource(StackOpenEvent.getType(), this, handler);
    }

    /**
     * Тип панели. Черная или белая.
     */
    public static enum Type {
        WHITE, BLACK
    }
}
