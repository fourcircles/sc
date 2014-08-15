package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.SimplePanel;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.menu.events.ListSelectionEvent;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.ArrayList;

/**
 * User: vsl
 * Date: 30.07.14
 * Time: 12:16
 *
 * Панель для тегов
 * Управляется через события {@link TagAddEvent}, {@link TagRemoveEvent}
 */
public class TagsPanel extends Composite implements HasEnabled{

    /**
     * Панель которая содержит корневую панель и позволяет
     * прятать сдвинутые теги.
     * Это освобождает родительский элемент от необходимости задавать
     * overflow: hidden.
     */
    private SimplePanel container;

    /**
     * Корневая панель
     */
    private FlowPanel root;

    /**
     * Максимальная ширина панели, все теги не влезают, то они
     * либо сдвигаются либо добавляются в попап индикатора
     */
    private int maxWidth;

    /**
     * Индикатор скрытых тегов
     */
    private TagIndicator indicator;

    /**
     * Список добавленых тегов
     */
    private ArrayList<Tag<?>> tags;

    public TagsPanel(final EventBus bus, int maxWidth) {
        container = new SimplePanel();
        initWidget(container);

        container.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        container.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        container.getElement().getStyle().setTop(0, Style.Unit.PX);
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            container.getElement().getStyle().setRight(0, Style.Unit.PX);
            container.getElement().getStyle().setPaddingLeft(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
        } else {
            container.getElement().getStyle().setLeft(0, Style.Unit.PX);
            container.getElement().getStyle().setPaddingRight(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
        }
        container.setHeight("100%");

        root = new FlowPanel();
        container.setWidget(root);

        this.maxWidth = maxWidth;

        indicator = new TagIndicator(bus);
        tags = new ArrayList<Tag<?>>();

        Style rootStyle = root.getElement().getStyle();
        rootStyle.setPosition(Style.Position.RELATIVE);
        rootStyle.setTop(2, Style.Unit.PX);

        rootStyle.setDisplay(Style.Display.INLINE_BLOCK);
        rootStyle.setWhiteSpace(Style.WhiteSpace.NOWRAP);

        TagAddEvent.register(bus, new TagAddEvent.Handler() {
            @Override
            public void onTagAdd(TagAddEvent event) {
                add(event.getTag());
            }
        });

        TagRemoveEvent.register(bus, new TagRemoveEvent.Handler() {
            @Override
            public void onTagRemove(TagRemoveEvent event) {
                removeTag(event.getTag());
            }
        });
    }

    public boolean contains(Tag<?> tag) {
        return tags.contains(tag);
    }

    /**
     * Удаляет тег
     * @param tag тег
     */
    private void removeTag(Tag<?> tag) {
        tags.remove(tag);
        root.remove(tag);
        rebuild();
    }

    /**
     * Добавляет все теги в панель, если не влезают - убирает в индикатор количества тегов
     * или сдвигает влево
     */
    private void rebuild() {
        int i = 0;
        //теги [i..i-1] скрываем в индикатор, остальные пытаемся разместить
        while (i <= tags.size()) {
            //правый отступ панели тегов
            int totalWidth = Constants.COMMON_INPUT_PADDING;

            if (i > 0) {
                indicator.setText(i + "+");
                totalWidth += Utils.getTextWidth(indicator) + Constants.TAG_PADDING * 2 + Constants.TAG_INTERVAL;
            }

            for (int j = i; j < tags.size(); j++) {
                totalWidth += tags.get(j).getOffsetWidth();
                totalWidth += Constants.TAG_INTERVAL;
            }

            totalWidth += Constants.COMMON_INPUT_PADDING;

            if (totalWidth <= maxWidth) {
                break;
            }
            i++;
        }

        indicator.clear();
        root.clear();
        if (i > 0) {
            indicator.addAll(tags.subList(0, i));

            root.add(indicator);
        } else {
            indicator.hide();
        }

        for (int j = i; j < tags.size(); j++) {
            root.add(tags.get(j));
        }
        container.setWidth(root.getOffsetWidth() + "px");
    }

    private void add(Tag tag) {
        tags.add(tag);
        rebuild();
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    /**
     * Сдвигает теги налево на заданную величину в пикселях. Направление сдвига зависит от локали.
     * @param offset сдвиг в пикселях
     */
    public void setOffset(int offset) {
        if (LocaleInfo.getCurrentLocale().isRTL()) {
            root.getElement().getStyle().setLeft(offset, Style.Unit.PX);
        } else {
            root.getElement().getStyle().setRight(offset, Style.Unit.PX);
        }
        container.setWidth(Math.max(0, root.getOffsetWidth() - offset) + "px");
    }

    /**
     * Убирает сдвиг если он был
     */
    public void clearOffset() {
        root.getElement().getStyle().clearRight();
        root.getElement().getStyle().clearLeft();

        container.setWidth(root.getOffsetWidth() + "px");
    }

    /**
     * Возвращает всех тегов и индикатора
     * @return ширина
     */
    public int getTagsWidth() {
        return root.getOffsetWidth() + Constants.COMMON_INPUT_PADDING;
    }

    @Override
    public boolean isEnabled() {
        return indicator.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (Tag tag: tags) {
            tag.setEnabled(enabled);
        }
        indicator.setEnabled(enabled);
    }
}
