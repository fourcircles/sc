package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.tags.events.TagRemoveEvent;
import kz.arta.synergy.components.client.util.ArtaHasText;
import kz.arta.synergy.components.client.util.Utils;
import kz.arta.synergy.components.style.client.Constants;

import java.util.List;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 11:56
 *
 * Индикатор количества скрытых тегов
 */
public class TagIndicator extends Composite implements ArtaHasText {

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

    private EventBus bus;

    public TagIndicator(EventBus bus) {
        label = new Label();
        initWidget(label);
        label.setStyleName(SynergyComponents.resources.cssComponents().tag());
        label.addStyleName(getFontStyle());
        label.getElement().getStyle().setCursor(Style.Cursor.POINTER);

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

        popupPanel = new PopupPanel(true);
        popupPanel.addAutoHidePartner(label.getElement());

        popupRootPanel = new FlowPanel();
        popupPanel.setWidget(popupRootPanel);

        popupPanel.addStyleName(SynergyComponents.resources.cssComponents().tagIndicator());

        TagRemoveEvent.register(bus, new TagRemoveEvent.Handler() {
            @Override
            public void onTagRemove(TagRemoveEvent event) {
                popupRootPanel.remove(event.getTag().getParent());
                if (popupRootPanel.getWidgetCount() == 0) {
                    popupPanel.hide();
                }
            }
        });
    }

    /**
     * Показывает попап под индикатором
     */
    public void show() {
        popupPanel.showRelativeTo(label);
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
    public void add(final Tag tag) {
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

    /**
     * Класс для тега <p>, в который добавляется каждый тег.
     */
    private class PPanel extends SimplePanel {
        public PPanel() {
            super(Document.get().createPElement());
        }
    }
}
