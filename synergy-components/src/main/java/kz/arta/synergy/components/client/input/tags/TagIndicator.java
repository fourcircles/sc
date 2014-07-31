package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.PPanel;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 11:56
 *
 * Индикатор количества скрытых тегов
 */
public class TagIndicator extends Composite implements ArtaHasText {
    /**
     * Максимальное количество тегов при котором нет вертикального скролла
     */
    public static final int MAX_TAG = 8;

    /**
     * Корневая панель попапа
     */
    private FlowPanel popupRootPanel;

    /**
     * Индикатор
     */
    private Label label;

    /**
     * Попап индикатора
     */
    private PopupPanel popupPanel;

    /**
     * Ширина самого широкого тега
     */
    private int maxTagWidth;

    private EventBus bus;

    private PriorityQueue<Tag<?>> tags;

    public TagIndicator(EventBus bus) {
        label = new Label();
        initWidget(label);
        label.setStyleName(SynergyComponents.resources.cssComponents().tag());
        label.addStyleName(getFontStyle());
        label.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        label.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);

        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (popupPanel.isShowing()) {
                    hide();
                } else {
                    show();
                }
            }
        });

        tags = new PriorityQueue<Tag<?>>(10, new Comparator<Tag<?>>() {
            @Override
            public int compare(Tag<?> tag1, Tag<?> tag2) {
                return - tag1.getOffsetWidth() + tag2.getOffsetWidth();
            }
        });

        popupPanel = new PopupPanel(true);
        popupPanel.addAutoHidePartner(label.getElement());

        popupRootPanel = new FlowPanel();
        popupRootPanel.getElement().getStyle().setPadding(6, Style.Unit.PX);

        ArtaScrollPanel vScroll = new ArtaScrollPanel(popupRootPanel);

        popupPanel.setWidget(vScroll);

        popupPanel.setStyleName(SynergyComponents.resources.cssComponents().tagIndicator());

        TagRemoveEvent.register(bus, new TagRemoveEvent.Handler() {
            @Override
            public void onTagRemove(TagRemoveEvent event) {
                if (tags.contains(event.getTag())) {
                    tags.remove(event.getTag());

                    popupPanel.setHeight(getPopupHeight(tags.size()) + "px");
                }
                if (popupRootPanel.getWidgetCount() == 0) {
                    popupPanel.hide();
                }
            }
        });
    }

    /**
     * Возвращает высоту попапа в зависимости от количества тегов
     * @param widgetCount количество тегов
     * @return высота
     */
    private int getPopupHeight(int widgetCount) {
        widgetCount = Math.min(MAX_TAG, widgetCount);
        return widgetCount * (Constants.TAG_HEIGHT + Constants.TAG_INTERVAL) + 12 - Constants.TAG_INTERVAL;
    }

    /**
     * Задает ширину попапа включая отступы и скроллбар (если он присутствует)
     * @param width внутренняя ширина попапа
     */
    private void setPopupWidth(int width) {
        popupPanel.setWidth(width + 12 + (tags.size() > MAX_TAG ? 17 : 0) + "px");
    }

    /**
     * Показывает попап под индикатором
     */
    public void show() {
        popupPanel.setHeight(getPopupHeight(popupRootPanel.getWidgetCount()) + "px");
        popupPanel.setWidth(tags.peek().getOffsetWidth() + 12 + (tags.size() > MAX_TAG ? 17 : 0) + "px");

        int labelWidth = Utils.getTextWidth(label.getText(), getFontStyle()) + Constants.COMMON_INPUT_PADDING;

        popupPanel.showRelativeTo(label);
        int width = popupPanel.getOffsetWidth();

        if (popupPanel.getAbsoluteLeft() + (double) labelWidth / 2 - width / 2 < 0) {
            //треугольник слева
            popupPanel.removeStyleName(SynergyComponents.resources.cssComponents().rightIndicator());
            popupPanel.removeStyleName(SynergyComponents.resources.cssComponents().centerIndicator());
            popupPanel.addStyleName(SynergyComponents.resources.cssComponents().leftIndicator());
        } else if (popupPanel.getAbsoluteLeft() + (double) labelWidth / 2 + width / 2 > Window.getClientWidth()) {
            //треугольник справа
            popupPanel.removeStyleName(SynergyComponents.resources.cssComponents().leftIndicator());
            popupPanel.removeStyleName(SynergyComponents.resources.cssComponents().centerIndicator());
            popupPanel.addStyleName(SynergyComponents.resources.cssComponents().rightIndicator());
        } else {
            //треугольник посередине
            popupPanel.removeStyleName(SynergyComponents.resources.cssComponents().leftIndicator());
            popupPanel.removeStyleName(SynergyComponents.resources.cssComponents().rightIndicator());
            popupPanel.addStyleName(SynergyComponents.resources.cssComponents().centerIndicator());
            popupPanel.setPopupPosition((int) (popupPanel.getAbsoluteLeft() + (double) labelWidth / 2 - (double) width / 2),
                    popupPanel.getAbsoluteTop() - 7);
        }
    }

    /**
     * Скрывает индикатор
     */
    public void hide() {
        popupPanel.hide();
    }

    /**
     * Добавляет тег в индикатор.
     * Используется для добавления уже существующего тега.
     * @param tag тег
     */
    public void add(final Tag<?> tag) {
        maxTagWidth = Math.max(maxTagWidth, tag.getOffsetWidth());
        tags.add(tag);
        PPanel ptag = new PPanel();
        ptag.setWidget(tag);
        popupRootPanel.add(ptag);
    }

    /**
     * Добавляет все теги из списка
     * @param tags список тегов
     */
    public void addAll(List<Tag> tags) {
        for (Tag tag : tags) {
            add(tag);
        }
    }

    /**
     * Удаляет все теги
     */
    public void clear() {
        maxTagWidth = 0;
        tags.clear();
        popupRootPanel.clear();
    }

    @Override
    public String getFontStyle() {
        return SynergyComponents.resources.cssComponents().mainText();
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }

    @Override
    public int getOffsetWidth() {
        return Utils.getTextWidth(this) + Constants.TAG_PADDING * 2;
    }

}
