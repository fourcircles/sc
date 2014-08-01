package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import kz.arta.synergy.components.client.input.tags.events.TagAddEvent;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
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
public class TagsPanel extends Composite{
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
    private ArrayList<Tag> tags;

    /**
     * Скрывать ли теги в индикатор
     */
    boolean hasIndicator;

    /**
     * Сдвиг справа. Используется когда индикатора нет и теги сдвигаются влево.
     */
    private int rightOffset;

    public TagsPanel(EventBus bus, int maxWidth) {
        this(bus, maxWidth, true);
    }

    public TagsPanel(EventBus bus, int maxWidth, boolean hasIndicator) {
        root = new FlowPanel();
        initWidget(root);

        this.maxWidth = maxWidth;
        this.hasIndicator = hasIndicator;

        indicator = new TagIndicator(bus);
        tags = new ArrayList<Tag>();

        Style style = getElement().getStyle();
        style.setPosition(Style.Position.RELATIVE);
        style.setPaddingRight(Constants.COMMON_INPUT_PADDING, Style.Unit.PX);
        style.setDisplay(Style.Display.INLINE_BLOCK);
        style.setWhiteSpace(Style.WhiteSpace.NOWRAP);

        TagAddEvent.register(bus, new TagAddEvent.Handler() {
            @Override
            public void onTagAdd(TagAddEvent event) {
                add(event.getTag());
            }
        });

        TagRemoveEvent.register(bus, new TagRemoveEvent.Handler() {
            @Override
            public void onTagRemove(TagRemoveEvent event) {
                tags.remove(event.getTag());
                root.remove(event.getTag());
                event.getTag().getElement().getStyle().clearRight();
                rebuild();
            }
        });
    }

    /**
     * Добавляет все теги в панель, если не влезают - убирает в индикатор количества тегов
     * или сдвигает влево
     */
    private void rebuild() {
        if (!hasIndicator) {
            root.clear();
            for (Tag tag : tags) {
                root.add(tag);
            }
            getElement().getStyle().clearWidth();
            if (getOffsetWidth() > maxWidth) {
                rightOffset = getOffsetWidth() - maxWidth - Constants.COMMON_INPUT_PADDING;
                getElement().getStyle().setWidth(maxWidth, Style.Unit.PX);
            } else {
                rightOffset = 0;
            }
            for (Tag tag : tags) {
                tag.getElement().getStyle().setRight(rightOffset, Style.Unit.PX);
            }
            return;
        }

        int i = 0;
        //пытаемся добавить теги начиная с тега на позиции i
        while (i <= tags.size()) {
            int totalWidth = 0;

            if (i > 0) {
                indicator.setText(i + "+");
                totalWidth += Utils.getTextWidth(indicator) + Constants.TAG_PADDING * 2;
            }

            for (int j = i; j < tags.size(); j++) {
                totalWidth += tags.get(j).getOffsetWidth();
                totalWidth += Constants.TAG_INTERVAL;
            }

            //убираем интервал между тегами, если отображен только индикатор
            if (i < tags.size() || tags.size() == 1) {
                totalWidth -= Constants.TAG_INTERVAL;
            }

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

    public boolean isHasIndicator() {
        return hasIndicator;
    }

    public void setHasIndicator(boolean hasIndicator) {
        this.hasIndicator = hasIndicator;
    }
}
