package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.scroll.ArtaScrollPanel;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Navigator;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.*;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 11:56
 *
 * Индикатор количества скрытых тегов
 */
public class TagIndicator<V> extends Composite implements ArtaHasText, HasEnabled {
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
     * Включен/выключен
     */
    private boolean isEnabled = true;

    private List<Tag<V>> tags;

    public TagIndicator(EventBus bus) {
        label = GWT.create(Label.class);
        initWidget(label);
        label.setStyleName(SynergyComponents.resources.cssComponents().tag());
        label.addStyleName(getFontStyle());
        label.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        label.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);

        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isEnabled()) {
                    if (popupPanel.isShowing()) {
                        hide();
                    } else {
                        show();
                    }
                }
            }
        });

        tags = new ArrayList<Tag<V>>();

        popupPanel = GWT.create(PopupPanel.class);
        popupPanel.setAutoHideEnabled(true);

        popupPanel.addAutoHidePartner(label.getElement());

        popupRootPanel = new FlowPanel();
        popupRootPanel.addStyleName(SynergyComponents.resources.cssComponents().tagIndicatorContent());

        ArtaScrollPanel vScroll = new ArtaScrollPanel(popupRootPanel);

        popupPanel.setWidget(vScroll);
        vScroll.removeHorizontalScrollbar();

        popupPanel.setStyleName(SynergyComponents.resources.cssComponents().tagIndicator());

        TagRemoveEvent.register(bus, new TagRemoveEvent.Handler() {
            @Override
            public void onTagRemove(TagRemoveEvent event) {
                if (tags.contains(event.getTag())) {
                    tags.remove(event.getTag());

                    popupPanel.setHeight(getPopupHeight() + "px");
                }
                if (popupRootPanel.getWidgetCount() == 0) {
                    popupPanel.hide();
                }
            }
        });

        if (Navigator.isChrome() && LocaleInfo.getCurrentLocale().isRTL()) {
            popupRootPanel.getElement().getStyle().setLeft(15, Style.Unit.PX);
            popupRootPanel.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        }
    }

    /**
     * Возвращает высоту попапа в зависимости от количества тегов
     * @return высота
     */
    private int getPopupHeight() {
        int widgetCount = Math.min(MAX_TAG, tags.size());
        return widgetCount * (Constants.TAG_HEIGHT + Constants.TAG_INTERVAL) + Constants.TAG_INDICATOR_PADDING * 2 - Constants.TAG_INTERVAL;
    }

    /**
     * Показывает попап под индикатором
     */
    public void show() {
        if (tags.isEmpty()) {
            return;
        }

        popupPanel.setHeight(getPopupHeight() + "px");

        //самый широкий тег надо находить именно перед появлением попапа
        // т. к. ширина тегов может меняться, то решения типа PriorityQueue не подходят
        Tag<V> widestTag = Collections.max(tags, new Comparator<Tag<V>>() {
            @Override
            public int compare(Tag<V> tag1, Tag<V> tag2) {
                return tag1.getWidth() - tag2.getWidth();
            }
        });
        int width = widestTag.getWidth();
        width += Constants.TAG_INDICATOR_PADDING * 2 + (tags.size() > MAX_TAG ? Constants.STD_SCROLL_WIDTH : 0);
        popupPanel.setWidth(width + "px");

        int labelWidth = Utils.getTextWidth(label.getText(), getFontStyle()) + Constants.COMMON_INPUT_PADDING;

        popupPanel.showRelativeTo(label);

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
            double offset = ((double) labelWidth - width) / 2;
            if (LocaleInfo.getCurrentLocale().isRTL()) {
                offset = -offset;
            }
            popupPanel.setPopupPosition((int) (popupPanel.getAbsoluteLeft() + offset), popupPanel.getAbsoluteTop() - 7);
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
    public void add(final Tag<V> tag) {
        tags.add(tag);
        tag.setMaxWidth(Constants.INDICATOR_TAG_MAX_WIDTH);
        popupRootPanel.add(tag);
    }

    /**
     * Добавляет все теги из списка
     * @param tags список тегов
     */
    public void addAll(List<Tag<V>> tags) {
        for (Tag<V> tag : tags) {
            add(tag);
        }
    }

    /**
     * Удаляет все теги
     */
    public void clear() {
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

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;

        Style.Cursor cursor;
        if (enabled) {
            cursor = Style.Cursor.POINTER;
        } else {
            cursor = Style.Cursor.DEFAULT;
        }
        getElement().getStyle().setCursor(cursor);
    }
}
