package kz.arta.synergy.components.client.input.tags;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import kz.arta.synergy.components.client.SynergyComponents;
import kz.arta.synergy.components.client.input.tags.events.*;

/**
 * User: vsl
 * Date: 25.07.14
 * Time: 11:56
 *
 * Индикатор количества скрытых тегов
 */
public class TagIndicator implements HasTagRemoveEventHandler {

    /**
     * Корневая панель
     */
    private FlowPanel root;

    private PopupPanel popupPanel;

    /**
     * Последний тег в индикаторе у которого не должно быть нижнего отступа
     */
    private Tag lastTag;

    private HandlerManager handlerManager;

    public TagIndicator() {
        handlerManager = new HandlerManager(this);

        popupPanel = new PopupPanel(true);

        root = new FlowPanel();
        popupPanel.setWidget(root);

        popupPanel.addStyleName(SynergyComponents.resources.cssComponents().tagIndicator());
    }

    /**
     * Показывает индикатор под объектом
     */
    public void showRelativeTo(UIObject o) {
        popupPanel.showRelativeTo(o);
    }

    /**
     * Скрывает индикатор
     */
    public void hide() {
        popupPanel.hide();
    }

    /**
     * Класс для тега <p>, в который добавляется каждый тег.
     */
    private class PPanel extends SimplePanel {
        public PPanel() {
            super(Document.get().createPElement());
        }
    }

    /**
     * Добавляет тег в индикатор.
     * Используется для добавления уже существующего тега.
     * @param tag тег
     */
    public void addTag(final Tag tag) {
        tag.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
        if (lastTag != null) {
            lastTag.getElement().getStyle().setMarginBottom(2, Style.Unit.PX);
        }

        PPanel ptag = new PPanel();
        ptag.setWidget(tag);
        root.add(ptag);

        lastTag = tag;
        lastTag.getElement().getStyle().setMarginBottom(0, Style.Unit.PX);
    }

    /**
     * Создает и добавляет тег с текстом из аргумента
     * @param text текст для тега
     * @return созданный тег
     */
    public Tag addTag(String text) {
        Tag tag = new Tag(text);
        addTag(tag);
        return tag;
    }

    @Override
    public HandlerRegistration addTagRemoveHandler(TagRemoveEvent.Handler handler) {
        return handlerManager.addHandler(TagRemoveEvent.TYPE, handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

//    public void removeTag(Tag tag) {
//        tag.removeFromParent();
//        root.remove(tag.getParent());
//        handlerManager.fireEvent(new TagRemoveEvent(tag));
//    }
}
